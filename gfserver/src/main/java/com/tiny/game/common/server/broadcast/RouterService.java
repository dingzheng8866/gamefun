package com.tiny.game.common.server.broadcast;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.GeneratedMessage;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.role.UserOnlineInfo;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.main.bizlogic.role.RoleSessionService;
import com.tiny.game.common.server.proxy.ProxyServer;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.I_RouteMessage;
import sun.security.krb5.internal.NetClient;


public class RouterService {

	private static final Logger logger = LoggerFactory.getLogger(RouterService.class);
	
	public static void directRouteToRole(String roleId, String msgName, byte[] msgContent) {
		directRouteToRole(roleId, new NetCmd(msgName, msgContent));
	}
	
	private static void directRouteToRole(String roleId, NetCmd cmd) {
		logger.info("Receive route to role message: " + cmd.getName() + " to " + roleId);
		NetSession session = RoleSessionService.getRoleSession(roleId);
		if(session!=null) {
			logger.info("Send route to role message: " + cmd.getName() + " to " + roleId);
			NetLayerManager.getInstance().asyncSendOutboundMessage(session, cmd);
		} else {
			logger.info("Not found net session to route to role message: " + cmd.getName() + " to " + roleId);
		}
	}
	
	public static void routeToRole(String roleId, GeneratedMessage gm) {
		UserOnlineInfo onlineInfo = DaoFactory.getInstance().getUserDao().getUserOnlineInfo(roleId);
		if(onlineInfo!=null) {
			if(onlineInfo.getLoginServerId().equals(ServerContext.getInstance().getServerUniqueTag())) {
				directRouteToRole(roleId, new NetCmd(gm));
			} else {
				I_RouteMessage.Builder rq = NetMessageUtil.buildRouteMessage(new NetCmd(gm), onlineInfo.getLoginServerId(), false, roleId, ServerContext.getInstance().getServerUniqueTag());
				routeToTarget(rq.build());
			}
		}
	}
	
	public static void routeToAllOnlineRoles(GeneratedMessage gm) {
		List<NetSession> sessions = RoleSessionService.getAllActiveSessions();
		for(NetSession ns : sessions) {
			NetLayerManager.getInstance().asyncSendOutboundMessage(ns, gm);
		}
	}
	
	public static void routeToTarget(I_RouteMessage req) {
//		if(ServerContext.getInstance().getGameServer() instanceof ProxyServer) {
//			NetSession proxyServerSession = NetSessionManager.getInstance().getRandomSessionByPeerType(ProxyServer.class.getSimpleName());
//			if(proxyServerSession!=null) {
//				NetLayerManager.getInstance().asyncSendOutboundMessage(proxyServerSession, req);
//				logger.info("Route proxy message: " + req.getMsgName() + " to " + req.getFinalTargetClientType());
//			} else {
//				throw new InternalBugException(ErrorCode.Error_NoActiveProxyServer.name() + " to proxy message: " + req.getMsgName());
//			}
//		} else {
			logger.info("Route proxy message: " + req);
			NetClientManager.getInstance().sendMsg(ProxyServer.class.getSimpleName(), req);
//		}
	}
	
}
