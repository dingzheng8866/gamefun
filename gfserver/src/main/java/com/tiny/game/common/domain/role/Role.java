package com.tiny.game.common.domain.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.domain.role.setting.RoleSettingBean;

public class Role {

	private String roleId;
//	private String userId;

//	private String headIcon;
//	private String name;
//
//	private int level;
//	private int levelExp;
//	
//	private int systemDonategolds; 
//	private int golds;
//
//	private int systemDonateDiamonds;
//	private int diamonds;
//	
//	private int point;  // 积分
//
//	private RoleBattleInfoBean battleInfo;

//	private RoleSettingBean setting = new RoleSettingBean();
	
	private Map<Integer, RoleOwnItem> items = new ConcurrentHashMap<Integer, RoleOwnItem>();
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Role)) {
			return false;
		}
		
		return roleId.equals(((Role) o).roleId);
	}
	
	public RoleOwnItem getRoleOwnItem(int itemId) {
		return items.get(itemId);
	}
	
	public void addRoleOwnItem(RoleOwnItem item) {
		RoleOwnItem oldItem = items.get(item.getItem().getId());
		if(oldItem==null) {
			oldItem = item;
			items.put(item.getItem().getId().getValue(), oldItem);
		} else {
//			oldItem.
		}
	}
	
//	public void deleteRoleOwnItem(RoleOwnItem item) {
//		List<RoleOwnItem> list = items.get(item.getItem().getType());
//		if(list!=null) {
//			list.remove(item);
//		}
//	}
	
	// TODO: remove by count
	
//	public int getAllGolds() {
//		return getSystemDonategolds()+getGolds();
//	}
//	
//	public int getAllDiamonds() {
//		return getSystemDonateDiamonds()+getDiamonds();
//	}
//	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

//	public String getHeadIcon() {
//		return headIcon;
//	}
//
//	public void setHeadIcon(String headIcon) {
//		this.headIcon = headIcon;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}

//	public int getLevel() {
//		return level;
//	}
//
//	public void setLevel(int level) {
//		this.level = level;
//	}

//	public int getLevelExp() {
//		return levelExp;
//	}
//
//	public void setLevelExp(int levelExp) {
//		this.levelExp = levelExp;
//	}

//	public int getSystemDonategolds() {
//		return systemDonategolds;
//	}

//	public void setSystemDonategolds(int systemDonategolds) {
//		this.systemDonategolds = systemDonategolds;
//	}

//	public int getGolds() {
//		return golds;
//	}

//	public void setGolds(int golds) {
//		this.golds = golds;
//	}

//	public int getSystemDonateDiamonds() {
//		return systemDonateDiamonds;
//	}

//	public void setSystemDonateDiamonds(int systemDonateDiamonds) {
//		this.systemDonateDiamonds = systemDonateDiamonds;
//	}

//	public int getDiamonds() {
//		return diamonds;
//	}

//	public void setDiamonds(int diamonds) {
//		this.diamonds = diamonds;
//	}

//	public int getPoint() {
//		return point;
//	}
//
//	public void setPoint(int point) {
//		this.point = point;
//	}

//	public RoleBattleInfoBean getBattleInfo() {
//		return battleInfo;
//	}
//
//	public void setBattleInfo(RoleBattleInfoBean battleInfo) {
//		this.battleInfo = battleInfo;
//	}

//	public RoleSettingBean getSetting() {
//		return setting;
//	}
//
//	public void setSetting(RoleSettingBean setting) {
//		this.setting = setting;
//	}
	
	
}
