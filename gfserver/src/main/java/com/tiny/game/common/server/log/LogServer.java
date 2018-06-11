package com.tiny.game.common.server.log;

import com.tiny.game.common.server.AbstractGameServer;

public class LogServer extends AbstractGameServer {

	public LogServer(String propPath, String serverTag){
		super(propPath, serverTag);
	}
	
	public static void main(String[] args) throws Exception {
		LogServer server = new LogServer("resources/log_server.properties", LogServer.class.getSimpleName());
		server.start();
	}

}
