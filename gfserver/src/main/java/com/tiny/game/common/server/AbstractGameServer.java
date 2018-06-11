package com.tiny.game.common.server;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.dao.db.druid.DruidManager;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.client.NetClientManager;
import com.tiny.game.common.server.gate.GateServer;
import com.tiny.game.common.server.main.MainGameServer;
import com.tiny.game.common.util.GameUtil;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;
import game.protocol.protobuf.GameProtocol.C_RegisterClient;


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

		ServerContext.getInstance().setGameServer(this);
		// put net layer init at last
		NetLayerManager.getInstance().init(ServerContext.getInstance());
		logger.info("Started server: " + getClass()+"," + ServerContext.getInstance().getServerUniqueTag());
		
		onStart();
	}
	
	protected void onStart(){
		
	}
	
	protected void startNetClient(){
		
		String gateServers = ServerContext.getInstance().getProperty("gateserver.hosts");
		if(gateServers!=null && gateServers.trim().length() > 0){
			gateServers = gateServers.trim();
			List<String> gateServerList = GameUtil.splitToStringList(gateServers, ",");
			int gateServerPort = ServerContext.getInstance().getPropertyInt("gateserver.port");
			for(String serverIp : gateServerList){
				NetClientManager.getInstance().addConnectTarget(GateServer.class.getSimpleName(), serverIp, gateServerPort);
			}
			NetClientManager.getInstance().start();
		}
		
		
		C_RegisterClient.Builder builder = C_RegisterClient.newBuilder();
		builder.setClientType(MainGameServer.class.getSimpleName());
		builder.setClientUniqueId(ServerContext.getInstance().getServerUniqueTag());
		builder.setServerIp(ServerContext.getInstance().getLocalAnyIp());
		builder.setServerPort(ServerContext.getInstance().getProperty(ContextParameter.NET_SERVER_LISTEN_PORT).trim());
		
		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), builder.build());
		
		NetClientManager.getInstance().sendMsg(GateServer.class.getSimpleName(), C_GetLoginServerInfo.newBuilder().build());
	}
	
}
