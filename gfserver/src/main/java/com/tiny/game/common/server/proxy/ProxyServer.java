package com.tiny.game.common.server.proxy;

import com.tiny.game.common.server.AbstractGameServer;

public class ProxyServer extends AbstractGameServer {

	public ProxyServer(String propPath, String serverTag){
		super(propPath, serverTag);
	}
	
	public static void main(String[] args) throws Exception {
		ProxyServer server = new ProxyServer("resources/proxy_server.properties", ProxyServer.class.getSimpleName());
		server.start();
	}

}
