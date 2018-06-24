package com.tiny.game.common.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.tiny.game.common.GameConst;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.GameRuntimeException;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.cmd.NetCmdProcessorFactory;
import com.tiny.game.common.net.netty.NetChannelInboundMessageHandler;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.net.netty.NetSessionFilter;
import com.tiny.game.common.net.netty.NetSessionHandler;
import com.tiny.game.common.net.netty.NetSessionIpFilter;
import com.tiny.game.common.net.netty.NetSessionManagerFilter;
import com.tiny.game.common.net.server.NetServer;
import com.tiny.game.common.server.ContextParameter;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.broadcast.RouterService;
import com.tiny.game.common.server.fight.FightServer;
import com.tiny.game.common.server.gate.GateServer;
import com.tiny.game.common.server.main.MainGameServer;
import com.tiny.game.common.server.main.bizlogic.role.RoleSessionService;
import com.tiny.game.common.util.GameUtil;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.I_RouteMessage;
import game.protocol.protobuf.GameProtocol.S_Exception;
import io.netty.channel.ChannelHandler;


public class NetLayerManager {

	public static final String DEF_NET_CMD_PROCESSOR = "com.tiny.game";
	
	private static final Logger logger = LoggerFactory.getLogger(NetLayerManager.class);
	
	public static ScheduledExecutorService outboundThreadPool = null;
	private List<NetServer> netServers = new ArrayList<NetServer>();
	
	private static NetLayerManager instance = new NetLayerManager();
	private NetLayerManager() {
	}

	public static NetLayerManager getInstance() {
		return instance;
	}
	
	public void init(ServerContext ctx) {
		if(outboundThreadPool == null) {
			int threads = Integer.parseInt(ctx.getProperty(ContextParameter.NET_OUTBOUND_THREADS, "4"));
			outboundThreadPool = new ScheduledThreadPoolExecutor(threads);
		}
		initNetCmdProcessors(ctx);
		
		if(netServers.size() < 1) {
			int listenPort = ctx.getPropertyInt(ContextParameter.NET_SERVER_LISTEN_PORT, "8888");
			if(listenPort > 0) {
				NetServer netServer = buildNetServer(ctx, listenPort);
				try {
					netServer.start();
				} catch (Exception e) {
					throw new RuntimeException("Failed to start net server: "+e.getMessage(), e);
				}
				netServers.add(netServer);
			}
		}
		
//		// TEST:
//		NetClientManager.getInstance().addConnectTarget(GateServer.class.getSimpleName(), "127.0.0.1", 7777);
//		
	}
	
	public void clear() {
		if(outboundThreadPool!=null) {
			outboundThreadPool.shutdown();
		}
		
		for(NetServer server: netServers) {
			server.shutdown();
		}
		netServers.clear();
		
	}
	
	public void processNetMessage(NetSession session, I_RouteMessage req) {
		try {
			handleNetMessageByProcessor(session, new NetMessage(req.getMsgName(), req.getMsgContent().toByteArray()));
		} catch(Exception e) {
			logger.error("process route msg "+req.getMsgName()+" error: "+e.getMessage(), e);
			if(ServerContext.getInstance().getServerUniqueTag().equals(req.getOriginalFromServerUniqueTag())) {
				NetSession roleSession = RoleSessionService.getRoleSession(req.getFinalRouteToRoleId());
				if(roleSession!=null){
					asyncSendOutboundMessage(roleSession, NetMessageUtil.buildS_Exception(e));
				} else {
					logger.info("Drop message for role "+req.getFinalRouteToRoleId()+"is not on line on server: " + req.getOriginalFromServerUniqueTag() + " now to I_RouteMessage: " + req.getMsgName());
				}
			} else {
				S_Exception exp = NetMessageUtil.buildS_Exception(e);
				
				if(StringUtils.isEmpty(req.getFinalRouteToRoleId())) {
					for(String targetRoleId : req.getBackupFinalRouteToRoleIdList()) {
						I_RouteMessage.Builder routeMsg = NetMessageUtil.buildRouteMessage(new NetCmd(exp), req.getOriginalFromServerUniqueTag(), false, targetRoleId, req.getOriginalFromServerUniqueTag());
						RouterService.routeToTarget(routeMsg.build());
					}
				} else {
					I_RouteMessage.Builder routeMsg = NetMessageUtil.buildRouteMessage(new NetCmd(exp), req.getOriginalFromServerUniqueTag(), false, req.getFinalRouteToRoleId(), req.getOriginalFromServerUniqueTag());
					RouterService.routeToTarget(routeMsg.build());
				}
			}
		}
	}
	
