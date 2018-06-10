package com.tiny.game.common.net.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;

import game.protocol.protobuf.GameProtocol.C_RegisterClient;


@NetCmdAnnimation(cmd = C_RegisterClient.class)
public class C_RegisterClientProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_RegisterClientProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		C_RegisterClient req = NetUtils.getNetProtocolObject(C_RegisterClient.PARSER, msg);
		
		NetSessionManager.getInstance().addSession(req, session);
		logger.info("C_RegisterClient: type: "+req.toString());
	}
	
}
