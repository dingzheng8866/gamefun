package com.tiny.game.common.server.main.cmd.processor.alliance;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.alliance.AllianceService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_GetAllianceDetail;

@NetCmdAnnimation(cmd = C_GetAllianceDetail.class)
public class C_GetAllianceDetailProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_GetAllianceDetail req = NetUtils.getNetProtocolObject(C_GetAllianceDetail.PARSER, msg);
		AllianceService.getAllianceDetailAlliance(role, session, req);
	}
	
}