package com.tiny.game.common.server.fight.room;

import com.tiny.game.common.server.fight.bizlogic.FightRole;
import com.tiny.game.common.server.fight.bizlogic.FightRoleStatus;

import game.protocol.protobuf.FightProtocol.S_FightStart;

public class FightRoomStatePreparing extends AbstractFightRoomState {

	public FightRoomStatePreparing(FightRoom room) {
		super(room);
	}

	public State getState() {
		return FightRoomState.State.Preparing;
	}
	
	@Override
	public boolean update() {
		if(isStateTimeout(20000)) {
			changeToFightingState(true);
		}
		return false;
	}

	@Override
	public void onPlayerLoad(String roleId, int progress) {
		super.onPlayerLoad(roleId, progress);
		if(isAllRealPlayerLoadFinished()){
			changeToFightingState(false);
		}
	}

	private void changeToFightingState(boolean isTimeout) {
		if(isTimeout){
			for (FightRole player : room.getPlayers()) {
				if (player.isRobot()) {
					player.setLoadProgress(100);
				} else if (player.getLoadProgress() >= 100) {
					player.setStatus(FightRoleStatus.FIGHTING);
				}
			}
		} else {
			for (FightRole player : room.getPlayers()) {
				player.setStatus(FightRoleStatus.FIGHTING);
			}
		}
		
		room.changeState(new FightRoomStateFighting(room));
		room.broadcastMessage(S_FightStart.newBuilder().build());
	}
	
}
