package com.tiny.game.common.server;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tiny.game.common.server.ServerContext;


public class TestServerContext {

	@BeforeClass
	public static void setUp() throws Exception {
		ServerContext.getInstance().load("resources/game_server.properties", "TestServer");
	}
	
	@Test
	public void testIp(){
		String ip = ServerContext.getInstance().getLocalAnyIp();
		System.out.println(ip);
		assertNotNull(ip);
		ip = ServerContext.getInstance().getExternalIp();
		System.out.println(ip);
	}

	@Test
	public void testServerUniqueId(){
		String tag = ServerContext.getInstance().getServerUniqueTag();
		System.out.println(tag);
	}
	
}
