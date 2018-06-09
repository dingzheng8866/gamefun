package com.tiny.game.common.dao.nosql.redis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.server.ServerContext;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class RedisManager {
	private static Logger logger = LoggerFactory.getLogger(RedisManager.class);
	
	private ShardedJedisPool jedisPool;
	
	private static class SingletonHolder {
		private static RedisManager instance = new RedisManager();
	}

	public static RedisManager getInstance() {
		return SingletonHolder.instance;
	}
	
	public void initDB(ServerContext prop) {
		try {
			logger.info("init redis");
			
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxIdle(Integer.parseInt(prop.getProperty("redis.pool.maxidle")));
			config.setMaxTotal(Integer.parseInt(prop.getProperty("redis.pool.maxsize")));
			config.setMaxWaitMillis(Integer.parseInt(prop.getProperty("redis.pool.timeout")));
			
//			//单机redis
//			jedisPool = new JedisPool(config, prop.getProperty("redis.host"), Integer.parseInt(prop.getProperty("redis.port")), 
//			Integer.parseInt(prop.getProperty("redis.pool.timeout")), prop.getProperty("redis.password"));
			
			// 分布式jedis
			List<JedisShardInfo> jedisInfoList = new ArrayList<JedisShardInfo>();
			int clusterSize = Integer.parseInt(prop.getProperty("redis.cluster.size"));
			for(int i = 1; i <= clusterSize; i++) {
				JedisShardInfo jedisInfo = new JedisShardInfo(prop.getProperty("redis.cluster.host" + i), Integer.parseInt(prop.getProperty("redis.cluster.port" + i)));
				jedisInfo.setPassword(prop.getProperty("redis.cluster.password" + i));
				
				jedisInfoList.add(jedisInfo);
			}
			
			jedisPool = new ShardedJedisPool(config, jedisInfoList);
			
		} catch (Exception e) {
			logger.error("connect redis fail!", e);
		}
	}
	
	public ShardedJedis getJedis() {
		return jedisPool.getResource();
	}
	
	public void closeJeids(ShardedJedis jedis) {
		jedis.close();
	}
	
	public void shutdown() {
		jedisPool.destroy();
	}
}
