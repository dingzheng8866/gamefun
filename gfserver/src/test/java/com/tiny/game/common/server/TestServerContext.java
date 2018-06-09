package com.tiny.game.common.server;

import static org.junit.Assert.*;

import org.junit.Test;

import com.tiny.game.common.server.ServerContext;


public class TestServerContext {

	@Test
	public void testIp(){
		String ip = ServerContext.getInstance().getLocalAnyIp();
		System.out.println(ip);
		assertNotNull(ip);
		ip = ServerContext.getInstance().getExternalIp();
		System.out.println(ip);
	}
	
}
