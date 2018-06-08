package com.tiny.game.common.domain.league;

public enum LeagueMemberType {
	Leader(0), CoLeader(1), Elder(2), Memeber(3);
	private int value = 0;
	private LeagueMemberType(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	public static LeagueMemberType valueOf(int val) {
		switch (val) {
		case 0:
			return Leader;
		case 1:
			return CoLeader;
		case 2:
			return Elder;
		case 3:
			return Memeber;			
		}

		return Memeber;
	}
}
