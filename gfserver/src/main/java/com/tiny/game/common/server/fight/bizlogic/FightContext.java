package com.tiny.game.common.server.fight.bizlogic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.GameConst;

import game.protocol.protobuf.FightProtocol.FightEnterData;

public class FightContext {

	private int matchType;
	private int vsX;
	private int vsY;
	private int stageId;
	
	private int replayVideoId;
	private int currentVideoWatchRoleId;
	
	private FightEnterData enterData = null;
	
	private WarRandom random = null;
	
	public FightContext(long seed) {
		random = new WarRandom(seed); // make the seed fixed for one battle
	}
	
	public boolean isNewUserGuide() {
		return matchType == GameConst.MatchType_Train;
	}
	
	public boolean isPVE() {
		return matchType == GameConst.MatchType_PVE;
	}

	public boolean isGVE() {
		return matchType == GameConst.MatchType_GVE;
	}
	public WarRandom getWarRandom() {
		return random;
	}
}
