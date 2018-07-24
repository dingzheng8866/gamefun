package com.tiny.game.common.server.fight.room;

public abstract class AbstractFightRoomState implements FightRoomState {

	protected FightRoom room;
	
	public AbstractFightRoomState(FightRoom room) {
		this.room = room;
	}
	
//	@Override
//	public void onPlayerEnter(String roleId) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPlayerLoad(String roleId, int progress) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPlayerReconnect(String roleId) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onPlayerDisconnect(String roleId) {
//		// TODO Auto-generated method stub
//		
//	}

}
