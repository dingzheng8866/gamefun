package com.tiny.game.common.server.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.main.bizlogic.role.RoleSessionService;
import com.tiny.game.common.server.proxy.ProxyServer;

import game.protocol.protobuf.GameProtocol.I_RouteMessage;
import sun.security.krb5.internal.NetClient;


public class RouterService {

	private static final Logger logger = LoggerFactory.getLogger(RouterService.class);
	
	public static void routeToRole(String roleId, String msgName, byte[] msgContent) {
		logger.info("Receive route to role message: " + msgName + " to " + roleId);
		NetSession session = RoleSessionService.getRoleSession(roleId);
		if(session!=null) {
			logger.info("Send route to role message: " + msgName + " to " + roleId);
			NetLayerManager.getInstance().asyncSendOutboundMessage(session, new NetCmd(msgName, msgContent));
		} else {
			logger.info("Not found net session to route to role message: " + msgName + " to " + roleId);
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
