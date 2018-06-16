package com.tiny.game.common.net;

import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;

import game.protocol.protobuf.GameProtocol.S_RoleData;

@NetCmdAnnimation(cmd = S_RoleData.class)
public class S_RoleDataProcessor extends NetCmdProcessor {

	@Override
	public void process(NetSession session, NetMessage msg) {
		S_RoleData req = NetUtils.getNetProtocolObject(S_RoleData.PARSER, msg);
		System.out.println("===============================Receive S_RoleData====================================");
		System.out.println(req);
	}
}
