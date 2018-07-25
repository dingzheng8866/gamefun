package com.tiny.game.common.server.fight.room;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.server.fight.bizlogic.FightRole;
import com.tiny.game.common.server.fight.bizlogic.FightRoleStatus;
import com.tiny.game.common.server.fight.bizlogic.IFight;
import com.tiny.game.common.server.fight.domain.ActionParameters;
import com.tiny.game.common.server.fight.domain.FightScene;
import com.tiny.game.common.server.fight.domain.PlayerActionChangeVideoPlaySpeed;
import com.tiny.game.common.server.fight.domain.PlayerActionChat;
import com.tiny.game.common.server.fight.domain.PlayerActionSurrender;
import com.tiny.game.common.server.fight.domain.PlayerActionUseItem;
import com.tiny.game.common.server.fight.domain.PlayerFightAction;
import com.tiny.game.common.server.fight.util.FightNetUtil;

import game.protocol.protobuf.FightProtocol.C_WarFightAction;
import game.protocol.protobuf.FightProtocol.S_FightLeave;
import game.protocol.protobuf.FightProtocol.S_WarFightUpdateData;

public class FightRoomStateFighting extends AbstractFightRoomState {

	private static Map<Integer, PlayerFightAction> actions = new HashMap<Integer, PlayerFightAction>();
	
	static {
		actions.put(IFight.Para.UserActionTypeUseItem.getValue(), new PlayerActionUseItem());
		actions.put(IFight.Para.UserActionTypeChat.getValue(), new PlayerActionChat());
		actions.put(IFight.Para.UserActionTypeSurrender.getValue(), new PlayerActionSurrender());
		actions.put(IFight.Para.UserActionTypeChangeVideoPlaySpeed.getValue(), new PlayerActionChangeVideoPlaySpeed());
	}
	
	private static final Logger logger = LoggerFactory.getLogger(FightRoomStateFighting.class);
	private FightScene scene = null;
	
	private Queue<C_WarFightAction> userActionQueue = new LinkedBlockingQueue<C_WarFightAction>();
	
	public FightRoomStateFighting(FightRoom room) {
		super(room);
		scene = room.getFightScene();
		recordBattleStartEvent();
		scene.init();
	}

	public State getState() {
		return FightRoomState.State.Fighting;
	}

	@Override
	public boolean update() {
		scene.updateFightTime();
		updateUserAction();
		scene.update();
		
		//sync scene data
		syncSceneDataToClient();
		
		if(scene.isNeedEnd()) {
			scene.calculateFightEndResult();
			room.changeState(new FightRoomStateEnding(room));
		}
		
		return false;
	}


	private void syncSceneDataToClient() {
		boolean needFullSyncFlag = false;
		for (FightRole player : room.getPlayers()) {
			if(player.isNeedFullSyncFlag()) {
				needFullSyncFlag = true; 
			}
		}
		
		S_WarFightUpdateData fullData = null;
		S_WarFightUpdateData deltaData = null;
		
		for (FightRole player : room.getPlayers()) {
			if(player.isNeedToSyncFightData()) {
				
				S_WarFightUpdateData syncData = player.isNeedFullSyncFlag() ? fullData : deltaData;
				//player.getSession().getChannel().eventLoop().submit(() -> {
					NetLayerManager.getInstance().asyncSendOutboundMessage(player.getSession(), syncData);
				//});
				
				if(player.isNeedFullSyncFlag()) {
					player.setNeedFullSyncFlag(false);
				}
			}
		}
		
	}
	
	@Override
	public void onPlayerDisconnect(String roleId) {
		super.onPlayerDisconnect(roleId);
		
		S_FightLeave.Builder builder = S_FightLeave.newBuilder();
		builder.setRoleId(roleId);
		S_FightLeave msg = builder.build();
		
		for (FightRole fr : room.getPlayers()) {
			if (!fr.isRobot() && fr.getStatus() == FightRoleStatus.FIGHTING) {
				NetLayerManager.getInstance().asyncSendOutboundMessage(fr.getSession(), msg);
			}
		}		
		
		//room.broadcastMessage(msg);
	}

	@Override
	public void onPlayerSyncFullData(String roleId) {
		room.getPlayer(roleId).setNeedFullSyncFlag(true);
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
	
	@Override
	public void onPlayerOperAction(C_WarFightAction action) {
		userActionQueue.add(action);
	}
	
	private C_WarFightAction getUserAction() {
		return userActionQueue.poll();
	}
	
	private void updateUserAction() {
//		if (videoReplayer != null) { // video replay
//			if (videoReplayer.hasVideoActionToReplay()) {
//				videoReplayer.replayAction();
//			}
//		} else {
			C_WarFightAction action = null;
			while ((action = getUserAction()) != null) {
				executeUserActionNow(action);
			}
//		}
	}
	
	private void executeUserActionNow(C_WarFightAction action) {
		PlayerFightAction fa = actions.get(action.getActionName());
		if (fa == null) {
			throw new InternalBugException("Internal error: not found user action bean to execute action: " + action.getActionName() + " ==> " + IFight.Para.valueOf(action.getActionName()));
		} else {
			ActionParameters ap = FightNetUtil.convertToActionParameter(action);
			
			fa.execute(room.getFightScene(), ap);
		}
	}
	
//	
//	public void executeActionNow(C_WarFightAction_0x890 action) {
//		if (WarGlobals.config.enableShowFightLog) {
//			logger.info("Do user action now: " + ", time: " + System.currentTimeMillis() + ", ac: " + action);
//		}
//		ActionParameter ap = NetMessageFactory.convertToActionParameter(action);
//		if (!isNewUserGuide()) {
//			if (ap.parameter(ActionParameterKey.PARA_LEGION_ID) != null) {
//				int legionId = ap.parameterToInt(ActionParameterKey.PARA_LEGION_ID);
//				FightLegion legion = war.getScene().getFightLegion(legionId);
//				if (legion != null && !legion.isComputer()) {
//					int fromRoleId = legion.getRoleId();
//					int expectedRoleId = ap.parameterToInt(ActionParameterKey.PARA_ACTION_FROM_ROLE);
//					if (fromRoleId != expectedRoleId) {
//						logger.error("Ignore user action: " + ap.getName() + ", because not pass role validation" + expectedRoleId + "--" + fromRoleId);
//						return;
//					}
//				}
//			}
//		}
//
//		new WarFightActionUserRecord(war.getScene(), action); // main relation by itself
//
//		war.getScene().exeAction(ap);
//
//		if (videoRecord != null) {
//			WarFightVideoActionRecord.Builder builder = WarFightVideoActionRecord.newBuilder();
//			builder.setActionParameter(action);
//			builder.setTime((int) war.getWarTime().getWarPassedTime());
//			videoRecord.addWarFightVideoActionRecord(builder.build());
//		}
//	}
	
}
