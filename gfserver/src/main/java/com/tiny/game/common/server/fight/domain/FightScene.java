package com.tiny.game.common.server.fight.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.server.fight.bizlogic.WarTime;
import com.tiny.game.common.server.fight.room.FightRoom;

public class FightScene {
	
	private static final Logger logger = LoggerFactory.getLogger(FightScene.class);
	
	private FightRoom room = null;
	private WarTime warTime = null;
	
	public FightScene(FightRoom room) {
		this.room = room;
		//warServerInfo = new WarServerInfo(this);
		warTime = new WarTime(60);
	}
	
	public void init() {
		
	}
	
	public void updateFightTime() {
		warTime.update();
	}
	
	public void beforeUpdate() {
		
	}
	
	public void update() {
		beforeUpdate();
		
	}
	
	public void calculateFightEndResult() {
		
	}
	
	public boolean isNeedEnd() {
		return false;
	}
	
	
	
}
