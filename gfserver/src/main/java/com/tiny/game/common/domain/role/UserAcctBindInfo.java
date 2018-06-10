package com.tiny.game.common.domain.role;

import java.util.Date;

public class UserAcctBindInfo {

	private String userId;
	
	private String bindedAccountId;
	
	private Date lastUpdateTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBindedAccountId() {
		return bindedAccountId;
	}

	public void setBindedAccountId(String bindedAccountId) {
		this.bindedAccountId = bindedAccountId;
	}
	
	public String toString() {
		return "UserAcctBindInfo:"+userId+"-"+bindedAccountId+":" +lastUpdateTime.toString();
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
}
