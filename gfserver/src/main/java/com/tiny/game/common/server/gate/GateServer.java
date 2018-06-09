package com.tiny.game.common.server.gate;

import com.tiny.game.common.server.AbstractGameServer;

public class GateServer extends AbstractGameServer {

	public GateServer(String propPath, String serverTag){
		super(propPath, serverTag);
	}
	
	public static void main(String[] args) throws Exception {
		new GateServer("resources/gate_server.properties", GateServer.class.getSimpleName()).start();
	}

}
