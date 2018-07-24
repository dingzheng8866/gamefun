package com.tiny.game.common.server.match.bizlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.exception.InternalBugException;

public class MatchManager {

	private Map<String, MatchRequest> reqs = new ConcurrentHashMap<String, MatchRequest>();
//	private Map<MatchRequestCategory, List<MatchRequest>> categoryReqs = new ConcurrentHashMap<MatchRequestCategory, List<MatchRequest>>();
	
	private Map<String, MatchRoom> rooms = new ConcurrentHashMap<String, MatchRoom>();
	
//	private Object lock = new Object();
	
	private Timer timer;
	private boolean working = true;
	
	private static MatchManager instance = new MatchManager();
	public static MatchManager getInstance() {
		return instance;
	}
	
	private MatchManager() {
	}
	
	public void init() {
		if(timer == null) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					while(working) {
						try {
							executeMatches();
						}catch(Exception e) {
							
						}
					}
				}
			}, 1000, 500);
		}
	}
	
	
	public void attendMatch(MatchRequest req) {
		MatchRequestCategory cat = req.getCategory();
		if(cat.getMatchType() == GameConst.MatchType_Train || cat.getMatchType() == GameConst.MatchType_PVE) {
			MatchRole robot = getRobotMatchRole(cat.getStageId());
			if(robot==null) {
				throw new InternalBugException("Not found robot for train stage : " + cat.getStageId());
			}
			handleMatched(cat, req.getMatchRole(), robot);
		} else {
			reqs.put(req.getRoleId(), req);
			String roomId = req.getMatchRoomId();
			MatchRoom room = null;
			synchronized (rooms) {
				room = rooms.get(req.getMatchRoomId());
				if(room==null) {
					room = new MatchRoom(roomId);
					rooms.put(roomId, room);
				}
			}
			
			boolean matched = room.onRoleEnterMatch(req);
			if(matched) {
				handleMatched(room.getMatchRequests().get(0).getCategory(), room.getMatchRequests().get(0).getMatchRole()); // TODO: finish me
				rooms.remove(roomId); // or room clear?
			}
		}
	}
	
	
	private void handleMatched(MatchRequestCategory cat, MatchRole... roles) {
		System.out.println("Matched: " + cat.getMatchType() + " ==> " + roles.length);
//		reqs.remove(key);
	}
	
	
	private void executeMatches() {
		List<String> roomIds = new ArrayList<String>(rooms.keySet());
		for(String roomId : roomIds) {
			MatchRoom room = rooms.get(roomId);
			boolean handled = room.handleMatchTimeoutRequests();
			if(handled) {
				rooms.remove(roomId);
			}
		}
	}
	
	private MatchRole getRobotMatchRole(int stageId) {
		MatchRole mr = new MatchRole();
		mr.setRoleId("bot123"); // TODO: finish me
		return mr;
	}
	
}
