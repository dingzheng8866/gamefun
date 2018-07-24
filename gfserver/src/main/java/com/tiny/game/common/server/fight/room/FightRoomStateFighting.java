package com.tiny.game.common.server.fight.room;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.server.fight.bizlogic.FightRole;
import com.tiny.game.common.server.fight.bizlogic.FightRoleStatus;

public class FightRoomStateFighting extends AbstractFightRoomState {

	private static final Logger logger = LoggerFactory.getLogger(FightRoomStateFighting.class);
	
	public FightRoomStateFighting(FightRoom room) {
		super(room);
		recordBattleStartEvent();
		//fightStage.setStart();
	}

	public State getState() {
		return FightRoomState.State.Fighting;
	}

	@Override
	public boolean update() {
		
		return false;
	}

	@Override
	public void onPlayerDisconnect(String roleId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlayerReconnect(String roleId) {
		// TODO Auto-generated method stub
		
	}
	
	private void recordBattleStartEvent() {
		for (FightRole player : room.getPlayers()) {
			if (!player.isRobot()
					&& (player.getStatus() != FightRoleStatus.OFFLINE)
					&& (player.getLoadProgress() >= 100)) {
				//GmLogManager.getInstance().battleStart(player, matcherType, roomId, stageId, war.getEnterLegionData(player.getLegionId()));
				logger.info("BattleStartLog: role:" + player.getRoleId() ); //+ " ==> channel id:" +player.getSession().getChannel().id()
			}
		}
	}
	
}
