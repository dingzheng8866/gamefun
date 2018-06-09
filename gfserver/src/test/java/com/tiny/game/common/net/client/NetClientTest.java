package com.tiny.game.common.net.client;

import org.junit.Test;

import com.tiny.game.common.server.ContextParameter;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.gate.GateServer;
import com.tiny.game.common.server.main.MainGameServer;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;
import game.protocol.protobuf.GameProtocol.C_RegisterClient;

public class NetClientTest {

	@Test
	public void test(){
//		NetClientManager.getInstance().addConnectTarget(GateServer.class.getSimpleName(), "127.0.0.1", 7777);
//		NetClientManager.getInstance().start();
//		
//		C_RegisterClient.Builder builder = C_RegisterClient.newBuilder();
//		builder.setClientType(MainGameServer.class.getSimpleName());
//		builder.setTag("game.server.ip");
//		builder.setParameter1("127.0.0.1");
//		builder.setParameter2(ServerContext.getInstance().getProperty(ContextParameter.NET_SERVER_LISTEN_PORT).trim());
//		
//		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), builder.build());
//		Thread.currentThread().sleep(1000);
//		
//		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), C_GetLoginServerInfo.newBuilder().build());
	}
	
}
