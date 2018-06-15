package com.tiny.game.common.server.main;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.cmd.NetCmdFactory;
import com.tiny.game.common.server.AbstractGameServer;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.broadcast.RouterService;
import com.tiny.game.common.server.gate.GateServer;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;
import game.protocol.protobuf.GameProtocol.I_RouteMessage;

public class MainGameServer extends AbstractGameServer {

	public MainGameServer(String propPath, String serverTag){
		super(propPath, serverTag);
		isNeedToRegisterToProxyServer = true;
		isNeedToRegisterToGateServer = true;	
	}
	
	public static void main(String[] args) throws Exception {
		MainGameServer server = new MainGameServer("resources/game_server.properties", MainGameServer.class.getSimpleName());
		server.start();
		
//		Thread.currentThread().sleep(2000);
//		// test
//		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), C_GetLoginServerInfo.newBuilder().build());
		
//		NetCmd errorCmd = NetCmdFactory.factoryCmdS_ErrorInfo(GameConst.Error_AnotherDeviceLogin, null);
//		I_RouteMessage req = NetMessageUtil.buildRouteMessage(errorCmd, ServerContext.getInstance().getServerUniqueTag(), "123456");
//		RouterService.routeToTarget(req);
		
	}

}
