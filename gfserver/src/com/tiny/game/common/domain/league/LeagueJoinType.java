package com.tiny.game.common.domain.league;

public enum LeagueJoinType {
	Any(0), Approve(1), Disable(2);
	private int value = 0;
	private LeagueJoinType(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	public static LeagueJoinType valueOf(int val) {
		switch (val) {
		case 0:
			return Any;
		case 1:
			return Approve;
		case 2:
			return Disable;
		}

		return Disable;
	}
}
