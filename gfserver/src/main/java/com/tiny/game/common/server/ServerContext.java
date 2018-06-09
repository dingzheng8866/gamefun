package com.tiny.game.common.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import com.tiny.game.common.net.NetUtil;

public class ServerContext {
	
	private static ServerContext instance = new ServerContext();
	
	private String defaultCommonConfig = "resources/common.properties";
	protected String configPath;
	
	protected Properties properties = new Properties();
	
	private int fps = 20;
	private String localExternalNetworkIp = null;
	private String localInternalNetworkIp = null;
	
	private ServerContext() {
		List<String> ipList = NetUtil.getNetworkIpAddress();
		localExternalNetworkIp = NetUtil.getLocalExternalNetworkAddress(ipList);
		localInternalNetworkIp = NetUtil.getLocalInternalNetworkAddress(ipList);
	}
	
	public static ServerContext getInstance() {
		return instance;
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
	
	private void init() {
		String s = getProperty("Battle.FPS");
		if(s!=null && s.trim().length() > 0) {
			fps = Integer.parseInt(s);
		}
	}
	
	private void loadProp(String file) {
		try {
			BufferedReader propReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			properties.load(propReader);
			init();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load prop: " + file +", error:" + e.getMessage(), e);
		}
	}
	
	public ServerContext load(String configPath) {
		return load(configPath, true);
	}
	
	public ServerContext load(String configPath, boolean loadCommonFile) {
		this.configPath = configPath;
		if(loadCommonFile) {
			loadProp(defaultCommonConfig);
		}
		loadProp(configPath);
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
	
	public int getFPS() {
		return fps;
	}

	
	
}
