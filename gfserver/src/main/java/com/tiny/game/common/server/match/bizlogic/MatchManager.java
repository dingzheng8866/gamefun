package com.tiny.game.common.server.match.bizlogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.exception.InternalBugException;

public class MatchManager {

	private Map<String, MatchRequest> reqs = new ConcurrentHashMap<String, MatchRequest>();
	private Map<MatchRequestCategory, Map<String, MatchRequest>> categoryReqs = new ConcurrentHashMap<MatchRequestCategory, Map<String, MatchRequest>>();
	
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
			Map<String, MatchRequest> map = categoryReqs.get(req.getCategory());
			if(map==null) {
				map = new ConcurrentHashMap<String, MatchRequest>();
				categoryReqs.put(req.getCategory(), map);
			}
			map.put(req.getRoleId(), req);
		}
	}
	
	private void handleMatched(MatchRequestCategory cat, MatchRole... roles) {
		System.out.println("Matched: " + cat.getMatchType() + " ==> " + roles.length);
	}
	
	
	private void executeMatches() {
		for(MatchRequestCategory cat : categoryReqs.keySet()) {
			Map<String, MatchRequest> map = categoryReqs.get(cat);
			executeMatch(cat, map);
		}
	}
	
	private MatchRole getRobotMatchRole(int stageId) {
		MatchRole mr = new MatchRole();
		mr.setRoleId("bot123"); // TODO: finish me
		return mr;
	}
	
	private void executeMatch(MatchRequestCategory cat, Map<String, MatchRequest> requests) {
		
		List<String> matchedRoles = new ArrayList<String>();
		for(Map.Entry<String, MatchRequest> entry : requests.entrySet()) {
			if (cat.getMatchType() == GameConst.MatchType_GVE) {
				
			} else if (cat.getMatchType() == GameConst.MatchType_PVP) {
				
			} else if (cat.getMatchType() == GameConst.MatchType_MIX) {
				
			} else {
				throw new InternalBugException("Not supported match type: " + cat.getMatchType());
			}
		}
		

	}
	
	
	
	
}
