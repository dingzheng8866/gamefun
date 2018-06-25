package com.tiny.game.common.server.main.cmd.processor.alliance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.alliance.AllianceService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_CreateAlliance;

@NetCmdAnnimation(cmd = C_CreateAlliance.class)
public class C_CreateAllianceProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_CreateAlliance req = NetUtils.getNetProtocolObject(C_CreateAlliance.PARSER, msg);
		AllianceService.createAlliance(role, session, req);
	}
	
}
