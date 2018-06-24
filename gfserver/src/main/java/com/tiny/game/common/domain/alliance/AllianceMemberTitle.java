package com.tiny.game.common.domain.alliance;

public enum AllianceMemberTitle {
	Leader(3), CoLeader(2), Elder(1), Member(0);
	private int value = 0;
	private AllianceMemberTitle(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	public static AllianceMemberTitle valueOf(int val) {
		switch (val) {
		case 3:
			return Leader;
		case 2:
			return CoLeader;
		case 1:
			return Elder;
		case 0:
			return Member;			
		}

		return Member;
	}
	
}
