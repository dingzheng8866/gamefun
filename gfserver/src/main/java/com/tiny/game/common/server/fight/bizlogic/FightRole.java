package com.tiny.game.common.server.fight.bizlogic;

import com.tiny.game.common.net.netty.NetSession;

public class FightRole {
	
	private String roleId;
	private int roomId;
	private boolean isRobot;
	private int legionId;
	private int groupId;
	private int loadProgress;
	private FightRoleStatus status;
	private NetSession session;
	private boolean needFullSyncFlag = false;
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public boolean isRobot() {
		return isRobot;
	}

	public void setRobot(boolean isRobot) {
		this.isRobot = isRobot;
	}

	public int getLegionId() {
		return legionId;
	}

	public void setLegionId(int teamId) {
		this.legionId = teamId;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getLoadProgress() {
		return loadProgress;
	}

	public void setLoadProgress(int loadProgress) {
		this.loadProgress = loadProgress;
	}

	public FightRoleStatus getStatus() {
		return status;
	}

	public void setStatus(FightRoleStatus status) {
		this.status = status;
	}

	public boolean isNeedFullSyncFlag() {
		return needFullSyncFlag;
	}

	public void setNeedFullSyncFlag(boolean needFullSyncFlag) {
		this.needFullSyncFlag = needFullSyncFlag;
	}

	public NetSession getSession() {
		return session;
	}

	public void setSession(NetSession session) {
		this.session = session;
	}
	
	public boolean isNeedToSyncFightData() {
		return !isRobot() && (getStatus() != FightRoleStatus.OFFLINE);
	}
	
}
