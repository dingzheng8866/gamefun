package com.tiny.game.common.domain.role;

public class UserAccountBindBean {

	private String userId;
	
	private String roleId;
	
	private String bindedAccountId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getBindedAccountId() {
		return bindedAccountId;
	}

	public void setBindedAccountId(String bindedAccountId) {
		this.bindedAccountId = bindedAccountId;
	}
	
}
