package com.tiny.game.common.domain.role;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tiny.game.common.domain.item.ItemType;
import com.tiny.game.common.domain.league.LeagueBean;
import com.tiny.game.common.domain.role.setting.RoleSettingBean;

public class RoleBean {

	private String roleId;
	private String userId;

	private String headIcon;
	private String name;

	private int level;
	private int levelExp;
	
	private int systemDonategolds; 
	private int golds;

	private int systemDonateDiamonds;
	private int diamonds;
	
	private int point;  // 积分

	private String leagueId;

	private RoleBattleInfoBean battleInfo;

	private RoleSettingBean setting = new RoleSettingBean();
	
	private Map<ItemType, List<RoleOwnItem>> items = new HashMap<ItemType, List<RoleOwnItem>>();
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof RoleBean)) {
			return false;
		}
		
		return roleId.equals(((RoleBean) o).roleId);
	}
	
	public RoleOwnItem getRoleOwnItem(ItemType type) {
		List<RoleOwnItem> list = items.get(type);
		if(list == null || list.size() < 1) {
			return null;
		}
		return list.get(0);
	}
	
	public void addRoleOwnItem(RoleOwnItem item) {
		List<RoleOwnItem> list = items.get(item.getItem().getType());
		if(list==null) {
			list = new ArrayList<RoleOwnItem>();
			items.put(item.getItem().getType(), list);
		}
		list.add(item);
	}
	
	public void deleteRoleOwnItem(RoleOwnItem item) {
		List<RoleOwnItem> list = items.get(item.getItem().getType());
		if(list!=null) {
			list.remove(item);
		}
	}
	
	// TODO: remove by count
	
	public int getAllGolds() {
		return systemDonategolds+golds;
	}
	
	public int getAllDiamonds() {
		return systemDonateDiamonds+diamonds;
	}
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevelExp() {
		return levelExp;
	}

	public void setLevelExp(int levelExp) {
		this.levelExp = levelExp;
	}

	public int getSystemDonategolds() {
		return systemDonategolds;
	}

	public void setSystemDonategolds(int systemDonategolds) {
		this.systemDonategolds = systemDonategolds;
	}

	public int getGolds() {
		return golds;
	}

	public void setGolds(int golds) {
		this.golds = golds;
	}

	public int getSystemDonateDiamonds() {
		return systemDonateDiamonds;
	}

	public void setSystemDonateDiamonds(int systemDonateDiamonds) {
		this.systemDonateDiamonds = systemDonateDiamonds;
	}

	public int getDiamonds() {
		return diamonds;
	}

	public void setDiamonds(int diamonds) {
		this.diamonds = diamonds;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}

	public RoleBattleInfoBean getBattleInfo() {
		return battleInfo;
	}

	public void setBattleInfo(RoleBattleInfoBean battleInfo) {
		this.battleInfo = battleInfo;
	}

	public RoleSettingBean getSetting() {
		return setting;
	}

	public void setSetting(RoleSettingBean setting) {
		this.setting = setting;
	}
	
	
}
