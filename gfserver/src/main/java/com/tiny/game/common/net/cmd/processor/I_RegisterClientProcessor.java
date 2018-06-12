package com.tiny.game.common.net.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;

import game.protocol.protobuf.GameProtocol.I_RegisterClient;


@NetCmdAnnimation(cmd = I_RegisterClient.class)
public class I_RegisterClientProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(I_RegisterClientProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		I_RegisterClient req = NetUtils.getNetProtocolObject(I_RegisterClient.PARSER, msg);
		
		NetSessionManager.getInstance().addSession(req, session);
		logger.info("I_RegisterClient: type: "+req.toString());
	}
	
}
