package com.tiny.game.common.error;

public enum ErrorCode {

	Error_InvalidRequestParameter(1001),
	Error_AnotherDeviceLogin(1002),
	Error_InternalBug(2001), 
	Error_NoActiveGameServer(2002), 
	Error_NoActiveProxyServer(2003), 
	Error_NoActiveMatchServer(2004),
	Error_NoActiveFightServer(2005);
	
	private int value = 0;
	private ErrorCode(int v) {
		this.value = v;
	}
	public int getValue() {
		return value;
	}
	
}
