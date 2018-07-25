package com.tiny.game.common.server.fight.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.server.fight.bizlogic.FightRole;
import com.tiny.game.common.server.fight.bizlogic.FightRoleStatus;

import game.protocol.protobuf.FightProtocol.S_FightRoomCreateReady;

public class FightRoomStateWaiting extends AbstractFightRoomState {

	private static final Logger logger = LoggerFactory.getLogger(FightRoomStateWaiting.class);
	
	public FightRoomStateWaiting(FightRoom room) {
		super(room);
	}

	public State getState() {
		return FightRoomState.State.Waiting;
	}
	
	@Override
	public boolean update() {
		// timeout -->
		if(isStateTimeout(5000)) {
			changeToPreparingState();
		}
		return false;
	}

	@Override
	public void onPlayerEnter(String roleId) {
		super.onPlayerEnter(roleId);
		
		// all ready -->
		boolean hasNotEnter = false;
		for(FightRole fr : room.getPlayers()) {
			if(fr.getStatus() == FightRoleStatus.OFFLINE) {
				hasNotEnter = true;
				break;
			}
		}
		if(!hasNotEnter) {
			changeToPreparingState();
		}
	}

	private void changeToPreparingState() {
		room.changeState(new FightRoomStatePreparing(room));
		S_FightRoomCreateReady.Builder builder = S_FightRoomCreateReady.newBuilder();
		//builder.setRoomId(room.getRoomId());
		// TODO: 
		room.broadcastMessage(builder.build());
	}

	
}
