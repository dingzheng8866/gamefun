package com.tiny.game.common.server.fight.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FightRoomStateEnded extends AbstractFightRoomState {

	private static final Logger logger = LoggerFactory.getLogger(FightRoomStateEnded.class);
	
	public FightRoomStateEnded(FightRoom room) {
		super(room);
		processRoomOver();
	}

	public State getState() {
		return FightRoomState.State.Ended;
	}

	@Override
	public boolean update() {
		return true;
	}
	
	private void processRoomOver() {
		logger.info("processRoomOver...");
	}
	
}
