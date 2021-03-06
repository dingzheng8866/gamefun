package com.tiny.game.common.dao.db.druid;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.dao.db.druid.DruidManager;
import com.tiny.game.common.server.ServerContext;

public class BaseDaoDBTest {

	@BeforeClass
	public static void setUp() throws Exception {
		LocalConfManager.getInstance().load();
		ServerContext.getInstance().load("resources/game_server.properties", "TestServer");
		DruidManager.getInstance().initDB(ServerContext.getInstance());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		DruidManager.getInstance().shutdown();
	}
	
}
