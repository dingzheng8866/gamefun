package com.tiny.game.common.domain.item;

import java.util.HashMap;
import java.util.Map;

public class RoleInitItem {

	private ItemId itemId;
	private int value;
	private int level = 1;

	protected Map<String, String> props = new HashMap<String, String>();
	
	public String getKey(){
		return Item.getKey(itemId, level);
	}
	
	public ItemId getItemId() {
		return itemId;
	}

	public void setItemId(ItemId itemId) {
		this.itemId = itemId;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Map<String, String> getProps() {
		return props;
	}

	public void setProps(Map<String, String> props) {
		this.props = props;
	}

	public void addAttr(String key, String value){
		props.put(key, value);
	}
	
}
