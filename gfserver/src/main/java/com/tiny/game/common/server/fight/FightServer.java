package com.tiny.game.common.server.fight;

import com.tiny.game.common.server.AbstractGameServer;

public class FightServer extends AbstractGameServer {

	public FightServer(String propPath, String serverTag){
		super(propPath, serverTag);
	}
	
	public static void main(String[] args) throws Exception {
		FightServer server = new FightServer("resources/fight_server.properties", FightServer.class.getSimpleName());
		server.start();
	}

}
