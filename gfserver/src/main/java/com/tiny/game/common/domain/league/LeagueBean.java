package com.tiny.game.common.domain.league;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.InternalBugException;


public class LeagueBean {

	private String leagueId;
	private String leagueName;
	private String icon;
	
	private String description;
	private LeagueJoinType joinType;
	
	private String location;
	
	private Map<String, LeagueMember> members =new HashMap<String, LeagueMember>(); // role id
	private int level;
	
	private int maxMember = 50;
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof LeagueBean)) {
			return false;
		}
		
		return leagueId.equals(((LeagueBean) o).leagueId);
	}
	
	
	public String getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}
	public String getLeagueName() {
		return leagueName;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public LeagueJoinType getJoinType() {
		return joinType;
	}
	public void setJoinType(LeagueJoinType joinType) {
		this.joinType = joinType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Map<String, LeagueMember> getMembers() {
		return members;
	}
	public void setMembers(Map<String, LeagueMember> members) {
		this.members = members;
	}
	
	public void addMember(Role role, LeagueMemberType type) {
		if(members.containsKey(role.getRoleId())) {
			throw new InternalBugException("Duplicate to add league member: " + role.getRoleId());
		}
		if(getTotalMember() >= getMaxMember()) {
			throw new InternalBugException("Exceed member limit: " + getMaxMember() + ", current: " + getTotalMember());
		}
		
		LeagueMember member = new LeagueMember();
		member.setRoleId(role.getRoleId());
		member.setType(type);
		members.put(role.getRoleId(), member);
	}
	
	public int getTotalMember() {
		return members.size();
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

	public int getMaxMember() {
		return maxMember;
	}

	public void setMaxMember(int maxMember) {
		this.maxMember = maxMember;
	}
	
}
