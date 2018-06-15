package com.tiny.game.common.net;

import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;

import game.protocol.protobuf.GameProtocol.S_LoginServerInfo;

@NetCmdAnnimation(cmd = S_LoginServerInfo.class)
public class S_LoginServerInfoProcessor extends NetCmdProcessor {

	@Override
	public void process(NetSession session, NetMessage msg) {
		S_LoginServerInfo req = NetUtils.getNetProtocolObject(S_LoginServerInfo.PARSER, msg);
		
		try {
			NetTestCallback.getInstance().onGotS_LoginServerInfo(req);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
