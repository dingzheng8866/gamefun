package com.tiny.game.common.domain.item;

import com.tiny.game.common.exception.InternalBugException;

public enum ItemId {

    systemDonateGold(100000),
    buyGold(100001),
    systemDonateDiamond(100002),
    buyDiamond(100003),
    exp(100004),
    mainBase(100005),
    defenseTower(100006),
    monthCard(100007),
    seasonCard(100008),
    yearCard(100009),
    permanentCard(100010);
	
	private int value = 0;
	private ItemId(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	
	public static ItemId valueOf(int val) {
		switch (val) {
        case 100000: return systemDonateGold;
        case 100001: return buyGold;
        case 100002: return systemDonateDiamond;
        case 100003: return buyDiamond;
        case 100004: return exp;
        case 100005: return mainBase;
        case 100006: return defenseTower;
        case 100007: return monthCard;
        case 100008: return seasonCard;
        case 100009: return yearCard;
        case 100010: return permanentCard;


		}
		throw new InternalBugException();
	}	
}
