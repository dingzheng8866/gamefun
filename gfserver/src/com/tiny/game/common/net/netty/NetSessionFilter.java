package com.tiny.game.common.net.netty;


public interface NetSessionFilter {

	public boolean onInitNetSession(NetSession session);
	public void onCloseNetSession(NetSession session);
	
	// TODO: filter message 
	
}
