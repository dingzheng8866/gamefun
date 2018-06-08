package com.tiny.game.common.server.gate;

import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.AbstractGameServer;
import com.tiny.game.common.server.main.MainGameServer;

import game.protocol.protobuf.GameProtocol.C_Heartbeat;

public class GateServer extends AbstractGameServer {

	public static void main(String[] args) throws Exception {
		GateServer server = new GateServer();
		server.serverProp = "resources/gate_server.properties";
		
		server.start();
		
		Thread.currentThread().sleep(10000);
		
		NetSession session = NetSessionManager.getInstance().getSession(MainGameServer.class.getSimpleName());
		if(session!=null) {
			NetCmd msg = new NetCmd(C_Heartbeat.newBuilder().build());
			NetLayerManager.getInstance().asyncSendOutboundMessage(session, msg);
		}
		
	}

}
