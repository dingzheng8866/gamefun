package com.tiny.game.common.server.fight.bizlogic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FightStage {
	
	private static final Logger logger = LoggerFactory.getLogger(FightStage.class);

	private BattleRoom battleRoom;
	private StageWeightConfig weight =null;
	private WarScene scene;
	private WarTime warTime = null;
	private long timeMax = 120;
	private RoomState roomState = RoomState.Waiting;

	private long updateCounter = 0;
	private long gameOverPreTime = 0;
	private long realBeginFightingTime = 0;
	private long realEndFightingTime = 0;

	private boolean needToEndGame = false;
	private boolean hasSendWillOverUpdateData = false;

	public int getTimeleft() {
		int timeLeft = (int) (getGameMaxTime() - ((warTime != null) ? warTime.getWarPassedTime() / 1000 : 0));
		if (timeLeft <= 0) {
			timeLeft = 0;
		}
		return timeLeft;
	}

	public long getGameMaxTime() {
		return timeMax;
	}

	public void setGameMaxTime(long max) {
		timeMax = max;
		if (timeMax > 0) {
			timeMax += 5;
		}
	}

	public FightStage(BattleRoom battleRoom) { 
		//weight = StageWeightConfigReader.getInstance().getConfig(stageId);
		if(weight == null) {
			weight = new StageWeightConfig();
		}
		
		roomState = RoomState.Waiting;
		//WarGlobals.config.Init();
		this.battleRoom = battleRoom;
		scene = new WarScene(this);
		scene.init();
	}

	public BattleRoom getBattleRoom() {
		return battleRoom;
	}

	public WarTime getWarTime() {
		return warTime;
	}

	public boolean isRoomStateOver() {
		return roomState == RoomState.Over || (roomState == RoomState.Gameing && needToEndGame);
	}

	public boolean update() {
		if (roomState == RoomState.Over) {
			return true;
		}
		if (roomState == RoomState.Gameing) {
			markRealBeginFightingTime();

			boolean needToUpdateScene = false;
			if (needToEndGame) {
				if (!hasSendWillOverUpdateData) {
					hasSendWillOverUpdateData = true;
					needToUpdateScene = true;
				}
			} else {
				needToUpdateScene = true;
			}

			if (needToUpdateScene) {
				try {
					updateCounter++;
					warTime.update();
					battleRoom.updateUserActionNow();
					scene.update(warTime);
				} catch (Exception e) {
					logger.error("Update error:" + e.getMessage(), e);
				}
			}

			updateRoomState();
		}

		if (roomState == RoomState.Over) {
//			logger.info("pass time: " + warTime.getWarPassedTime());

//			List<ProtoRoleFightResult> result = scene.buildFightResultData(warTime.getWarPassedTime());
//
////			FightServer.getInstance().asynSendNetIOTask(() -> {
//				battleRoom.battleEnd(BattleEndReason.NORMAL, result, 0);
////			});
//			logger.info("Room: " + battleRoom.getRoomId() + " --> FPS: " + calculateFPS());
		}

		return false; // no need to end it here
	}

	private void markRealBeginFightingTime() {
		if (realBeginFightingTime == 0) {
			realBeginFightingTime = System.currentTimeMillis();
		}
	}

	private void markRealEndFightingTime() {
		if (realEndFightingTime == 0) {
			realEndFightingTime = System.currentTimeMillis();
		}
	}

	private long calculateFPS() {
		int second = (int) ((realEndFightingTime - realBeginFightingTime) / 1000);
		return second == 0 ? 999 : updateCounter / second;
	}

	private void updateRoomState() {
		if (!needToEndGame) {
//			if (!scene.calculateGameOverResult(false)) { // need end
//				if (isNeedToEndByTimeLimit()) {
//					needToEndGame = true;
//					logger.info("Mark room to end because of max time");
//					scene.calculateGameOverResult(true);
//				}
//			} else {
//				needToEndGame = true;
//				logger.info("Mark room to end because of some one win game");
//			}

//			if (isNeedToEndByTimeLimit()) {
//				needToEndGame = true;
//				logger.info("Mark room to end because of max time");
//				scene.calculateGameOverResult();
//			} else {
//				if (scene.needEndWar()) {
//					needToEndGame = true;
//					logger.info("Mark room to end because of some one win game");
//					scene.calculateGameOverResult();
//				}
//			}
//			if (needToEndGame) {
//				markRealEndFightingTime();
//				gameOverPreTime = System.currentTimeMillis();
//			} else {
//				//死亡状态判断
//				scene.updatePlayerDeadStatus();
//			}

		} else {
//			if (System.currentTimeMillis() - gameOverPreTime >= WarGlobals.config.battleEndDelayTime) { // give buffer time let client draw battle end legion buildings
//				setOver();
//			}
		}
	}

	private boolean isNeedToEndByTimeLimit() {
		if (warTime.getWarPassedTime() >= getGameMaxTime() * 1000) {
			return true;
		}
		return false;
	}

	// to start, make time run
	public void setStart() {
		roomState = RoomState.Gameing;
		warTime = new WarTime();
	}

	// init server fight data
	public void init() {
		scene.start();
	}

	public void setOver() {
		roomState = RoomState.Over;
	}

//	public void over(int uploadVideoRoleId, int videoType, int videoId, List<ProtoRoleFightResult> results, WarVideoRecord videoRecord) {
//		roomState = RoomState.Over;
//
//		logger.info("WarRoom over end: " + videoType);
//
////		if (videoType > VideoType.NORMAL_VIDEO && videoRecord != null) {  // 上传视频
////			FightRole uploadRole = FightRoleManager.getInstance().getFightRole(uploadVideoRoleId);
////			long sessionId = uploadRole.getSession().getSessionId();
////			ayncSaveTimeline(sessionId, videoId, videoType, videoRecord);
//
//			//WarVideoRecord.saveBinData(videoRecord);
////		}
//	}

	public void setGroupSurrender(int groupId) {
//		scene.setGroupSurrender(groupId);
	}

//	public WarReconnectData.Builder getWRCData() {
////		return reconnectData.generate();
//		return null; // TODO
//	}

//	public WarEnterLegionData getEnterLegionData(int legionId) {
//		for (WarEnterLegionData item : enterData.getLegionListList()) {
//			if (item.getLegionId() == legionId) {
//				return item;
//			}
//		}
//
//		return null;
//	}

	public RoomState getRoomState() {
		return roomState;
	}

	public void setRoomState(RoomState roomState) {
		this.roomState = roomState;
	}

//	public WarEnterData.Builder getEnterData() {
//		return enterData;
//	}
//
//	public void setEnterData(WarEnterData.Builder enterData) {
//		this.enterData = enterData;
//	}

	public StageWeightConfig getWeight() {
		return weight;
	}

	public void setWeight(StageWeightConfig weight) {
		this.weight = weight;
	}

	public WarScene getScene() {
		return scene;
	}

	public void setScene(WarScene scene) {
		this.scene = scene;
	}


}
