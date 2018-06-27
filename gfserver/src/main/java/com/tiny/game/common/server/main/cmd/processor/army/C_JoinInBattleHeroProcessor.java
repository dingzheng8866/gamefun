package com.tiny.game.common.server.main.cmd.processor.army;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.army.ArmyService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_JoinInBattleHero;

@NetCmdAnnimation(cmd = C_JoinInBattleHero.class)
public class C_JoinInBattleHeroProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_JoinInBattleHero req = NetUtils.getNetProtocolObject(C_JoinInBattleHero.PARSER, msg);
		ArmyService.joinInBattleHero(role, session, req);
	}
	
}