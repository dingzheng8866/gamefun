package com.tiny.game.common.domain.role.setting;


public enum LanguageType {

	English(0), SimpleCN(1), TraditionalCN(2), Japan(3);
	private int value = 0;
	private LanguageType(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	public static LanguageType valueOf(int val) {
		switch (val) {
		case 0:
			return English;
		case 1:
			return SimpleCN;
		case 2:
			return TraditionalCN;
		case 3:
			return Japan;			
		}

		return SimpleCN;
	}
	
}
