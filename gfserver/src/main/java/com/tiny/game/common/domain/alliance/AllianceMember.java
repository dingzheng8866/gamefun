package com.tiny.game.common.domain.alliance;

import java.util.Date;

public class AllianceMember {

	private String allianceId;
	private String roleId;
	private AllianceMemberTitle title;
	private int donated; // reset by week/month
	
	private Date lastUpdateTime = null;
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof AllianceMember)) {
			return false;
		}
		
		return roleId.equals(((AllianceMember) o).roleId); //allianceId.equals(((AllianceMember) o).allianceId) && 
	}
	
	public String getAllianceId() {
		return allianceId;
	}
	public void setAllianceId(String allianceId) {
		this.allianceId = allianceId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public AllianceMemberTitle getTitle() {
		return title;
	}
	public void setTitle(AllianceMemberTitle title) {
		this.title = title;
	}
	public int getDonated() {
		return donated;
	}
	public void setDonated(int donated) {
		this.donated = donated;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

}
