package com.tiny.game.common.server.fight.bizlogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WarScene {

	private static final Logger logger = LoggerFactory.getLogger(WarScene.class);
	
	private FightStage stage = null;
	
	public WarScene(FightStage stage) {
		this.stage = stage;
		//warServerInfo = new WarServerInfo(this);
	}
	
	public void init() {
		
	}
	
	public void start() {
		
	}
	
	public boolean update(WarTime time) {
		return false;
	}
	
}
