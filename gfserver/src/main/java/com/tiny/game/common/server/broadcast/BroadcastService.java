package com.tiny.game.common.server.broadcast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.error.ErrorCode;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleSessionService;

import game.protocol.protobuf.GameProtocol.C_ProxyBroadcastReq;


public class BroadcastService {

	private static final Logger logger = LoggerFactory.getLogger(BroadcastService.class);
	
	public static void broadcastToRole(String roleId, String msgName, byte[] msgContent) {
		logger.info("Receive broadcast to role message: " + msgName + " to " + roleId);
		NetSession session = RoleSessionService.getRoleSession(roleId);
		if(session!=null) {
			logger.info("Send broadcast to role message: " + msgName + " to " + roleId);
			NetLayerManager.getInstance().asyncSendOutboundMessage(session, new NetCmd(msgName, msgContent));
		} else {
			logger.info("Not found net session to broadcast to role message: " + msgName + " to " + roleId);
		}
	}
	
	public static void broadcastToProxyServer(C_ProxyBroadcastReq req) {
		NetSession proxyServerSession = NetSessionManager.getInstance().getSession(""); // TODO: FINISH ME
		if(proxyServerSession!=null) {
			NetLayerManager.getInstance().asyncSendOutboundMessage(proxyServerSession, req);
			logger.info("Proxy message: " + req.getMsgName() + " to " + req.getFinalTargetClientType());
		} else {
			throw new InternalBugException(ErrorCode.Error_NoActiveProxyServer.name() + " to proxy message: " + req.getMsgName());
		}
	}
	
}
