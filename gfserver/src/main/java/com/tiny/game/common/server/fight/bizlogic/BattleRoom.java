package com.tiny.game.common.server.fight.bizlogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import game.protocol.protobuf.FightProtocol.C_WarFightAction;
import game.protocol.protobuf.FightProtocol.S_WarFightUpdateData;


public class BattleRoom {

	private static final Logger logger = LoggerFactory.getLogger(BattleRoom.class);

	private static int WAITING_TIMEOUT = 5000;   // 等待玩家连接5秒超时
	private static int LOADING_TIMEOUT = 30000;   // 等待玩家加载10秒超时

	private long lastFrameUpdateTime = 0;
	private long maxUsedUpdateTime = 0;
	private long updateCount = 0;
	private long updateTotalTime = 0;
	
	public void logUsedUpdateTime(long time) {
		try {
			lock.lock();
			if(time > maxUsedUpdateTime) {
				maxUsedUpdateTime = time;
			}
			if(status == BattleRoomStatus.ROOM_INBATTLE) {
				updateCount++;
				updateTotalTime+=time;
			}
		} finally {
			lock.unlock();
		}
	}
	
	private int roomId;
	//private List<FightRole> players;
	private Map<String, FightRole> players = new ConcurrentHashMap<String, FightRole>();
	private int status;
	private long lastRoomStatusChangeTime;
	private boolean isBreakLine;
	private List<SurrenderGroup> surrenderGroups = new ArrayList<SurrenderGroup>();
	private FightStage war;
//	private WarVideoRecord videoRecord;
//	private WarVideoReplayer videoReplayer;

	private Queue<C_WarFightAction> userActionQueue = new LinkedBlockingQueue<C_WarFightAction>();
	private FightContext ctx;
	
	private Lock lock = new ReentrantLock();
	
	private C_WarFightAction getUserAction() {
		return userActionQueue.poll();
	}

	private synchronized void cacheUserAction(C_WarFightAction action) {
		if (userActionQueue != null) {
			userActionQueue.add(action);
		}
	}

	public BattleRoom(int roomId, List<FightRole> fightRoles, FightContext ctx) {
		this.roomId = roomId;
		this.ctx = ctx;
		
		for(FightRole fr : fightRoles) {
			players.put(fr.getRoleId(), fr);
		}

//		if (replayVideoId > 0) {
//			byte[] videoBinData = WarVideoRecord.loadVideoBinData(videoId);
//			WarVideoRecord vr = WarVideoRecord.factoryFromBinData(videoBinData);
//			warEnterData = vr.getWarEnterData().toBuilder();
//			videoReplayer = new WarVideoReplayer(this, vr);
//			war = new War(vr.getRandomSeed(), this, matcherType, challengeType, roomId, stageId, players, warEnterData);
//		} else {
			war = new FightStage(this);
//			videoRecord = new WarVideoRecord();
//			videoRecord.setWarEnterData(warEnterData);
//			videoRecord.setRandomSeed(seed);
//		}

		initRoomOtherParameters();
	}

	private void initRoomOtherParameters() {
		war.init();

		this.status = BattleRoomStatus.ROOM_WAITING;
		this.lastRoomStatusChangeTime = System.currentTimeMillis();
		this.isBreakLine = false;

		for (FightRole frole : players.values()) {
			if (logger.isDebugEnabled()) {
				logger.debug("CreateBattleRoom. room roleId={}", frole.getRoleId());
			}

			// TODO: handle video
//			if (replayVideoId > 0 && frole.getRoleId() != currentVideoWatchRoleId) { // in video play mode, others will be robot
//				frole.setRobot(true);
//			}

			if (frole.isRobot()) {
				frole.setStatus(FightRoleStatus.ONLINE);
			}
		}

		checkRoomPrepare();
	}

	public synchronized void restartFightRoomForNewGuide() {
//		long seed = getRandomSeed();
//		war = new FightStage(seed, this, matcherType, 1, roomId, stageId, players, warEnterData);
//		initRoomOtherParameters();
	}

	public FightStage getWar() {
		return war;
	}

	public int getRoomId() {
		return roomId;
	}

	public int getRoomStatus() {
		return status;
	}

//	public Collection<FightRole> getFightRoles() {
//		return players.values();
//	}

