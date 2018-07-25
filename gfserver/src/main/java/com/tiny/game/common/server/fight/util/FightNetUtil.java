package com.tiny.game.common.server.fight.util;

import com.tiny.game.common.server.fight.domain.ActionParameters;

import game.protocol.protobuf.FightProtocol.C_WarFightAction;
import game.protocol.protobuf.FightProtocol.FightObjectParameter;

public class FightNetUtil {

	public static ActionParameters convertToActionParameter(C_WarFightAction msg) {
		ActionParameters p = new ActionParameters(msg.getActionName());
		for (FightObjectParameter op : msg.getParameterList()) {
			p.addParameter(op.getKey(), op);
		}
		return p;
	}
	
}
