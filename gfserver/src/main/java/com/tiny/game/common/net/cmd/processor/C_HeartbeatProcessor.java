package com.tiny.game.common.net.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;

import game.protocol.protobuf.GameProtocol.C_Heartbeat;


@NetCmdAnnimation(cmd = C_Heartbeat.class)
public class C_HeartbeatProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_HeartbeatProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		C_Heartbeat req = NetUtils.getNetProtocolObject(C_Heartbeat.PARSER, msg);
		
		logger.debug("C_Heartbeat: ", session.getRemoteAddress());
	}
	
}
