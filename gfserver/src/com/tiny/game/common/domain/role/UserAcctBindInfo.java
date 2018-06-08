package com.tiny.game.common.domain.role;

public class UserAcctBindInfo {

	private String userId;
	
	private String bindedAccountId;

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
		return "UserAcctBindInfo:"+userId+"-"+bindedAccountId;
	}
	
}
