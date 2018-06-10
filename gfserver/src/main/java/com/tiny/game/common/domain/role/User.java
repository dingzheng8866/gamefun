package com.tiny.game.common.domain.role;

import java.util.Date;

public class User {

	private String userId;

//	private List<String> bindedAccountIds; // email, apple id, android device id, etc
	
	private String loginAccountId; // binded id
	private String loginDeviceId;
	private String loginDeviceInfo;
	private String loginIp;
	private String channel;
	private String platform;
	private String platformAccountId;
	private String platformAccountPassword;
	
	private Date createTime;
	private Date lastUpdateTime;
	
//	public void addBindedAccountId(String bindedAcctId) {
//		if(!bindedAccountIds.contains(bindedAcctId)) {
//			bindedAccountIds.add(bindedAcctId);
//		}
//	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
//	public List<String> getBindedAccountIds() {
//		return bindedAccountIds;
//	}
//	public void setBindedAccountIds(List<String> bindedAccountIds) {
//		this.bindedAccountIds = bindedAccountIds;
//	}
	public String getLoginAccountId() {
		return loginAccountId;
	}
	public void setLoginAccountId(String loginAccountId) {
		this.loginAccountId = loginAccountId;
	}
	public String getLoginDeviceId() {
		return loginDeviceId;
	}
	public void setLoginDeviceId(String loginDeviceId) {
		this.loginDeviceId = loginDeviceId;
	}
	public String getLoginDeviceInfo() {
		return loginDeviceInfo;
	}
	public void setLoginDeviceInfo(String loginDeviceInfo) {
		this.loginDeviceInfo = loginDeviceInfo;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getPlatformAccountId() {
		return platformAccountId;
	}
	public void setPlatformAccountId(String platformAccountId) {
		this.platformAccountId = platformAccountId;
	}
	public String getPlatformAccountPassword() {
		return platformAccountPassword;
	}
	public void setPlatformAccountPassword(String platformAccountPassword) {
		this.platformAccountPassword = platformAccountPassword;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("User: " + userId);
		sb.append(",loginAccountId" + loginAccountId);
		sb.append(",loginDeviceId" + loginDeviceId);
		sb.append(",loginIp" + loginIp);
		sb.append(",channel" + channel);
		sb.append(",platform" + platform);
		sb.append(",platformAccountId" + platformAccountId);
		sb.append(",platformAccountPassword" + platformAccountPassword);
		sb.append(",createTime" + createTime);
		sb.append(",lastUpdateTime" + lastUpdateTime);
		sb.append(",loginDeviceInfo" + loginDeviceInfo);
		return sb.toString();
	}
}
