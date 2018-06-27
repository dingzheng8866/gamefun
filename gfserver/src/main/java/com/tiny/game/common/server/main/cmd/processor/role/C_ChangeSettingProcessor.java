package com.tiny.game.common.server.main.cmd.processor.role;


import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_ChangeSetting;

@NetCmdAnnimation(cmd = C_ChangeSetting.class)
public class C_ChangeSettingProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_ChangeSetting req = NetUtils.getNetProtocolObject(C_ChangeSetting.PARSER, msg);
		RoleService.changeSetting(role, session, req);
	}
	
}