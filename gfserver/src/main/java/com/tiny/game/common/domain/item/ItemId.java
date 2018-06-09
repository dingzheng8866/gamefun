package com.tiny.game.common.domain.item;

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
		ItemIdManager.setItemId(this);
	}
	public int getValue() {
		return value;
	}
	
}