	public void processNetMessage(NetSession session, NetMessage msg) { // direct message
		try {
			handleNetMessageByProcessor(session, msg);
		} catch(Exception e) {
			logger.error("process msg "+msg.getName()+" error: "+e.getMessage(), e);
			Role role = session.getPlayerRole();
			if(role!=null || ServerContext.getInstance().getGameServer() instanceof GateServer
					|| ServerContext.getInstance().getGameServer() instanceof MainGameServer
					|| ServerContext.getInstance().getGameServer() instanceof FightServer) {
				asyncSendOutboundMessage(session, NetMessageUtil.buildS_Exception(e));
			}
		}
	}
	
	private void handleNetMessageByProcessor(NetSession session, NetMessage msg) {
		String msgName = msg.getName();
		
		NetCmdProcessor processor = getNetCmdProcessor(msgName); 
		if (processor == null) {
			throw new InternalBugException("Not found net message processor of msg:"+ msgName);
		}
		
		if (!processor.isEnable()) {
			throw new InternalBugException("Message processor is disabled!:"+ msgName);
		} else {
			// before process, send back ack
			// TODO: fix it later about drop/reconnect
//			NetCmdFactory.factoryCmdAck(msgName).syncExecuteOnRouter(session, false);
			processor.process(session, msg);
		} 
	}
	
	
	private NetServer buildNetServer(ServerContext ctx, int port) {
		List<String> disableIps = GameUtil.splitToStringList(ctx.getProperty(ContextParameter.NET_DISABLE_IPS, ""), ",");
		
		List<NetSessionFilter> filters = new ArrayList<NetSessionFilter>();
		NetSessionIpFilter filter = new NetSessionIpFilter();
		filter.setDisableIps(disableIps);
		filters.add(filter);
		
		filters.add(new NetSessionManagerFilter());
		
		NetSessionHandler sessionHandler = new NetSessionHandler();
		sessionHandler.setFilters(filters);
		
		NetChannelInboundMessageHandler channelHandler = new NetChannelInboundMessageHandler();
		channelHandler.setSessionHandler(sessionHandler);
		List<ChannelHandler> channelHandlers = new ArrayList<ChannelHandler>();
		channelHandlers.add(channelHandler);
		
		NetServer server = new NetServer(port, channelHandlers);
		return server;
	}
	
	private void initNetCmdProcessors(ServerContext ctx) {
		String paths = ctx.getProperty(ContextParameter.NET_PROCESSOR_PATHS, DEF_NET_CMD_PROCESSOR);
		String[] pathArray = paths.split(",");
		for(String path : pathArray) {
			if(path!=null && path.trim().length() >0) {
				NetCmdProcessorFactory.loadNetCmdProcessors(path);
			}
		}
	}
	
	public NetCmdProcessor getNetCmdProcessor(String cmd) {
		return NetCmdProcessorFactory.getNetCmdProcessor(cmd);
	}
	
	public void syncSendOutboundMessage(NetSession session, NetCmd msg, boolean flush) {
		System.out.println("About to send out bound message: " + msg.getName() + ", flush: " + flush);
		if(flush) {
			session.writeAndFlush(msg);
		} else {
			session.write(msg);
		}
	}
	
	public void asyncSendOutboundMessage(final NetSession session, final NetCmd msg) {
		if(outboundThreadPool!=null) {
			outboundThreadPool.execute(() -> {
				syncSendOutboundMessage(session, msg, true);
			});
		} else {
			syncSendOutboundMessage(session, msg, true);
		}
	}
	
	public void asyncSendOutboundMessage(NetSession session, GeneratedMessage msg) {
		asyncSendOutboundMessage(session, new NetCmd(msg));
	}
	
}
