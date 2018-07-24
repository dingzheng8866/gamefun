package com.tiny.game.common.server.match.bizlogic;

import java.util.ArrayList;
import java.util.List;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.exception.InternalBugException;

public class MatchRoom {

	private String roomId;
	private List<MatchRequest> requests = new ArrayList<MatchRequest>();

	public MatchRoom(String roomId) {
		this.roomId = roomId;
	}

	public synchronized boolean onRoleEnterMatch(MatchRequest req) {
		if(requests.contains(req)) {
			requests.remove(req);
		}
		requests.add(req);
		
		MatchRequestCategory category = requests.get(0).getCategory();
		if(category.getMatchType() == GameConst.MatchType_GVE) {
			if(requests.size() == category.getVsX()) {
				return true;
			}
		} else if(category.getMatchType() == GameConst.MatchType_PVP) {
			if(requests.size() == category.getVsX() + category.getVsY()) {
				return true;
			}
		} else if(category.getMatchType() == GameConst.MatchType_MIX) {
			if(requests.size() == category.getVsX()) {
				return true;
			}
		} else {
			throw new InternalBugException("Not supported matchtype: " + category.getMatchType());
		}
		
		return false;
	}
	
	public synchronized boolean handleMatchTimeoutRequests() {
		for(MatchRequest req : requests) {
			if(req.isTimeout()) {
				boolean needMatchBot = true;
				if(needMatchBot) {
					// TODO: allocate bots
					return true;
				}
			}
		}
		return false;
	}
	
	public List<MatchRequest> getMatchRequests(){
		return requests;
	}
	
}
