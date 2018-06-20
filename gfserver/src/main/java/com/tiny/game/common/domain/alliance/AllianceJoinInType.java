package com.tiny.game.common.domain.alliance;

public enum AllianceJoinInType {

	Any(0), Approve(1), Reject(2);
	private int value = 0;
	private AllianceJoinInType(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	public static AllianceJoinInType valueOf(int val) {
		switch (val) {
		case 0:
			return Any;
		case 1:
			return Approve;
		case 2:
			return Reject;
		}

		return Any;
	}
	
}