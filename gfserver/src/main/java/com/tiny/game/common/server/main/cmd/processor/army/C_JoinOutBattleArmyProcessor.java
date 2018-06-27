package com.tiny.game.common.server.main.cmd.processor.army;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.army.ArmyService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_JoinOutBattleArmy;

@NetCmdAnnimation(cmd = C_JoinOutBattleArmy.class)
public class C_JoinOutBattleArmyProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_JoinOutBattleArmy req = NetUtils.getNetProtocolObject(C_JoinOutBattleArmy.PARSER, msg);
		ArmyService.joinOutBattleArmy(role, session, req);
	}
	
}