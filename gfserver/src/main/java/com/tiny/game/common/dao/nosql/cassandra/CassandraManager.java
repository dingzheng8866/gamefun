package com.tiny.game.common.dao.nosql.cassandra;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.HostDistance;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
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
		String[] hosts = prop.getProperty("cassandra.cluster.hosts").split(",");
//		AuthProvider authProvider = new PlainTextAuthProvider("ershixiong", "123456"); 
		QueryOptions queryOptions = new QueryOptions().setConsistencyLevel(ConsistencyLevel.valueOf(prop.getProperty("cassandra.cluster.query.consistency.level").trim()));  
		
		PoolingOptions poolingOptions= new PoolingOptions()  
		        .setMaxRequestsPerConnection(HostDistance.LOCAL, 1000)//每个连接最多允许64个并发请求  
		        .setCoreConnectionsPerHost(HostDistance.LOCAL, 2)//和集群里的每个机器都至少有2个连接  
		        .setMaxConnectionsPerHost(HostDistance.LOCAL, 6);//和集群里的每个机器都最多有6个连接 
		
		int port = prop.getPropertyInt("cassandra.cluster.host.port");
		
		cluster = Cluster.builder()  
		        .addContactPoints(hosts)  
//		        .withAuthProvider(authProvider)  
//		        .withLoadBalancingPolicy(lbp)  
//		        .withSocketOptions(so)  
		        .withPoolingOptions(poolingOptions)  
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
