package com.tiny.game.common.server.main.cmd.processor.army;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.army.ArmyService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_SwitchJoinInBattleArmyId;

@NetCmdAnnimation(cmd = C_SwitchJoinInBattleArmyId.class)
public class C_SwitchJoinInBattleArmyIdProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_SwitchJoinInBattleArmyId req = NetUtils.getNetProtocolObject(C_SwitchJoinInBattleArmyId.PARSER, msg);
		ArmyService.switchJoinInBattleArmyId(role, session, req);
	}
	
}