package com.tiny.game.common.net;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.tiny.game.common.net.client.NetClient;
import com.tiny.game.common.net.client.NetClientManager;
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
import com.tiny.game.common.server.gate.GateServer;
import com.tiny.game.common.util.GameUtil;

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
	
	public void asyncSendOutboundMessage(NetSession session, NetCmd msg) {
		outboundThreadPool.execute(() -> {
			syncSendOutboundMessage(session, msg, true);
		});
	}
	
	public void asyncSendOutboundMessage(NetSession session, GeneratedMessage msg) {
		asyncSendOutboundMessage(session, new NetCmd(msg));
	}
	
}
