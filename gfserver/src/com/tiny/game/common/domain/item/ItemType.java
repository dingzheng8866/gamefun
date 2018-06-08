package com.tiny.game.common.domain.item;

import com.tiny.game.common.exception.InternalBugException;

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
	
	public static ItemType valueOf(int val) {
		switch (val) {
		case 90001:
			return SysDonateGold;
		case 90002:
			return Gold;
		case 90003:
			return SysDonateDiamond;
		case 90004:
			return Diamond;	
		case 90005:
			return roleExp;					
		}

		throw new InternalBugException("Invalid item type: " + val);
	}
	
}
