package com.tiny.game.common.server.main;

import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.server.AbstractGameServer;
import com.tiny.game.common.server.ContextParameter;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.gate.GateServer;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;
import game.protocol.protobuf.GameProtocol.C_Heartbeat;
import game.protocol.protobuf.GameProtocol.C_RegisterClient;

public class MainGameServer extends AbstractGameServer {

	public static void main(String[] args) throws Exception {
		MainGameServer server = new MainGameServer();
		server.serverProp = "resources/game_server.properties";
		
		server.start();
		
		Thread.currentThread().sleep(1000);
		
		NetClientManager.getInstance().addConnectTarget(GateServer.class.getSimpleName(), "127.0.0.1", 7777);
		NetClientManager.getInstance().start();
		
		Thread.currentThread().sleep(1000);
		
		C_RegisterClient.Builder builder = C_RegisterClient.newBuilder();
		builder.setClientType(MainGameServer.class.getSimpleName());
		builder.setTag("game.server.ip");
		builder.setParameter1("127.0.0.1");
		builder.setParameter2(ServerContext.getInstance().getProperty(ContextParameter.NET_SERVER_LISTEN_PORT).trim());
		
		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), builder.build());
		Thread.currentThread().sleep(1000);
		
		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), C_GetLoginServerInfo.newBuilder().build());
		
	}

}
