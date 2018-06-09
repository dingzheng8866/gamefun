package com.tiny.game.common.server.main;

import com.tiny.game.common.server.AbstractGameServer;

public class MainGameServer extends AbstractGameServer {

	public MainGameServer(String propPath, String serverTag){
		super(propPath, serverTag);
	}
	
	public static void main(String[] args) throws Exception {
		MainGameServer server = new MainGameServer("resources/game_server.properties", MainGameServer.class.getSimpleName());
		server.start();
	}

}
