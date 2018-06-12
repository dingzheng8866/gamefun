package com.tiny.game.common.server.conf;

import com.tiny.game.common.server.AbstractGameServer;

public class ConfigServer extends AbstractGameServer {

	public ConfigServer(String propPath, String serverTag){
		super(propPath, serverTag);
		isNeedToRegisterToProxyServer = true;
		isNeedToRegisterToGateServer = false;	
	}
	
	public static void main(String[] args) throws Exception {
		ConfigServer server = new ConfigServer("resources/conf_server.properties", ConfigServer.class.getSimpleName());
		server.start();
	}

}
