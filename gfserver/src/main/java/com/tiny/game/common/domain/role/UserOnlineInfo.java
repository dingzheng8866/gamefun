package com.tiny.game.common.domain.role;

import java.util.Date;

public class UserOnlineInfo {

	private String userId;
	private String loginServerId;
	private Date lastUpdateTime;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLoginServerId() {
		return loginServerId;
	}
	public void setLoginServerId(String loginServerId) {
		this.loginServerId = loginServerId;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public String toString() {
		return "UserOnlineInfo:"+userId+", online server:"+loginServerId+",update time:"+lastUpdateTime;
	}
	
}
