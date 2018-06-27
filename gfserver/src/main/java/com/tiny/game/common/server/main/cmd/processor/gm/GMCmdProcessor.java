package com.tiny.game.common.server.main.cmd.processor.gm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.gm.GMService;

import game.protocol.protobuf.GMProtocol.C_GMCmd;

@NetCmdAnnimation(cmd = C_GMCmd.class)
public class GMCmdProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(GMCmdProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		C_GMCmd req = NetUtils.getNetProtocolObject(C_GMCmd.PARSER, msg);
		GMService.doGmCmd(req.getRoleId(), req.getGmCmd(), req.getParameter());
		
//		S_HintInfo.Builder response = S_HintInfo.newBuilder();
//		response.setHintCode(value);
		// do not give any response back to gm
	}
	
}
