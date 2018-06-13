package com.tiny.game.common.domain.item;

public class RoleInitItem {

	private ItemId itemId;
	private int value;
	private int level = 1;

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

}
