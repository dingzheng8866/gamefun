package com.tiny.game.common.server.match.bizlogic;

public class MatchRequest {

	private String roleId;
	private MatchRequestCategory category;
	
	private int timeout;
	private long beginMatchTime;
	
	private MatchRole matchRole;
	
	public MatchRequest(String roleId, int matchType, int vsX, int vsY, int stageId) {
		this.roleId = roleId;
		this.category = new MatchRequestCategory(matchType, vsX, vsY, stageId);
		beginMatchTime = System.currentTimeMillis();
	}
	
	public boolean isTimeout() {
		return System.currentTimeMillis() - beginMatchTime >= timeout;
	}
	
	public MatchRequestCategory getCategory() {
		return category;
	}

	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public long getBeginMatchTime() {
		return beginMatchTime;
	}

	public void setBeginMatchTime(long beginMatchTime) {
		this.beginMatchTime = beginMatchTime;
	}

	public MatchRole getMatchRole() {
		return matchRole;
	}

	public void setMatchRole(MatchRole matchRole) {
		this.matchRole = matchRole;
	}
	
}
