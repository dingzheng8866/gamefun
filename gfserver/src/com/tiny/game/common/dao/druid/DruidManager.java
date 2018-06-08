package com.tiny.game.common.dao.druid;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.tiny.game.common.server.ServerContext;

public class DruidManager {
	
	private static Logger logger = LoggerFactory.getLogger(DruidManager.class);
	
	private DruidDataSource dataSource;
	
	private DruidDataSource statisDataSource;

	private static class SingletonHolder {
		private static DruidManager instance = new DruidManager();
	}

	public static DruidManager getInstance() {
		return SingletonHolder.instance;
	}

	public void initDB(ServerContext prop) {
		try {
			Map<String, String> druidProp = new HashMap<String, String>();
			
			druidProp.put("url", prop.getProperty("mysql.url"));
			druidProp.put("username", prop.getProperty("mysql.username"));
			druidProp.put("password", prop.getProperty("mysql.password"));
			druidProp.put("initialSize", "5");
			druidProp.put("maxActive", "30");
			druidProp.put("maxWait", "30000");
			druidProp.put("minIdle", "10");
			druidProp.put("timeBetweenEvictionRunsMillis", "30000");
			druidProp.put("minEvictableIdleTimeMillis", "30000");
			druidProp.put("validationQuery", "SELECT 1");
			druidProp.put("testWhileIdle", "true");
			druidProp.put("testOnBorrow", "false");
			druidProp.put("testOnReturn", "false");
			druidProp.put("filters", "stat");
			
			dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(druidProp);
		
			if(prop.getProperty("mysql.urlstatis")!=null) {
				druidProp.put("url", prop.getProperty("mysql.urlstatis"));
			}
			statisDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(druidProp);
		} catch (Exception e) {
			logger.error("init db fail!", e);
			throw new RuntimeException("init db fail!" + e.getMessage(), e);
		}
	}
	
	public DruidDataSource getDataSource() {
		return dataSource;
	}
	
	public DruidDataSource getStatisDataSource() {
		return statisDataSource;
	}
	
	public void shutdown() {
		dataSource.close();
	}

}
