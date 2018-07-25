package com.tiny.game.common.server.fight.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FightRoomStateEnding extends AbstractFightRoomState {

	//private static final Logger logger = LoggerFactory.getLogger(FightRoomStateEnding.class);
	
	public FightRoomStateEnding(FightRoom room) {
		super(room);
	}

	public State getState() {
		return FightRoomState.State.Ending;
	}

	@Override
	public boolean update() {
		if(isStateTimeout(1000)) {
			room.changeState(new FightRoomStateEnded(room));
		}
		return false;
	}
	
}
