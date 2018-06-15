package com.tiny.game.common.net;

import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.server.gate.GateServer;
import com.tiny.game.common.server.main.MainGameServer;

import game.protocol.protobuf.GameProtocol.C_RoleLogin;
import game.protocol.protobuf.GameProtocol.S_LoginServerInfo;

public class NetTestCallback {

	private static NetTestCallback instance = new NetTestCallback();
	private NetTestCallback() {
	}

	public static NetTestCallback getInstance() {
		return instance;
	}
	
	private C_RoleLogin buildC_RoleLogin() {
		C_RoleLogin.Builder builder = C_RoleLogin.newBuilder();
		builder.setAccount("");
		builder.setChannel(1);
		builder.setDeviceId("device123");
		builder.setDeviceInfo("xiaomi");
		builder.setLoginAccountId("device123");
		builder.setPlatform("android");
		builder.setSdkInfo("");
		return builder.build();
	}
	
	
	public void onGotS_LoginServerInfo(S_LoginServerInfo info) throws Exception {
		NetClientManager.getInstance().shutdown();
		NetClientManager.getInstance().addConnectTarget(MainGameServer.class.getSimpleName(), info.getIpAddress(), info.getPort());
		NetClientManager.getInstance().start(null);
		Thread.currentThread().sleep(1000);
		C_RoleLogin loginReq = buildC_RoleLogin();
		NetClientManager.getInstance().sendMsg(MainGameServer.class.getSimpleName(), loginReq);
	}
	
}
