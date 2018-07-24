package com.tiny.game.common.server.fight.bizlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.server.ServerContext;


public class BattleRoomManager {
	private static final Logger logger = LoggerFactory.getLogger(BattleRoomManager.class);
	
	public Map<Integer, BattleRoom> rooms = new ConcurrentHashMap<Integer, BattleRoom>();
	private List<Integer> roomIds = new ArrayList<Integer>();
	private List<Integer> updatingRoomIds = new ArrayList<Integer>();
	
	private List<Integer> selectToUpdateRoomIds = new ArrayList<Integer>();
	
	private LinkedBlockingQueue<Integer> needUpdateRoomQueue = new LinkedBlockingQueue<Integer>();
	
	private static class SingletonHolder {
		private static BattleRoomManager instance = new BattleRoomManager();
	}

	public static BattleRoomManager getInstance() {
		return SingletonHolder.instance;
	}
	
	private float battleFrameUpdateInterval = 100;
	
	public ExecutorService executorService;
	private boolean working = false;
	private int logicUpdateThreads = 16;
	
	public synchronized void init() {
		int fps = ServerContext.getInstance().getPropertyInt("Battle.Logic.FPS", "10");
		battleFrameUpdateInterval = 1000/fps;
		logicUpdateThreads = ServerContext.getInstance().getPropertyInt("Battle.Logic.Threads", "8");
		logger.info("Battle FPS: " + fps + " ==>" + battleFrameUpdateInterval + " ==> Battle.Logic.Threads: " +logicUpdateThreads);
		
		if(executorService == null) {
			working = true;
			
			executorService = Executors.newFixedThreadPool(logicUpdateThreads, r -> {
				return new Thread(r, "BLogic");
			});
			
			for(int i=0; i<logicUpdateThreads; i++) {
				executorService.submit(new BattleRoomUpdateTask());
			}
		}
	}
	
//	public synchronized BattleRoom createBattleRoom(int matcherType, int battleMode, int roomId, int stageId, List<FightRole> players, int leagueMsgId, int leagueStake, int defendLeagueFlag, int challengeType, int videoId, int videoWatchRoleId, List<N_SummonBuildConf> summonBuildConfList) {
//		BattleRoom room = new BattleRoom(matcherType, battleMode, roomId, stageId, players, leagueMsgId, leagueStake, defendLeagueFlag, challengeType, videoId, videoWatchRoleId, summonBuildConfList);
//		rooms.put(roomId, room);
//		roomIds.add(0, roomId);
//		if(WarGlobals.config.enableShowFightLog) {
//			logger.info("Create battle room {}, total size {}", roomId, rooms.size());
//		}
//		return room;
//	}
	
	public int getTotalRooms() {
		return rooms.size();
	}
	
	public BattleRoom getBattleRoom(int roomId) {
		return rooms.get(roomId);
	}
	
	private void prepareNeedUpdateRooms() {
		//selectToUpdateRoomIds
		if(selectToUpdateRoomIds.size() < 1) {
			selectToUpdateRoomIds.addAll(roomIds);
		}
		
		if(selectToUpdateRoomIds.size() > 0) {
			List<Integer> tempList = new ArrayList<Integer>();
			for(int i=0; i<selectToUpdateRoomIds.size(); i++) {
//				roomCheckIndex++;
				int roomId = selectToUpdateRoomIds.get(i);
				BattleRoom room = getBattleRoom(roomId);
				if(room!=null) {
					long lastUpdateTime = room.getLastFrameUpdateTime();
					if(System.currentTimeMillis() - lastUpdateTime >=battleFrameUpdateInterval) {
						if(!needUpdateRoomQueue.contains(roomId)) {
							try {
								needUpdateRoomQueue.put(roomId);
								tempList.add(roomId);
							} catch (InterruptedException e) {
								logger.error("Failed to put room in queue to update, error: "+e.getMessage(), e);
								break;
							}
						} else {
							logger.warn("Battle FPS slow update interval: " + battleFrameUpdateInterval + " ==> " + roomId + ", queue size: " + needUpdateRoomQueue.size());
						}
					}
				} else {
					tempList.add(roomId);
				}
			}
			selectToUpdateRoomIds.removeAll(tempList);
		}
	}
	
	private synchronized BattleRoom getNextNeedToUpdateFrameBattleRoom() {
		BattleRoom found = null;
		for(int roomId : roomIds) {
			BattleRoom br = getBattleRoom(roomId);
			if(br!=null) {
				if(br.isNeedToUpdateFrame(battleFrameUpdateInterval)) {
					found = br;
					break;
				} 
			} 
		}
		
		if(found!=null) {
			roomIds.remove(new Integer(found.getRoomId()));
			updatingRoomIds.add(found.getRoomId());
		}
		
		return found;
	}
	
	private synchronized void clearRoom(int roomId) {
		rooms.remove(roomId);
		roomIds.remove(new Integer(roomId));
		updatingRoomIds.remove(new Integer(roomId));
	}
	
	private synchronized void notifyRoomFrameUpdateFinished(int roomId) {
		updatingRoomIds.remove(new Integer(roomId));
		roomIds.add(roomId);
	}
	
	class BattleRoomUpdateTask implements Runnable {
		public BattleRoomUpdateTask() {
		}
		@Override
		public void run() {
			while(working) {
				try {
					BattleRoom room = getNextNeedToUpdateFrameBattleRoom();
					if(room!=null) {
						updateRoom(room);
					} else {
						Thread.sleep(2);
					}
				} catch (Exception e) {
					logger.error("Failed to take room out from queue, error: "+e.getMessage(), e);
				}
			}
		}
		
		private void updateRoom(BattleRoom room) {
			try {
				room.setLastFrameUpdateTime(System.currentTimeMillis());
				boolean isDestroy = false;
				if(room.isNeedToUpdateRoomStatus()) {
					isDestroy = room.update();
				}
				if(!isDestroy) {
					isDestroy = room.updateFrame();
				}
				if(isDestroy) {
					room.kickoutFightRoles();
					clearRoom(room.getRoomId());
				}
				room.logUsedUpdateTime(System.currentTimeMillis() - room.getLastFrameUpdateTime());
			}catch(Exception e) {
				logger.error("BattleRoom "+room.getRoomId()+" update error: "+e.getMessage(), e);
			}finally {
				notifyRoomFrameUpdateFinished(room.getRoomId());
				room.setFrameUpdatingFinishFlag();
			}
		}
		
	}
}
