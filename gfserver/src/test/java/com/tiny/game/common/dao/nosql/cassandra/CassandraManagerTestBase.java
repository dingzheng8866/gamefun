package com.tiny.game.common.dao.nosql.cassandra;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.server.ServerContext;

public class CassandraManagerTestBase {

	@BeforeClass
	public static void setUp() throws Exception {
		LocalConfManager.getInstance().load();
		ServerContext.getInstance().load("resources/game_server.properties", "TestServer");
		CassandraManager.getInstance().initDB(ServerContext.getInstance());
	}

	@AfterClass
	public static void tearDown() throws Exception {
		CassandraManager.getInstance().shutdown();
	}
	
}
