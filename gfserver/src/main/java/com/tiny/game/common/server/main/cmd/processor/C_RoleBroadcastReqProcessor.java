package com.tiny.game.common.server.main.cmd.processor;

import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.broadcast.BroadcastService;

import game.protocol.protobuf.GameProtocol.C_RoleBroadcastReq;

@NetCmdAnnimation(cmd = C_RoleBroadcastReq.class)
public class C_RoleBroadcastReqProcessor extends NetCmdProcessor {

	@Override
	public void process(NetSession session, NetMessage msg) {
		C_RoleBroadcastReq req = NetUtils.getNetProtocolObject(C_RoleBroadcastReq.PARSER, msg);
		BroadcastService.broadcastToRole(req.getRoleId(), req.getMsgName(), req.getMsgContent().toByteArray());
	}
	
}
