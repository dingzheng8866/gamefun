package com.tiny.game.common.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.dao.db.druid.DruidManager;
import com.tiny.game.common.net.NetLayerManager;


public abstract class AbstractGameServer {

	private static Logger logger = LoggerFactory.getLogger(AbstractGameServer.class);
	
	protected long startTime = System.currentTimeMillis();
	protected String serverProp = ""; //resources/login_server.properties
	protected String serverTag = "Unknown";
	
	public AbstractGameServer(String propPath, String serverTag){
		this.serverProp = propPath;
		this.serverTag = serverTag;
	}
	
	public void start() {
		logger.info("Load server property: " + serverProp);
		ServerContext.getInstance().load(serverProp, serverTag);
		
		logger.info("Load local conf");
		LocalConfManager.getInstance().load();
		
		if(ServerContext.getInstance().getPropertyBoolean(ContextParameter.DAO_ENABLE_RDB, false)) {
			logger.info("Init RDB");
			DruidManager.getInstance().initDB(ServerContext.getInstance());
		}
		
		onStart();
		
		ServerContext.getInstance().setGameServer(this);
		// put net layer init at last
		NetLayerManager.getInstance().init(ServerContext.getInstance());
		logger.info("Started server: " + getClass()+"," + ServerContext.getInstance().getServerUniqueTag());
	}
	
	protected void onStart(){
		
	}
	
}
