package com.tiny.game.common.server.proxy.cmd.processor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.broadcast.RouterService;

import game.protocol.protobuf.GameProtocol.C_ProxyBroadcastReq;

@NetCmdAnnimation(cmd = C_ProxyBroadcastReq.class)
public class C_ProxyBroadcastReqProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_ProxyBroadcastReqProcessor.class);
	
	@Override
	public void process(NetSession session, NetMessage msg) {
		C_ProxyBroadcastReq req = NetUtils.getNetProtocolObject(C_ProxyBroadcastReq.PARSER, msg);
		String specifiedTarget = req.getFinalTargetClientType();
		
		if(ServerContext.getInstance().getServerUniqueTag().equals(req.getTargetServerTag())) {
			logger.info("Proxy router: " +req.getMsgName() +" arrived final route target: " + req.getTargetServerTag());
			if(StringUtils.isNotEmpty(specifiedTarget)) {
				logger.info("Proxy router: " +req.getMsgName() +" route to role: " + specifiedTarget);
				RouterService.routeToRole(specifiedTarget, req.getMsgName(), req.getMsgContent().toByteArray());
			} else {
				NetLayerManager.getInstance().processNetMessage(session, msg);
			}
		} else {
			NetSession routerSession = null;
			boolean usePeerUniqueIdRouter = StringUtils.isNotEmpty(specifiedTarget);
			if(usePeerUniqueIdRouter) {
				routerSession = NetSessionManager.getInstance().getSessionByPeerUniqueId(req.getTargetServerTag());
			} else {
				routerSession = NetSessionManager.getInstance().getRandomSessionByPeerType(req.getTargetServerTag());
			}

			if(routerSession!=null) {
				logger.info("Proxy router: " +req.getMsgName() +", "+session.getPeerUniqueId() + "==>" + routerSession.getPeerUniqueId());
				
				NetLayerManager.getInstance().asyncSendOutboundMessage(routerSession, req); //new NetCmd(req.getMsgName(), req.getMsgContent().toByteArray())
			} else {
				logger.error("TODO: proxy message re-send to target(not found target active session): " + req.getTargetServerTag());
			}
		}
	}
	
}
