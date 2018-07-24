package com.tiny.game.common.server.fight.room;

public interface FightRoomState {

	enum State {
		Waiting, Preparing, Fighting, Ending, Ended
	}
	
	public State getState();
	
	public boolean update();
	
	public void onPlayerEnter(String roleId);
	
	public void onPlayerLoad(String roleId, int progress);
	
	public void onPlayerDisconnect(String roleId);
	
	public void onPlayerReconnect(String roleId);

}