	public void updateUserActionNow() {
//		if (videoReplayer != null) { // video replay
//			if (videoReplayer.hasVideoActionToReplay()) {
//				videoReplayer.replayAction();
//			}
//		} else {
			if (war.getRoomState() == RoomState.Gameing) {
				C_WarFightAction action = null;
				while ((action = getUserAction()) != null) {
					executeActionNow(action);
				}
			}
//		}
	}

	public boolean updateFrame() {
		try {
			lock.lock();
			return war.update();
		} finally {
			lock.unlock();
		}
	}

	public boolean update() {
		try {
			lock.lock();
			lastRoomStatusUpdateTime = System.currentTimeMillis();
			// 房间等待超时，切换房间状态
			long durationTime = System.currentTimeMillis() - this.lastRoomStatusChangeTime;
			switch (status) {
				case BattleRoomStatus.ROOM_WAITING:
					// 超过5秒有玩家没进入战斗房间，则直接进入加载
					if (durationTime > WAITING_TIMEOUT) {
						changeStatusToRoomPrepare();
					}
					break;
				case BattleRoomStatus.ROOM_PREPARE:
					// 更新并广播加载进度
					//playerBattleLoad(0, 0);

					// 超过8秒有玩家没加载完成，则直接进入战斗
					if (durationTime > LOADING_TIMEOUT) {
						for (FightRole player : players) {
							if (player.isRobot()) {
								player.setLoadProgress(100);
							}

							if (player.getLoadProgress() >= 100) {
								player.setStatus(FightRoleStatus.INBATTLE);
							}
						}

						changeStatusToRoomInBattle();
					}
					break;
				case BattleRoomStatus.ROOM_INBATTLE:
					// 检测投降投票
					SurrenderGroup toDeleteGroup = null;
					for (SurrenderGroup surrenderGroup : surrenderGroups) {
						// 超过半数玩家发起了投票，结算投票结果
						if ((surrenderGroup.getOkNum() + surrenderGroup.getNoNum()) > surrenderGroup.getTotalNum() / 2) {
							// 超过半数同意投降
							if (surrenderGroup.getOkNum() > surrenderGroup.getTotalNum() / 2) {
								onSurrenderFinish(surrenderGroup.getGroupId(), BattleSurrenderResult.AGREE);
							}
							// 不同意
							else {
								onSurrenderFinish(surrenderGroup.getGroupId(), BattleSurrenderResult.DISAGREE);
							}

							toDeleteGroup = surrenderGroup;
						} else if (System.currentTimeMillis() > surrenderGroup.getRobotVoteTime()) {
	                        for (FightRole pp : players) {
	                            if (pp.isRobot() && pp.getGroupId() == surrenderGroup.getGroupId()) {
	                                if (RandUtil.nextInt(2)  == 1) {
	                                    surrenderGroup.setOkNum(surrenderGroup.getOkNum() + 1);
	                                } else {
	                                    surrenderGroup.setNoNum(surrenderGroup.getNoNum() + 1);
	                                }
	                            }
	                        }
	                    }

					}
					surrenderGroups.remove(toDeleteGroup);
					break;
				case BattleRoomStatus.ROOM_BATTLEEND:
					return true;
				default:
					break;
			}

			return false;
			
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 玩家进入房间
	 *
	 * @param roleId
	 */
	public void enterRole(int roleId) {
		try {
			lock.lock();
			for (FightRole player : players) {
				if ((player.getRoleId() == roleId)
						&& (player.getStatus() == FightRoleStatus.OFFLINE)) {
					player.setStatus(FightRoleStatus.ONLINE);
					break;
				}
			}

			checkRoomPrepare();
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 检查房间准备完毕
	 */
	public void checkRoomPrepare() {
		if (this.status == BattleRoomStatus.ROOM_WAITING) {
			boolean isRoomPrepare = true;
			for (FightRole player : players) {
				if (player.getStatus() == FightRoleStatus.OFFLINE) {
					isRoomPrepare = false;
					break;
				}
			}

			if (isRoomPrepare) {
				changeStatusToRoomPrepare();
			}
		}
	}

	private boolean isNeedToSyncFightDataToPlayer(FightRole player) {
		return status == BattleRoomStatus.ROOM_INBATTLE && !player.isRobot() &&(
				//(player.getLoadProgress() >= 100 && player.getStatus() == FightRoleStatus.INBATTLE)
				(player.getStatus() > FightRoleStatus.OFFLINE)
				|| player.isNeedFullSyncFlag()
				);
	}
	
	public List<FightRole> getAllPlayersByNeedToSyncDeltaData() {
		List<FightRole> list = new ArrayList<FightRole>();
		for (FightRole player : players) {
			if(isNeedToSyncFightDataToPlayer(player)) {
				list.add(player);
			}
		}
		return list;
	}
	
	public void broadcastFightUpdateData() {
		List<FightRole> players = getAllPlayersByNeedToSyncDeltaData();
		boolean needFullSyncFlag = false;
		for(FightRole fr : players) {
			if(fr.isNeedFullSyncFlag()) {
				needFullSyncFlag = true;
				break;
			}
		}
//		if(players.size() > 0) {
//			List<SceneObject> list = war.getScene().buildLastestFightData(needFullSyncFlag);
//	        S_WarFightUpdateData deltaSyncData = NetMessageFactory.factoryDelta(list);
//			S_WarFightUpdateData fullSyncData = null;
//			if (needFullSyncFlag) {
//				fullSyncData = NetMessageFactory.factoryFull(list);
//			}
//
//			for(FightRole fr : players) {
//				S_WarFightUpdateData syncData = fr.isNeedFullSyncFlag() ? fullSyncData : deltaSyncData;
//				fr.getSession().getChannel().eventLoop().submit(() -> {
//					long startTime = System.nanoTime();
//					fr.getSession().writeAndFlush(0x890, RetCode.RET_OK_VALUE, syncData);
//					long endTime = System.nanoTime();
//					syncStats.addWriteTime(endTime - startTime);
//				});
//				if(fr.isNeedFullSyncFlag()) {
//					fr.setNeedFullSyncFlag(false);
//				}
//			}
//		}
	}
	
	/**
	 * 切换房间到准备完毕状态
	 */
	public void changeStatusToRoomPrepare() {
		this.status = BattleRoomStatus.ROOM_PREPARE;
		this.lastRoomStatusChangeTime = System.currentTimeMillis();
		broadcastRoomPrepare();
	}

	/**
	 * 广播房间准备完毕
	 */
	public void broadcastRoomPrepare() {
		// S_BattleRoomPrepare_0x810
//		for (FightRole player : players) {
//			if (!player.isRobot() && (player.getStatus() > FightRoleStatus.OFFLINE)) {
//				//if (WarGlobals.config.enableShowFightLog) {
//					logger.info("RoomPrepareNotify. roleId={}", player.getRoleId());
//				//}
//				player.getSession().writeAndFlush(0x810, RetCode.RET_OK_VALUE);
//			}
//		}
	}

	private boolean isAllRealPlayerLoadFinished() {
		boolean flag = true;
		for (FightRole player : players) {
			if (player.getStatus() < FightRoleStatus.INBATTLE && player.getLoadProgress() < 100 && !player.isRobot()) {
				return false;
			}
		}
		return flag;
	}

	/**
	 * 更新玩家加载进度
	 *
	 * @param roleId
	 * @param progress
	 */
	public void playerBattleLoad(int roleId, int progress) {
		try {
			lock.lock();
			
			for (FightRole player : players) {
				if (player.getStatus() < FightRoleStatus.INBATTLE) {
					if ((player.getRoleId() == roleId) && (progress >= player.getLoadProgress())) {
						player.setLoadProgress(progress);
						if ((progress == 100) && (this.status == BattleRoomStatus.ROOM_INBATTLE)) {
							player.setStatus(FightRoleStatus.INBATTLE);
						}
					}

					if (player.isRobot()) {  // random add robot load progress
						int robotProgress = player.getLoadProgress() + (5 + RandUtil.nextInt(5));
						if (robotProgress > 100) {
							robotProgress = 100;
						} else if (isAllRealPlayerLoadFinished()) {
							robotProgress = 100;
						}

						player.setLoadProgress(robotProgress);
					}
				}
			}

			FightRole fr = getFightRole(roleId);
			if(!fr.isRobot()) {
				logger.info("roleId={},progress={}", fr.getRoleId(), fr.getLoadProgress());
				//sendBattleStartDataToAllClient(); // ask client to load all data generated by server, client only need to load, no need to generate
				broadcastBattleLoad();
				
//				if(fr.getLoadProgress() >=100 && this.status == BattleRoomStatus.ROOM_INBATTLE) { // sync full data
//					hostSyncBattleData(fr);
//				}
				
				checkBattleStart();
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 广播资源加载进度
	 *
	 * @param roleId
	 * @param progress
	 */
	public void broadcastBattleLoad() {
//		S_BattleLoad.Builder battleLoad = S_BattleLoad_0x811.newBuilder();
//
//		battleLoad.setRoomStatus(status);
//		for (FightRole player : players) {
//			ProtoLoadProgress.Builder protoLoad = ProtoLoadProgress.newBuilder();
//			protoLoad.setRoleId(player.getRoleId());
//			protoLoad.setProgress(player.getLoadProgress());
//			battleLoad.addLoadStatus(protoLoad.build());
//		}
//
//		for (FightRole player : players) {
//			if (!player.isRobot() && (player.getStatus() == FightRoleStatus.ONLINE)) {
//				player.getSession().writeAndFlush(0x811, RetCode.RET_OK_VALUE, battleLoad.build());
//			}
//		}
	}

	/**
	 * 检查玩家加载完毕
	 */
	public void checkBattleStart() {
		if (this.status == BattleRoomStatus.ROOM_PREPARE) {
			boolean isBattleStart = true;
			for (FightRole player : players) {
				if (player.getLoadProgress() < 100) {
					isBattleStart = false;
					break;
				}
			}

			if (isBattleStart) {
				for (FightRole player : players) {
					player.setStatus(FightRoleStatus.INBATTLE);
				}

				changeStatusToRoomInBattle();
			}
		}
	}

	public void changeStatusToRoomInBattle() {
		war.setStart();
		this.status = BattleRoomStatus.ROOM_INBATTLE;
		this.lastRoomStatusChangeTime = System.currentTimeMillis();
		broadcastBattleStart();
	}

	public void broadcastBattleStart() {
		// S_BattleStart_0x812
//		for (FightRole player : players) {
//			if (!player.isRobot()
//					&& (player.getStatus() > FightRoleStatus.OFFLINE)
//					&& (player.getLoadProgress() >= 100)) {
//				player.getSession().writeAndFlush(0x812, RetCode.RET_OK_VALUE);
//			}
//		}

		recordBattleStartEvent();
	}

	private void recordBattleStartEvent() {
		for (FightRole player : players) {
			if (!player.isRobot()
					&& (player.getStatus() > FightRoleStatus.OFFLINE)
					&& (player.getLoadProgress() >= 100)) {
				//GmLogManager.getInstance().battleStart(player, matcherType, roomId, stageId, war.getEnterLegionData(player.getLegionId()));
				logger.info("BattleStartLog: role:" + player.getRoleId() ); //+ " ==> channel id:" +player.getSession().getChannel().id()
			}
		}
	}

	/**
	 * 同步主机战斗数据到请求客户端
	 *
	 * @param askRoleId
	 * @param time
	 * @param battleData
	 */
	public void hostSyncBattleData(FightRole frole) {
		logger.info("hostSyncBattleData: roleId={}", frole.getRoleId());
		try {
			lock.lock();
			
			if(this.status < BattleRoomStatus.ROOM_INBATTLE) { 
				// sync at once
//				S_WarFightUpdateData res = war.getScene().buildLastestFightFullSyncData();
//				frole.getSession().writeAndFlush(0x890, RetCode.RET_OK_VALUE, res);
			} else {
				frole.setNeedFullSyncFlag(true);
			}
		} finally {
			lock.unlock();
		}
	}

	public void executeAction(C_WarFightAction action) {
		try {
			lock.lock();
			if (!war.isRoomStateOver()) {
				cacheUserAction(action);
			} else {
				//logger.info("Battle is end, ignore user action: " + action);
			}
		} finally {
			lock.unlock();
		}
	}

	public void executeActionNow(C_WarFightAction action) {
		if (logger.isInfoEnabled()) {
			logger.info("Do user action now: " + ", time: " + System.currentTimeMillis() + ", ac: " + action);
		}
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

		//new WarFightActionUserRecord(war.getScene(), action); // main relation by itself

//		war.getScene().exeAction(ap);

//		if (videoRecord != null) {
//			WarFightVideoActionRecord.Builder builder = WarFightVideoActionRecord.newBuilder();
//			builder.setActionParameter(action);
//			builder.setTime((int) war.getWarTime().getWarPassedTime());
//			videoRecord.addWarFightVideoActionRecord(builder.build());
//		}
	}

	/**
	 * 通知离开战斗
	 */
	private void broadcastBattleLeave(int roleId) {
		// S_BattleLeave_0x813
//		S_BattleLeave_0x813.Builder msg = S_BattleLeave_0x813.newBuilder();
//		msg.setRoleId(roleId);
//
//		for (FightRole player : players) {
//			if (isNeedToSyncFightDataToPlayer(player)) {
//				player.getSession().writeAndFlush(0x813, RetCode.RET_OK_VALUE, msg.build());
//			}
//		}
	}

	/**
	 * 玩家断线
	 *
	 * @param roleId
	 */
	public void playerBattleDisconnected(int roleId) {
//		try {
//			lock.lock();
			for (FightRole player : players) {
				if (player.getRoleId() == roleId) {
					logger.info("roleId={} disconnected, set status to offline", roleId);
					player.setStatus(FightRoleStatus.OFFLINE);
					player.setLoadProgress(0);
					//player.setLastSyncData(null);
					break;
				}
			}

			// 通知客户端玩家离开
			broadcastBattleLeave(roleId);
			isBreakLine = true;
//		} finally {
//			lock.unlock();
//		}
	}

	/**
	 * 玩家重连进入战斗
	 *
	 * @param roleId
	 */
	public void playerBattleReconncted(int roleId) {
		logger.info("BattleReconnected. roleId={}", roleId);
		try {
			lock.lock();
			FightRole player = getFightRole(roleId);
			if ((player != null)) { //&& player.getStatus() == FightRoleStatus.OFFLINE
				player.setStatus(FightRoleStatus.ONLINE);
				player.setNeedFullSyncFlag(true);
			}
		} finally {
			lock.unlock();
		}
	}

	public void doGmBattleChangeResult(int roleId, boolean win) {
		try {
			lock.lock();
			FightRole targetPlayer = null;
			for (FightRole player : players) {
				if (player.getRoleId() == roleId) {
					targetPlayer = player;
					break;
				}
			}

			int groupId = targetPlayer.getGroupId();

			if (win) {
//				System.out.println("gm win: " + groupId);
				for (FightRole player : players) {
					if (player.getRoleId() != roleId) {
						onSurrenderFinish(player.getGroupId(), BattleSurrenderResult.AGREE);
					}
				}
			} else {
//				System.out.println("gm fail: " + groupId);
				onSurrenderFinish(targetPlayer.getGroupId(), BattleSurrenderResult.AGREE);
			}
		} finally {
			lock.unlock();
		}
	}

	private boolean isSinglePlayer() {
		return true;
	}
	/**
	 * 玩家战斗投降
	 *
	 * @param roleId
	 * @param result: 0.反对   1.同意
	 */
	public boolean battleSurrender(int roleId, int result) {
		try {
			lock.lock();
			if (isSinglePlayer()) {
				if (result == BattleSurrenderResult.AGREE) {
					FightRole targetPlayer = null;
					for (FightRole player : players) {
						if (player.getRoleId() == roleId) {
							targetPlayer = player;
							break;
						}
					}

					// 1v1有人投降则直接给投票结果
					if (targetPlayer != null) {
						onSurrenderFinish(targetPlayer.getGroupId(), result);
					}
				}
			} else {
				for (FightRole player : players) {
					if (player.getRoleId() == roleId) {
						// 查看是否已有投降记录
						SurrenderGroup surrenderGroup = null;
						for (SurrenderGroup sg : surrenderGroups) {
							if (sg.getGroupId() == player.getGroupId()) {
								surrenderGroup = sg;
							}
						}

						// 更新投降记录
//						if (surrenderGroup == null) {
//						    FightLegionGroup legionGroup = war.getScene().getFightLegionGroup(player.getGroupId());
//						    if (legionGroup == null || System.currentTimeMillis() < legionGroup.getLastSurrenderFailTime() + 10 * 1000) {
//						        return false;
//	                        }
//
//							surrenderGroup = new SurrenderGroup(player.getGroupId());
//							surrenderGroup.setOkNum(1);
//
//							surrenderGroups.add(surrenderGroup);
//						} else {
//							if (result == BattleSurrenderResult.AGREE) {
//								surrenderGroup.setOkNum(surrenderGroup.getOkNum() + 1);
//							} else {
//								surrenderGroup.setNoNum(surrenderGroup.getNoNum() + 1);
//							}
//						}

						// 广播投降记录
						broadcastSurrenderStatus(surrenderGroup);

						break;
					}
				}
			}
			return true;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 广播当前投降状态
	 */
	private void broadcastSurrenderStatus(SurrenderGroup surrenderGroup) {
//		S_BattleSurrender_0x840.Builder notify = S_BattleSurrender_0x840.newBuilder();
//		notify.setOkNum(surrenderGroup.getOkNum());
//		notify.setNoNum(surrenderGroup.getNoNum());
//		notify.setCount(surrenderGroup.getTotalNum());
//
//		for (FightRole player : players) {
//			if (!player.isRobot() && (player.getStatus() > FightRoleStatus.OFFLINE)
//					&& player.getGroupId() == surrenderGroup.getGroupId()) {
//				player.getSession().writeAndFlush(0x840, RetCode.RET_OK_VALUE, notify.build());
//			}
//		}
	}

	/**
	 * 广播投降结果
	 *
	 * @param groupId
	 * @param result
	 */
	private void onSurrenderFinish(int groupId, int result) {
//		if (result == BattleSurrenderResult.AGREE) {
//			war.setGroupSurrender(groupId);
//		} else {
//            FightLegionGroup legionGroup = war.getScene().getFightLegionGroup(groupId);
//            if (legionGroup != null) {
//                legionGroup.setLastSurrenderFailTime(System.currentTimeMillis());
//            }
//        }
//
//		broadcastSurrenderResult(groupId, result);
//		updateSurrenderStatis(groupId, result);
	}

	private void broadcastSurrenderResult(int groupId, int result) {
//		S_BattleSurrenderResult_0x841.Builder notify = S_BattleSurrenderResult_0x841.newBuilder();
//		notify.setResult(result);
//		notify.setGroupId(groupId);
//
//		for(FightRole player : players) {
//			if(!player.isRobot() && (player.getStatus() > FightRoleStatus.OFFLINE)) {
//				player.getSession().writeAndFlush(0x841, RetCode.RET_OK_VALUE, notify.build());
//			}
//		}
	}

	/**
	 * 战斗结束
	 *
	 * @param roleId
	 * @param isWin
	 */
	public void battleEnd(int endReason, List<ProtoRoleFightResult> results, int forbiddenRoleId) {

		Long battleStartTime = lastRoomStatusChangeTime;

		for (FightRole player : players) {
			player.setStatus(FightRoleStatus.BATTLEEND);
		}

		changeStatusToRoomBattleEnd();

//		// 确认是否精选视频和公会视频
//		int videoType = VideoType.NORMAL_VIDEO;
//		boolean isEliteVideo = true;
//		boolean isLeagueVideo = false;
//		if (isBreakLine
//				|| (matcherType == MatcherType.FRIENDSHIP)
//				|| (matcherType == MatcherType.FRIEND)
//				|| (matcherType == MatcherType.LEAGUE_CHALLENGE)) {   // 断线战斗和非竞技场战斗不选为精选视频
//			isEliteVideo = false;
//
//			if ((matcherType == MatcherType.FRIENDSHIP)
//					|| (matcherType == MatcherType.LEAGUE_CHALLENGE)) {
//				isLeagueVideo = true;
//				videoType = VideoType.LEAGUE_VIDEO;
//			}
//		}
//
//		F2Z_BattleEnd_0x25.Builder msg = F2Z_BattleEnd_0x25.newBuilder();
//
//		msg.setMatcherType(matcherType);
//		if (matcherType == MatcherType.PVV_VIDEO) {
//			msg.setReplayVideoId(replayVideoId);
//			msg.setCurrentVideoWatchRoleId(currentVideoWatchRoleId);
//		}
//		msg.setBattleRoomId(roomId);
//		msg.setBattleStageId(stageId);
//		msg.setReason(endReason);
//		msg.setLeagueStake(leagueStake);
//		msg.setDefendLeagueFlag(defendLeagueFlag);
//		msg.addAllResults(results);
//		if (forbiddenRoleId > 0) msg.setForbiddenRoleId(forbiddenRoleId);
//		for (FightRole player : players) {
//			msg.addFightRoles(player.getFightRole());
//
//			// 有机器人，或存在玩家排名大于100不选为精选视频
////			if(player.getFightRole().getIsRobot() || (player.getFightRole().getRank() > 200)) { // test
////				isEliteVideo = false;
////			}
//
////			if(player.getFightRole().getIsRobot()) { // test
////				isEliteVideo = false;
////			}
//		}
//		// 确认视频类型
//		if (isEliteVideo) {
//			videoType = VideoType.ELITE_VIDEO;
//		}
//
//		// 确认视频上传者
//		int uploadVideoRoleId = 0;
//		int legionId = 100;
//		for (FightRole player : players) {
//			ProtoFightRoleInfo fightRole = player.getFightRole();
//			if (!fightRole.getIsRobot() && (fightRole.getLegionId() < legionId)) {
//				legionId = fightRole.getLegionId();
//				if (isEliteVideo || isLeagueVideo) {
//					uploadVideoRoleId = fightRole.getRoleInfo().getRoleId();
//				}
//			}
//		}
//		msg.setUploadVideoRoleId(uploadVideoRoleId);
//
//		// 友谊战加入公会消息相关字段
//		if ((matcherType == MatcherType.FRIENDSHIP)
//				|| (matcherType == MatcherType.LEAGUE_CHALLENGE)) {
//			msg.setLeagueMsgId(leagueMsgId);
//		}
//
//		FightServer.getInstance().lanClientManager.sendToZoneServer(0x25, RetCode.RET_OK_VALUE, msg.build());

		recordBattleEndEvent(results, battleStartTime);

//		if (videoRecord != null) {
//			for (ProtoRoleFightResult result : results) {
//				videoRecord.addProtoRoleFightResult(result);
//			}
//
////			ProtoBattleVideoInfo.Builder bvi = ProtoBattleVideoInfo.newBuilder();
////			bvi.setWarVersion(WarGlobals.version);
////			bvi.setVideoId(roomId); // could be problem, maybe use uuid
////			bvi.setStageId(stageId);
////			bvi.setCreateTime((int)Calendar.getInstance().getTime().getTime() / 1000);
////			bvi.setBinData(videoRecord.getBinData());
////			bvi.setViewCount(0);
////			
////			videoRecord.save();
//		}
//		war.setOver();
//		war.over(uploadVideoRoleId, videoType, roomId, results, videoRecord);

	}

	private void recordBattleEndEvent(List<ProtoRoleFightResult> results, Long battleStartTime) {
//		int robotCount = 0;
//		for (FightRole player : players) {
//			if (player.isRobot()) {
//				robotCount++;
//			}
//		}
//		Map<Integer, String> groupInfo = new HashMap<Integer, String>();
//		Map<Integer, String> groupTeamInfo = new HashMap<Integer, String>();
//		for (ProtoRoleFightResult result : results) {
//			FightRole player = getFightRole(result.getRoleId());
//			if (player != null && !player.isRobot() && player.getLegionId() != 0) {
//				int groupId = player.getGroupId();
//				String groupPlayers = groupInfo.getOrDefault(groupId, "") + player.getRoleId() + ";";
//				groupInfo.put(groupId, groupPlayers);
//				String teams = groupTeamInfo.getOrDefault(groupId, "") + player.getFightRole().getTeamInfo().getTeamId() + ";";
//				groupTeamInfo.put(groupId, teams);
//			}
//		}
//
//		Long battleTime = (Long) (System.currentTimeMillis() - battleStartTime);
//		for (ProtoRoleFightResult result : results) {
//			FightRole player = getFightRole(result.getRoleId());
//			if (player != null && !player.isRobot()) {
////				System.out.println(RedisUtil.getZscore(RedisTable.RankRolePrize, player.getRoleId()+""));
////				int roleRank = RedisUtil.getZrevrank(RedisTable.RankRolePrize, player.getRoleId()+"");
//				GmLogManager.getInstance().battleEnd(player, matcherType, roomId, stageId, result.getEndType(), robotCount, groupInfo.get(player.getGroupId()), groupTeamInfo.get(player.getGroupId()), (int) (battleTime / 1000), getSurrenderStatisInfo(player.getGroupId()));
//				logger.info("Test: room " + roomId + ", role: " + player.getRoleId() + " used max update frame time: " + maxUsedUpdateTime + ", avg: " + (updateTotalTime *1.0f / updateCount) + ", max update between interval time: " +maxUpdateIntervalTime);
//			}
//		}
	}

	private FightRole getFightRole(int roleId) {
		for (FightRole player : players) {
			if (player.getRoleId() == roleId) {
				return player;
			}
		}
		return null;
	}

	/**
	 * 切换房间到战斗结束状态
	 */
	public void changeStatusToRoomBattleEnd() {
		this.status = BattleRoomStatus.ROOM_BATTLEEND;
		this.lastRoomStatusChangeTime = System.currentTimeMillis();
	}

	/**
	 * 踢走房间内所有的玩家
	 */
	public void kickoutFightRoles() {
		try {
			lock.lock();
			for (FightRole player : players) {
				player.setRoomId(0);
				//if (player.getRoomId() == roomId) {  // 玩家没有进入新的战斗房间
					FightRoleManager.getInstance().remove(player.getRoleId());
				//}
			}
		} finally {
			lock.unlock();
		}
	}

	static class SurrenderStatis {
		int groupId;
		int agressNumber;
		int disAgressNumber;

		public SurrenderStatis(int groupId) {
			this.groupId = groupId;
		}
	}

	private Map<Integer, SurrenderStatis> surrenderStatisMap = new ConcurrentHashMap<Integer, SurrenderStatis>();

	private void updateSurrenderStatis(int groupId, int result) {
		SurrenderStatis statis = surrenderStatisMap.get(groupId);
		if (statis == null) {
			statis = new SurrenderStatis(groupId);
			surrenderStatisMap.put(groupId, statis);
		}
		if (result == BattleSurrenderResult.AGREE) {
			statis.agressNumber++;
		} else if (result == BattleSurrenderResult.DISAGREE) {
			statis.disAgressNumber++;
		}
	}

	private String getSurrenderStatisInfo(int groupId) {
		SurrenderStatis statis = surrenderStatisMap.get(groupId);
		if (statis == null) {
			return "0/0";
		} else {
			return statis.agressNumber + "/" + statis.disAgressNumber;
		}
	}

	public long getLastFrameUpdateTime() {
		return lastFrameUpdateTime;
	}

	public void setLastFrameUpdateTime(long lastFrameUpdateTime) {
		if(this.lastFrameUpdateTime > 0) {
			if(status == BattleRoomStatus.ROOM_INBATTLE) {
				long delta = lastFrameUpdateTime - this.lastFrameUpdateTime;
//				System.out.println("update between interval: "+ delta);
				if(delta > maxUpdateIntervalTime) {
					maxUpdateIntervalTime = delta;
				}
			}
		}
		this.lastFrameUpdateTime = lastFrameUpdateTime;
	}
	
	public boolean isNeedToUpdateFrame(float battleFrameUpdateInterval) {
		try {
			lock.lock();
			if(isFrameUpdating) {
				return false;
			}
			if(System.currentTimeMillis() - lastFrameUpdateTime >=battleFrameUpdateInterval) {
				isFrameUpdating = true; // Note: set this!!!
//				logger.info("========> " + lastFrameUpdateTime + ", " + battleFrameUpdateInterval + ", " + System.currentTimeMillis());
				return true;
			}
			return false;
		} finally {
			lock.unlock();
		}
	}

	private long maxUpdateIntervalTime = 0;
	private volatile boolean isFrameUpdating = false;
	
	private long lastRoomStatusUpdateTime = System.currentTimeMillis();
	
	public boolean isNeedToUpdateRoomStatus() {
		if(System.currentTimeMillis() - lastRoomStatusUpdateTime >=1000) {
			return true;
		}
		return false;
	}
	
	public void setFrameUpdatingFinishFlag() {
		isFrameUpdating = false;
	}
	
}
