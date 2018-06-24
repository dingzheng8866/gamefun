package com.tiny.game.common.domain.alliance;

import java.util.Date;

public class AllianceMember {

	private String allianceId;
	private String roleId;
	private AllianceMemberTitle title;
	private int donated; // reset by week/month
	private int requested;
	private Date lastUpdateTime = null;
	private int point; // TODO
	private int roleLevel; // TODO
	private String roleName; // TODO
	private Date lastReqReinforceTime = null; // TODO
	
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

	public int getRequested() {
		return requested;
	}

	public void setRequested(int requested) {
		this.requested = requested;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getRoleLevel() {
		return roleLevel;
	}

	public void setRoleLevel(int roleLevel) {
		this.roleLevel = roleLevel;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Date getLastReqReinforceTime() {
		return lastReqReinforceTime;
	}

	public void setLastReqReinforceTime(Date lastReqReinforceTime) {
		this.lastReqReinforceTime = lastReqReinforceTime;
	}

}
