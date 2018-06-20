package com.tiny.game.common.domain.alliance;

import java.util.Date;

public class Alliance {

	private String id;
	private String name;
	private String description="";

	private int location;
	private AllianceJoinInType joinType = AllianceJoinInType.Any;
	private int joinNeedPrize = 0;
	private int fightRate = 1;
	private int publicFightLog = 1;
	
	private int level = 1;
	private int maxMemebers = 50;
	
	private Date lastUpdateTime = null;
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Alliance)) {
			return false;
		}
		
		return id.equals(((Alliance) o).id);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public AllianceJoinInType getJoinType() {
		return joinType;
	}
	public void setJoinType(AllianceJoinInType joinType) {
		this.joinType = joinType;
	}
	public int getJoinNeedPrize() {
		return joinNeedPrize;
	}
	public void setJoinNeedPrize(int joinNeedPrize) {
		this.joinNeedPrize = joinNeedPrize;
	}
	public int getFightRate() {
		return fightRate;
	}
	public void setFightRate(int fightRate) {
		this.fightRate = fightRate;
	}
	public boolean isPublicFightLog() {
		return publicFightLog>0;
	}
	public void setPublicFightLog(int publicFightLog) {
		this.publicFightLog = publicFightLog;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getMaxMemebers() {
		return maxMemebers;
	}

	public void setMaxMemebers(int maxMemebers) {
		this.maxMemebers = maxMemebers;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public int getPublicFightLog() {
		return publicFightLog;
	}
	
}
