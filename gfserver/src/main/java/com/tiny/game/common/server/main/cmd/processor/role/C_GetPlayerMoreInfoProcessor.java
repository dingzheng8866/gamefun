package com.tiny.game.common.server.main.cmd.processor.role;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_GetPlayerMoreInfo;

@NetCmdAnnimation(cmd = C_GetPlayerMoreInfo.class)
public class C_GetPlayerMoreInfoProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_GetPlayerMoreInfo req = NetUtils.getNetProtocolObject(C_GetPlayerMoreInfo.PARSER, msg);
		RoleService.getPlayerMoreInfo(role, session, req);
	}
	
}