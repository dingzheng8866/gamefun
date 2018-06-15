package com.tiny.game.common.net;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.dao.db.druid.DruidManager;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.gate.GateServer;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;
import game.protocol.protobuf.GameProtocol.C_RoleLogin;

public class NetTest {

	@BeforeClass
	public static void setUp() throws Exception {
		LocalConfManager.getInstance().load();
		ServerContext.getInstance().load("resources/test_client.properties", "TestServer");
		NetLayerManager.getInstance().init(ServerContext.getInstance());
	}
	
	
	@Test
	public void testRoleAction() throws Exception {
		C_GetLoginServerInfo getLoginServerReq = C_GetLoginServerInfo.newBuilder().build();

		NetClientManager.getInstance().addConnectTarget(GateServer.class.getSimpleName(), "127.0.0.1", 17981);
		NetClientManager.getInstance().start(null);
		
		Thread.currentThread().sleep(1000);
		
		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), getLoginServerReq);
		
		Thread.currentThread().sleep(100000);
	}
	
}
