package com.tiny.game.common.domain.item;

public enum ItemType {

	SysDonateGold(90001), 
	Gold(90002), 
	SysDonateDiamond(90003), 
	Diamond(90004),
	roleExp(90005);
	
	private int value = 0;
	private ItemType(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	
}
