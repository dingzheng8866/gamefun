package com.tiny.game.common.server.main.cmd.processor.alliance;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_DoAllianceHelp;

@NetCmdAnnimation(cmd = C_DoAllianceHelp.class)
public class C_DoAllianceHelpProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_DoAllianceHelp req = NetUtils.getNetProtocolObject(C_DoAllianceHelp.PARSER, msg);
//		AllianceService.approveJoinInAlliance(role, session, req);
		// TODO: FINISH ME
	}
	
}