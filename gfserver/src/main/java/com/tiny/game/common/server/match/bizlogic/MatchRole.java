package com.tiny.game.common.server.match.bizlogic;

public class MatchRole {

	private String roleId;
	private int matchWeight; // battle power

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getMatchWeight() {
		return matchWeight;
	}

	public void setMatchWeight(int matchWeight) {
		this.matchWeight = matchWeight;
	}
	
	// TODO: ping value match?
	
}
