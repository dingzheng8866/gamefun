package com.tiny.game.common.server.proxy.cmd.processor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.broadcast.RouterService;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.I_RouteMessage;
import game.protocol.protobuf.GameProtocol.S_Exception;

@NetCmdAnnimation(cmd = I_RouteMessage.class)
public class I_RouteMessageProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(I_RouteMessageProcessor.class);
	
	@Override
	public void process(NetSession session, NetMessage msg) {
		I_RouteMessage req = NetUtils.getNetProtocolObject(I_RouteMessage.PARSER, msg);
		
		if(ServerContext.getInstance().getServerUniqueTag().equals(req.getTargetServerTag())) {
			logger.info("Proxy router: " +req.getMsgName() +" arrived final route target: " + req.getTargetServerTag());
			if(req.getMsgName().startsWith("S_")) { // direct response to user
				String roleId = req.getFinalRouteToRoleId();
				RouterService.routeToRole(roleId, req.getMsgName(), req.getMsgContent().toByteArray());
			} else {
				NetLayerManager.getInstance().processNetMessage(session, req);
			} 
		} else { // proxy server
			NetSession routerSession = null;
			if(req.getIsRandomServer()) {
				routerSession = NetSessionManager.getInstance().getRandomSessionByPeerType(req.getTargetServerTag());
			} else {
				routerSession = NetSessionManager.getInstance().getSessionByPeerUniqueId(req.getTargetServerTag());
			}

			if(routerSession!=null) {
				logger.info("Proxy router: " +req.getMsgName() +", "+session.getPeerUniqueId() + "==>" + routerSession.getPeerUniqueId());
				NetLayerManager.getInstance().asyncSendOutboundMessage(routerSession, req); 
			} else {
				logger.error("Not found target active session: " + req.getTargetServerTag()+", try to give error message back to router: " + req.getOriginalFromServerUniqueTag());
				// TODO: find final user id --> online server
				routerSession = NetSessionManager.getInstance().getSessionByPeerUniqueId(req.getOriginalFromServerUniqueTag());
				if(routerSession!=null) {
					S_Exception exp = NetMessageUtil.buildS_Exception(GameConst.Error_NoActiveServer, "No active server: " + req.getTargetServerTag(), "");
					I_RouteMessage.Builder routeMsg = NetMessageUtil.buildRouteMessage(new NetCmd(exp), req.getOriginalFromServerUniqueTag(), false, req.getFinalRouteToRoleId(), req.getOriginalFromServerUniqueTag());
					NetLayerManager.getInstance().asyncSendOutboundMessage(routerSession, routeMsg.build());
				}
			}
		}
	}
	
}
