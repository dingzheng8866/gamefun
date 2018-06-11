package com.tiny.game.common.server.match;

import com.tiny.game.common.server.AbstractGameServer;

public class MatchServer extends AbstractGameServer {

	public MatchServer(String propPath, String serverTag){
		super(propPath, serverTag);
	}
	
	public static void main(String[] args) throws Exception {
		MatchServer server = new MatchServer("resources/match_server.properties", MatchServer.class.getSimpleName());
		server.start();
	}

}
