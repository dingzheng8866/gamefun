package com.tiny.game.common.server.fight.room;

import com.tiny.game.common.server.fight.room.FightRoomState.State;

public class FightRoomStatePreparing extends AbstractFightRoomState {

	public FightRoomStatePreparing(FightRoom room) {
		super(room);
	}

	public State getState() {
		return FightRoomState.State.Preparing;
	}
	
	@Override
	public boolean update() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onPlayerEnter(String roleId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerLoad(String roleId, int progress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerReconnect(String roleId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerDisconnect(String roleId) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
