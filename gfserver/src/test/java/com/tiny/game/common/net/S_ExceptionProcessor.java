package com.tiny.game.common.net;

import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;

import game.protocol.protobuf.GameProtocol.S_Exception;

@NetCmdAnnimation(cmd = S_Exception.class)
public class S_ExceptionProcessor extends NetCmdProcessor {

	@Override
	public void process(NetSession session, NetMessage msg) {
		S_Exception req = NetUtils.getNetProtocolObject(S_Exception.PARSER, msg);
		System.out.println("===============================Receive Server Exception====================================");
		System.out.println(req.getTrace().replaceAll("\r\n", "\n"));
		System.out.println(req);
	}
}
