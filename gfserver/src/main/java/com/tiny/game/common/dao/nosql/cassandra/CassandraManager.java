package com.tiny.game.common.dao.nosql.cassandra;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import com.datastax.driver.core.policies.RetryPolicy;
import com.tiny.game.common.server.ServerContext;

public class CassandraManager {

	private static Logger logger = LoggerFactory.getLogger(CassandraManager.class);
	
	private Cluster cluster;
	
	private Session session;

	private static class SingletonHolder {
		private static CassandraManager instance = new CassandraManager();
	}

	public static CassandraManager getInstance() {
		return SingletonHolder.instance;
	}

	public void initDB(ServerContext prop) {
		logger.info("Init cassandra client...");
		String[] hosts = new String[]{"127.0.0.1"}; //"192.168.1.1", "192.168.1.2", "192.168.1.3"
		
//		AuthProvider authProvider = new PlainTextAuthProvider("ershixiong", "123456"); 
		QueryOptions queryOptions = new QueryOptions().setConsistencyLevel(ConsistencyLevel.ONE);  
//		  
		PoolingOptions poolingOptions= new PoolingOptions()  
		        .setMaxRequestsPerConnection(HostDistance.LOCAL, 64)//每个连接最多允许64个并发请求  
		        .setCoreConnectionsPerHost(HostDistance.LOCAL, 2)//和集群里的每个机器都至少有2个连接  
		        .setMaxConnectionsPerHost(HostDistance.LOCAL, 6);//和集群里的每个机器都最多有6个连接 
		
		int port = 9042;
		String keyspace = "gamefun";
		
		cluster = Cluster.builder()  
		        .addContactPoints(hosts)  
//		        .withAuthProvider(authProvider)  
//		        .withLoadBalancingPolicy(lbp)  
//		        .withSocketOptions(so)  
//		        .withPoolingOptions(poolingOptions)  
		        .withQueryOptions(queryOptions)  
//		        .withRetryPolicy(retryPolicy)  
		        .withPort(port)  
		        .build();  
		session = cluster.connect();
		logger.info("Create cassandra session => connected size:" + session.getState().getConnectedHosts().size());
	}
	
	public Session getSession() {
		return session;
	}
	
	public void shutdown() {
		session.close();
		cluster.close();
	}	
	
}
