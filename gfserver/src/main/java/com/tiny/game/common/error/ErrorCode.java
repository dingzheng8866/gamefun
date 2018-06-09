package com.tiny.game.common.error;

public enum ErrorCode {

	Error_InternalBug(1), 
	Error_NoActiveGameServer(2), 
	Error_NoActiveProxyServer(3), 
	Error_NoActiveMatchServer(4),
	Error_NoActiveFightServer(5);
	
	private int value = 0;
	private ErrorCode(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	
}
