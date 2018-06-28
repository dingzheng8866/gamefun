package com.tiny.game.common.server.main.cmd.processor.role;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_MakeAsFriend;

@NetCmdAnnimation(cmd = C_MakeAsFriend.class)
public class C_MakeAsFriendProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_MakeAsFriend req = NetUtils.getNetProtocolObject(C_MakeAsFriend.PARSER, msg);
		RoleService.makeAsFriend(role, session, req);
	}
	
}