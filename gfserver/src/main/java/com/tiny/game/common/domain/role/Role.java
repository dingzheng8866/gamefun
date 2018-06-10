package com.tiny.game.common.domain.role;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.exception.InternalBugException;

/**
 * gm:
 * add itemkey value
 * set itemkey value
 * del itemkey value
 * 
 * user:
 * use itemkey value (del)
 * get itemkey value (add)
 * setting: set itemkey value (set)
 */
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
	
	private Map<String, OwnItem> items = new ConcurrentHashMap<String, OwnItem>();
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Role)) {
			return false;
		}
		
		return roleId.equals(((Role) o).roleId);
	}
	
	public Collection<OwnItem> getOwnItems(){
		return items.values();
	}
	
	public OwnItem getRoleOwnItem(String ownKey) {
		return items.get(ownKey);
	}
	
	public void addRoleOwnItem(OwnItem item) {
		OwnItem oldItem = items.get(item.getKey());
		if(oldItem==null) {
			oldItem = item;
			items.put(item.getKey(), oldItem);
		} else {
			boolean isAccumulative = oldItem.getItem().isAccumulative();
			if(isAccumulative){
				int newCount = oldItem.getValue() + item.getValue();
				oldItem.setValue(newCount);
				// TODO: check count < 0?
				// TODO: how about attr?
			} else {
				oldItem.setValue(item.getValue());
			}
		}
	}
	
	public void setRoleOwnItem(OwnItem item) {
		OwnItem oldItem = items.get(item.getKey());
		if(oldItem==null) {
			oldItem = item;
			items.put(item.getKey(), oldItem);
		} else {
			boolean isAccumulative = oldItem.getItem().isAccumulative();
			if(isAccumulative){
				throw new InternalBugException("Invalid method invoked, change setRoleOwnItem to addRoleOwnItem to item: " + item.getKey());
			}
			oldItem.setValue(item.getValue());
		}
	}
	
	public void deleteRoleOwnItem(OwnItem item) {
		OwnItem oldItem = items.get(item.getKey());
		if(oldItem!=null) {
			boolean isAccumulative = oldItem.getItem().isAccumulative();
			if(!isAccumulative){
				throw new InternalBugException("Invalid method invoked, item is not accumulative: " + item.getKey());
			} 
			int newCount = oldItem.getValue() - item.getValue();
			oldItem.setValue(newCount);
		}
	}
	
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
