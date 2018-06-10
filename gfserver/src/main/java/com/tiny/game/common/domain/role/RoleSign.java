package com.tiny.game.common.domain.role;

import com.tiny.game.common.domain.item.ItemId;

public class RoleSign {

	private int day;
	private ItemId itemId;
	private int itemCount;
	
	public String getKey(){
		return day+"";
	}
	
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public ItemId getItemId() {
		return itemId;
	}
	public void setItemId(ItemId itemId) {
		this.itemId = itemId;
	}
	public int getItemCount() {
		return itemCount;
	}
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}
	
}
