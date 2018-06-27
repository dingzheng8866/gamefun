package com.tiny.game.common.server.main.cmd.processor.role;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_GetPlayerBaseCityInfo;

@NetCmdAnnimation(cmd = C_GetPlayerBaseCityInfo.class)
public class C_GetPlayerBaseCityInfoProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_GetPlayerBaseCityInfo req = NetUtils.getNetProtocolObject(C_GetPlayerBaseCityInfo.PARSER, msg);
		RoleService.getPlayerBaseCityInfo(role, session, req);
	}
	
}
