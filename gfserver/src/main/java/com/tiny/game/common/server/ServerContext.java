package com.tiny.game.common.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.util.IdGenerator;

public class ServerContext implements ContextParameter {
	
	private static final Logger logger = LoggerFactory.getLogger(ServerContext.class);
	
	private static ServerContext instance = new ServerContext();
	
	private String defaultCommonConfig = "resources/common.properties";
	protected String configPath;
	
	protected Properties properties = new Properties();
	
	private int fightFPS = 20;
	private String localExternalNetworkIp = null;
	private String localInternalNetworkIp = null;
	private int userIdLen = 12;
	private int userNameLen = 12;
	
	private String serverUniqueTag = "";
	
	private AbstractGameServer gameServer = null;
	
	private boolean gmEnable = false;
	
	private ServerContext() {
		List<String> ipList = NetUtils.getNetworkIpAddress();
		localExternalNetworkIp = NetUtils.getLocalExternalNetworkAddress(ipList);
		localInternalNetworkIp = NetUtils.getLocalInternalNetworkAddress(ipList);
	}
	
	public static ServerContext getInstance() {
		return instance;
	}
	
	public String getServerUniqueTag(){
		return serverUniqueTag;
	}
	
	public int getUserIdLen(){
		return userIdLen;
	}
	
	public int getUserNameLen(){
		return userNameLen;
	}
	
	public String getExternalIp(){
		return localExternalNetworkIp;
	}
	
	public String getLocalAnyIp(){
		if(localExternalNetworkIp!=null){
			return localExternalNetworkIp;
		}
		return localInternalNetworkIp;
	}
	
	public boolean isGmEnable(){
		return gmEnable;
	}
	
	private void init(String serverTagPrefix) {
		fightFPS = getPropertyInt(BATTLE_FPS, "20");
		userIdLen = getPropertyInt(USERID_LEN, "12");
		userNameLen = getPropertyInt(USERNAME_LEN, "12");
		gmEnable = getPropertyBoolean(GM_ENABLE, false);
	}
	
	private void loadProp(String file, String serverTagPrefix) {
		try {
			BufferedReader propReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			properties.load(propReader);
			init(serverTagPrefix);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load prop: " + file +", error:" + e.getMessage(), e);
		}
	}
	
	public ServerContext load(String configPath, String serverTagPrefix) {
		return load(configPath, true, serverTagPrefix);
	}
	
	public ServerContext load(String configPath, boolean loadCommonFile, String serverTagPrefix) {
		this.configPath = configPath;
		if(loadCommonFile) {
			loadProp(defaultCommonConfig,serverTagPrefix);
		}
		loadProp(configPath,serverTagPrefix);
		serverUniqueTag = IdGenerator.genServerTagUniqueId(serverTagPrefix);
		logger.info("serverUniqueTag: "+serverUniqueTag+", localExternalNetworkIp: " + localExternalNetworkIp +", localInternalNetworkIp: " + localInternalNetworkIp);
		return this;
	}
	
	public String getConfigPath() {
		return configPath;
	}
	
	public String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public int getPropertyInt(String key, String def) {
		return Integer.parseInt(getProperty(key, def));
	}
	
	public boolean getPropertyBoolean(String key, boolean def) {
		return Boolean.parseBoolean(getProperty(key, def?"true":"false"));
	}
	
	public int getPropertyInt(String key) {
		return Integer.parseInt(getProperty(key));
	}
	
	public float getPropertyFloat(String key) {
		return Float.parseFloat(getProperty(key));
	}
	
	public String getProperty(String key, String def) {
		return properties.getProperty(key, def);
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	public int getBattleLogicFrameFPS() {
		return fightFPS;
	}

	public AbstractGameServer getGameServer() {
		return gameServer;
	}

	public void setGameServer(AbstractGameServer gameServer) {
		this.gameServer = gameServer;
	}
	
}
