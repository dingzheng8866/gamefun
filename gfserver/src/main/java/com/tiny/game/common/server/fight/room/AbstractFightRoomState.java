package com.tiny.game.common.server.fight.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.server.fight.bizlogic.FightRole;
import com.tiny.game.common.server.fight.bizlogic.FightRoleStatus;
import com.tiny.game.common.server.fight.bizlogic.RandUtil;

import game.protocol.protobuf.FightProtocol.ProtoLoadProgress;
import game.protocol.protobuf.FightProtocol.S_FightLoadProgress;

public abstract class AbstractFightRoomState implements FightRoomState {

	private static final Logger logger = LoggerFactory.getLogger(AbstractFightRoomState.class);
	
	protected FightRoom room;
	
	public AbstractFightRoomState(FightRoom room) {
		this.room = room;
	}
	
	@Override
	public void onPlayerEnter(String roleId) {
		room.getPlayer(roleId).setStatus(FightRoleStatus.ONLINE);
	}
	
	@Override
	public void onPlayerLoad(String roleId, int progress) {
		FightRole player = room.getPlayer(roleId);
		if(player.getStatus() == FightRoleStatus.OFFLINE) { // should not happen, client make sure this
			player.setStatus(FightRoleStatus.ONLINE); //
		}
		
		boolean needBroadcast = false;
		if(player.getStatus() == FightRoleStatus.ONLINE && progress > player.getLoadProgress()) {
			player.setLoadProgress(progress);
			if(logger.isInfoEnabled()){
				logger.info("Room {} player {} load progress {}", room.getRoomId(), player.getRoleId(), player.getLoadProgress());
			}
			needBroadcast = true;
			if ((progress == 100) && (getState() == State.Fighting)) {
				player.setStatus(FightRoleStatus.FIGHTING);
			}
		}
		
		if(needBroadcast){
			changeRobotLoadProgress();
			room.broadcastMessage(buildS_FightLoadProgress());
		}
	}

	protected boolean isAllRealPlayerLoadFinished() {
		boolean flag = true;
		for (FightRole player : room.getPlayers()) {
			if (!player.isRobot() && player.getLoadProgress() < 100) {
				return false;
			}
		}
		return flag;
	}
	
	private void changeRobotLoadProgress(){
		for(FightRole fr : room.getPlayers()) {
			if(fr.isRobot() && fr.getLoadProgress() < 100) {
				int robotProgress = fr.getLoadProgress() + (5 + RandUtil.nextInt(5));
				if (robotProgress > 100) {
					robotProgress = 100;
				} else if (isAllRealPlayerLoadFinished()) {
					robotProgress = 100;
				}
				fr.setLoadProgress(robotProgress);
			}
		}
	}
	
	private S_FightLoadProgress buildS_FightLoadProgress(){
		S_FightLoadProgress.Builder builder = S_FightLoadProgress.newBuilder();
		builder.setRoomStatus(room.getRoomState().getState().getValue());
		for(FightRole fr : room.getPlayers()) {
			ProtoLoadProgress.Builder pb = ProtoLoadProgress.newBuilder();
			pb.setRoleId(fr.getRoleId());
			pb.setProgress(fr.getLoadProgress());
			builder.addLoadStatus(pb.build());
		}
		return builder.build();
	}
	
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
