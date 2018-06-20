package com.tiny.game.common.domain.alliance;

public enum AllianceMemberTitle {
	Leader(0), CoLeader(1), Elder(2), Member(3);
	private int value = 0;
	private AllianceMemberTitle(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	public static AllianceMemberTitle valueOf(int val) {
		switch (val) {
		case 0:
			return Leader;
		case 1:
			return CoLeader;
		case 2:
			return Elder;
		case 3:
			return Member;			
		}

		return Member;
	}
	
}
