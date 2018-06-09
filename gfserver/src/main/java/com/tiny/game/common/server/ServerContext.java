package com.tiny.game.common.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import com.tiny.game.common.net.NetUtil;
import com.tiny.game.common.util.IdGenerator;

public class ServerContext implements ContextParameter {
	
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
	
	private ServerContext() {
		List<String> ipList = NetUtil.getNetworkIpAddress();
		localExternalNetworkIp = NetUtil.getLocalExternalNetworkAddress(ipList);
		localInternalNetworkIp = NetUtil.getLocalInternalNetworkAddress(ipList);
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
	
	private void init(String serverTagPrefix) {
		fightFPS = getPropertyInt(BATTLE_FPS, "20");
		userIdLen = getPropertyInt(USERID_LEN, "12");
		userNameLen = getPropertyInt(USERNAME_LEN, "12");
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
	
}
