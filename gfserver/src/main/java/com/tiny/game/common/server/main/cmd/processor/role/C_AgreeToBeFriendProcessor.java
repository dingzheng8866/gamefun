package com.tiny.game.common.server.main.cmd.processor.role;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_AgreeToBeFriend;

@NetCmdAnnimation(cmd = C_AgreeToBeFriend.class)
public class C_AgreeToBeFriendProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_AgreeToBeFriend req = NetUtils.getNetProtocolObject(C_AgreeToBeFriend.PARSER, msg);
		RoleService.agreeToBeFriend(role, session, req);
	}
	
}