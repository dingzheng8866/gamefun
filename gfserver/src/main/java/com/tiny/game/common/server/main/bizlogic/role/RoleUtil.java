package com.tiny.game.common.server.main.bizlogic.role;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.conf.item.ItemConfReader;
import com.tiny.game.common.conf.item.ItemLevelAttrConfReader;
import com.tiny.game.common.conf.item.RoleInitItemConfReader;
import com.tiny.game.common.domain.item.Item;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.item.LevelItem;
import com.tiny.game.common.domain.item.RoleInitItem;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.domain.role.OwnItem;

public class RoleUtil {

	private static final Logger logger = LoggerFactory.getLogger(RoleUtil.class);
	
	
	public static OwnItem buildOwnItem(ItemId itemId, int level, int value){
		Item item = null;
		if(level < 1){
			level = 1;
		}
		if(level > 0){
			item =  (LevelItem)LocalConfManager.getInstance().getConfReader(ItemLevelAttrConfReader.class).getConfBean(LevelItem.getKey(itemId, level));
		}
		if(item == null && level <=1){
			item = (Item)LocalConfManager.getInstance().getConfReader(ItemConfReader.class).getConfBean(Item.getKey(itemId, level));
		}
		
		if(item==null){
			throw new InternalBugException("Not found item: " + itemId +", level: " + level);
		}
		
		OwnItem ownItem = new OwnItem();
		
		ownItem.setItem(item);
		ownItem.setValue(value);
		return ownItem;
	}
	
	
	public static Role buildRole(String roleId){
		Role role = new Role();
		role.setRoleId(roleId);
		role.setLastUpdateTime(Calendar.getInstance().getTime());
		
		for(Object obj : LocalConfManager.getInstance().getConfReader(RoleInitItemConfReader.class).getAllConfBeans()){
			RoleInitItem initItem = (RoleInitItem) obj;
			OwnItem ownItem = buildOwnItem(initItem.getItemId(), initItem.getLevel(), initItem.getValue());
			role.addOwnItem(ownItem);
		}
		
		return role;
	}
	
}
