package com.tiny.game.common.server.fight.bizlogic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.GameConst;

import game.protocol.protobuf.FightProtocol.FightEnterData;

public class FightContext {

	private int replayVideoId;
	private int currentVideoWatchRoleId;
	
	private FightEnterData enterData = null;
	
	private boolean hasPlayerDisconnected = false;
	
	private WarRandom random = null;
	
	public FightContext(FightEnterData enterData, long seed) {
		this.enterData = enterData;
		random = new WarRandom(seed); // make the seed fixed for one battle
	}
	
	public boolean isNewUserGuide() {
		return enterData.getMatchType() == GameConst.MatchType_Train;
	}
	
	public boolean isPVE() {
		return enterData.getMatchType() == GameConst.MatchType_PVE;
	}

	public boolean isGVE() {
		return enterData.getMatchType() == GameConst.MatchType_GVE;
	}
	public WarRandom getWarRandom() {
		return random;
	}

	public boolean isHasPlayerDisconnected() {
		return hasPlayerDisconnected;
	}

	public void setHasPlayerDisconnected(boolean hasPlayerDisconnected) {
		this.hasPlayerDisconnected = hasPlayerDisconnected;
	}
	
}
