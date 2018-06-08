package com.tiny.game.common.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.dao.druid.DruidManager;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.net.cmd.NetCmdProcessorFactory;


public class AbstractGameServer {

	private static Logger logger = LoggerFactory.getLogger(AbstractGameServer.class);
	
	protected long startTime = System.currentTimeMillis();
	protected String serverProp = ""; //resources/login_server.properties
	protected boolean enableRDB = true;
	
	public void start() {
		logger.info("Load server property: " + serverProp);
		ServerContext.getInstance().load(serverProp);
		
//		if(enableRDB) {
//			logger.info("Connect to database");
//			DruidManager.getInstance().initDB(ServerContext.getInstance());
//		}
		
		// put net layer init at last
		NetLayerManager.getInstance().init(ServerContext.getInstance());
	}
	
	
}
