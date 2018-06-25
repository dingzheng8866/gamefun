package com.tiny.game.common.domain.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tiny.game.common.GameConst;
import com.tiny.game.common.domain.item.Item;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.item.LevelItem;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.server.main.bizlogic.role.RoleUtil;
import com.tiny.game.common.util.GameUtil;
import com.tiny.game.common.util.IdGenerator;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.S_RoleData;

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
	
	private Date lastUpdateTime = null;
	
	public byte[] toBinData(){
		return NetMessageUtil.convertRole(this).toByteArray();
	}
	
	public static Role toRole(byte[] bin){
		try {
			S_RoleData data = S_RoleData.PARSER.parseFrom(bin, 0, bin.length);
			return NetMessageUtil.convertS_RoleData(data);
		} catch (InvalidProtocolBufferException e) {
			throw new InternalBugException("Failed to parse role bin data: " + e.getMessage(), e);
		}
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Role: " + roleId);
		for(OwnItem item : items.values()){
			sb.append(","+item.toString());
		}
		return sb.toString();
	}
	
	public boolean equals(Object o) {
		if(o==null || !(o instanceof Role)) {
			return false;
		}
		
		return roleId.equals(((Role) o).roleId);
	}
	
	public void setRoleName(String roleName) {
		OwnItem item = getOwnItem(ItemId.roleName);
		if(item == null) {
			item = RoleUtil.buildOwnItem(ItemId.roleName, 1, 1);
			setOwnItem(item);
		}
		item.setExtendAttrValue(GameConst.ITEM_ATTR_ROLE_NAME, roleName);
	}
	
	public String getRoleName() {
		OwnItem item = getOwnItem(ItemId.roleName);
		if(item == null) {
			return "";
		}
		return item.getExtendProp(GameConst.ITEM_ATTR_ROLE_NAME);
	}
	
	public Collection<OwnItem> getOwnItems(){
		return items.values();
	}
	
	public int getSystemDonategolds() {
		return getOwnItemValue(ItemId.systemDonateGold);
	}

	public int getBuyGolds() {
		return getOwnItemValue(ItemId.buyGold);
	}
	
	public int getSystemDonateGems() {
		return getOwnItemValue(ItemId.systemDonateGem);
	}

	public int getBuyGems() {
		return getOwnItemValue(ItemId.buyGem);
	}
	
	public int getAllGolds() {
		return getSystemDonategolds()+getBuyGolds();
	}
	
	public int getAllGems() {
		return getSystemDonateGems()+getBuyGems();
	}
	
	public int getLevel(){
		return getOwnItemValue(ItemId.roleLevel);
	}
	
	public int getLeaguePrize(){
		return getOwnItemValue(ItemId.leaguePrize);
	}
	
//	private OwnItem getOwnItem(String ownKey) {
//		return items.get(ownKey);
//	}
	
	public OwnItem getOwnItem(ItemId itemId) {
		return getOwnItem(itemId, 1);
	}
	
	public OwnItem getOwnItem(ItemId itemId, int level) {
		return items.get(LevelItem.getKey(itemId, level));
	}
	
	public int getOwnItemValue(ItemId itemId){
		OwnItem oi = getOwnItem(itemId);
		return oi==null ? 0 : oi.getValue();
	}
	
	public int getOwnItemValue(ItemId itemId, int level){
		OwnItem oi = getOwnItem(itemId, level);
		return oi==null ? 0 : oi.getValue();
	}
	
	public void addOwnItem(OwnItem item) {
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
				
				// handle some special logic like exp
				if(item.getItem().getItemId() == ItemId.roleExp){
					RoleService.fixupRoleExpChange(this);
				} 
			} else {
				oldItem.setValue(item.getValue());
			}
		}
	}
	
	public void setOwnItem(OwnItem item) {
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
	
	public void subOwnItem(OwnItem item) {
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
	
	public void removeOwnItem(String key) {
		items.remove(key);
	}
	
	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

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
	
	public boolean hasOwnItemMeetMinLevelCondition(ItemId itemId, int minLevel) {
		boolean flag = false;
		for(OwnItem ownItem:items.values()) {
			if(ownItem.getItem().getItemId() == itemId && (ownItem.getItem() instanceof LevelItem)) {
				LevelItem levelItem = (LevelItem)ownItem.getItem();
				if(levelItem.getLevel() >= minLevel) {
					return true;
				}
			}
		}
		return flag;
	}
	
	public List<OwnItem> getAllOwnItemsContainsAttrKey(String attrKey){
		List<OwnItem> list = new ArrayList<OwnItem>();
		for(OwnItem ownItem:items.values()) {
			if(ownItem.hasAttr(attrKey)){
				list.add(ownItem);
			}
		}
		return list;
	}
	
	public boolean hasItemContainsSubExtendAttributeValue(ItemId itemId, String attrName, String subValue){
		OwnItem ownItem = getOwnItem(itemId);
		if(ownItem!=null) {
			String itemValue = ownItem.getExtendProp(attrName);
			if(itemValue!=null){
				List<String> list = GameUtil.splitToStringList(itemValue, ",");
				if(list.contains(subValue)){
					return true;
				}
			}
		}
		return false;
	}
	
	public void addItemSubExtendAttributeValue(ItemId itemId, String attrName, String subValue){
		OwnItem ownItem = getOwnItem(itemId);
		if(ownItem==null) {
			addOwnItem(RoleUtil.buildOwnItem(itemId, 1, 1));
			ownItem = getOwnItem(itemId);
		}
		
		String itemValue = ownItem.getExtendProp(attrName);
		if(itemValue==null){
			itemValue = "";
		} else {
			itemValue += ",";
		}
		itemValue += subValue;
		ownItem.addExtendProp(attrName, itemValue);
	}
	
	
	public long getReqReinforceTimeInterval(){
		return GameConst.USER_REQ_REINFORCE_TIME_INTERVAL * 60 * 1000; // TODO: 
	}
	
}
