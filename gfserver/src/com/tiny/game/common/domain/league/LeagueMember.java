package com.tiny.game.common.domain.league;

public class LeagueMember {

	private String roleId;
	
	private LeagueMemberType type;
	
	private int donateNumber;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public LeagueMemberType getType() {
		return type;
	}

	public void setType(LeagueMemberType type) {
		this.type = type;
	}

	public int getDonateNumber() {
		return donateNumber;
	}

	public void setDonateNumber(int donateNumber) {
		this.donateNumber = donateNumber;
	}
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof LeagueMember)) {
			return false;
		}
		
		return roleId.equals(((LeagueMember) o).roleId);
	}
	
}
