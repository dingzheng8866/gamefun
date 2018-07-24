package com.tiny.game.common.server.fight.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.server.fight.bizlogic.BattleRoomStatus;
import com.tiny.game.common.server.fight.bizlogic.FightRole;
import com.tiny.game.common.server.fight.bizlogic.FightRoleStatus;
import com.tiny.game.common.server.fight.bizlogic.RandUtil;

import game.protocol.protobuf.FightProtocol.S_FightRoomCreateReady;

public class FightRoomStateWaiting extends AbstractFightRoomState {

	private static final Logger logger = LoggerFactory.getLogger(FightRoomStateWaiting.class);
	
	private long waitBeginTime = System.currentTimeMillis();
	public FightRoomStateWaiting(FightRoom room) {
		super(room);
	}

	public State getState() {
		return FightRoomState.State.Waiting;
	}
	
	@Override
	public boolean update() {
		// timeout -->
		if(System.currentTimeMillis() - waitBeginTime >= 5000) {
			changeToPreparingState();
		}
		return false;
	}

	@Override
	public void onPlayerEnter(String roleId) {
		room.getPlayer(roleId).setStatus(FightRoleStatus.ONLINE);
		
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
	
	@Override
	public void onPlayerLoad(String roleId, int progress) {
		FightRole player = room.getPlayer(roleId);
		if(player.getStatus() == FightRoleStatus.OFFLINE) { // should not happen, client make sure this
			player.setStatus(FightRoleStatus.ONLINE); //
		}
		
		if(player.getStatus() == FightRoleStatus.ONLINE && progress > player.getLoadProgress()) {
			player.setLoadProgress(progress);
			if ((progress == 100) && (getState() == State.Fighting)) {
				player.setStatus(FightRoleStatus.FIGHTING);
			}
		}
		
		for(FightRole fr : room.getPlayers()) {
			if(fr.isRobot()) {
				int robotProgress = fr.getLoadProgress() + (5 + RandUtil.nextInt(5));
				if (robotProgress > 100) {
					robotProgress = 100;
				} else if (isAllRealPlayerLoadFinished()) {
					robotProgress = 100;
				}

				player.setLoadProgress(robotProgress);
			}
		}
		
	}

	private boolean isAllRealPlayerLoadFinished() {
		boolean flag = true;
		for (FightRole player : room.getPlayers()) {
			if (!player.isRobot() && player.getStatus() == FightRoleStatus.OFFLINE || player.getLoadProgress() < 100) {
				return false;
			}
		}
		return flag;
	}
	
	@Override
	public void onPlayerDisconnect(String roleId) {
	}
	
	@Override
	public void onPlayerReconnect(String roleId) {
	}

	
}
