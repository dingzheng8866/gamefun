package com.tiny.game.common.domain.item;


public enum ItemCategory {
	Unknown(0), Army(1), Hero(2);
	private int value = 0;
	private ItemCategory(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	public static ItemCategory valueOf(int val) {
		switch (val) {
		case 0:
			return Unknown;
		case 1:
			return Army;
		case 2:
			return Hero;
		}

		return Unknown;
	}
}
