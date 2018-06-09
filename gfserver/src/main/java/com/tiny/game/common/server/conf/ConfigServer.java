package com.tiny.game.common.server.conf;

import com.tiny.game.common.server.AbstractGameServer;

public class ConfigServer extends AbstractGameServer {

	public static void main(String[] args) throws Exception {
		ConfigServer server = new ConfigServer();
		server.serverProp = "resources/conf_server.properties";
		
		server.start();
		
	}

}
