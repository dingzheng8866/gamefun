package com.tiny.game.common.server.main.cmd.processor.alliance;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.alliance.AllianceService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_UpAllianceMemberTitle;

@NetCmdAnnimation(cmd = C_UpAllianceMemberTitle.class)
public class C_UpAllianceMemberTitleProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_UpAllianceMemberTitle req = NetUtils.getNetProtocolObject(C_UpAllianceMemberTitle.PARSER, msg);
		AllianceService.upAllianceMemberTitle(role, session, req);
	}
	
}