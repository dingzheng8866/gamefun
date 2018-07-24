package com.tiny.game.common.server.fight.bizlogic;

public class SurrenderGroup {
	private int groupId;
	
	private int okNum;
	
	private int noNum;
	
	private int totalNum;
	
	private long startTime;

	private long robotVoteTime;
	
	public SurrenderGroup(int gId) {
		this.groupId = gId;
		this.okNum = 0;
		this.noNum = 0;
		this.totalNum = 2;
		this.startTime = System.currentTimeMillis();
//		this.robotVoteTime = this.startTime + (long)(RandUtil.range(3.0f, 6.0f) * 1000);
		this.robotVoteTime = this.startTime + 3000;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public int getOkNum() {
		return okNum;
	}

	public void setOkNum(int okNum) {
		this.okNum = okNum;
	}

	public int getNoNum() {
		return noNum;
	}

	public void setNoNum(int noNum) {
		this.noNum = noNum;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getRobotVoteTime() {
		return robotVoteTime;
	}
}
