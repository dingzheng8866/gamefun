package com.tiny.game.common.server.fight.room;

import game.protocol.protobuf.FightProtocol.C_WarFightAction;

public interface FightRoomState {

	enum State {
		Waiting(0), Preparing(1), Fighting(2), Ending(3), Ended(4);
		private int value = 0;
		private State(int v) {
			this.value = v;
		}
		public int getValue() {
			return value;
		}
	}
	
	public State getState();
	
	public boolean update();
	
	public void onPlayerEnter(String roleId);
	
	public void onPlayerLoad(String roleId, int progress);
	
	public void onPlayerDisconnect(String roleId);
	
	public void onPlayerReconnect(String roleId);

	public void onPlayerSyncFullData(String roleId);
	
	public void onPlayerOperAction(C_WarFightAction action);
	
}
