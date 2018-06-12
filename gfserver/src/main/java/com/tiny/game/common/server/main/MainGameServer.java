package com.tiny.game.common.server.main;

import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.server.AbstractGameServer;
import com.tiny.game.common.server.gate.GateServer;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;

public class MainGameServer extends AbstractGameServer {

	public MainGameServer(String propPath, String serverTag){
		super(propPath, serverTag);
		isNeedToRegisterToProxyServer = true;
		isNeedToRegisterToGateServer = true;	
	}
	
	public static void main(String[] args) throws Exception {
		MainGameServer server = new MainGameServer("resources/game_server.properties", MainGameServer.class.getSimpleName());
		server.start();
		
//		Thread.currentThread().sleep(10000);
//		// test
//		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), C_GetLoginServerInfo.newBuilder().build());
		
	}

}
