package com.tiny.game.common.dao.nosql.cassandra;


import org.junit.Test;

import com.datastax.driver.core.ColumnDefinitions.Definition;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.tiny.game.common.dao.db.druid.UserDaoImplDBTest;
import com.tiny.game.common.domain.role.UserOnlineInfo;

public class UserDaoImplCassandraTest extends CassandraManagerTestBase {

	@Test
	public void testDaoFactory() {
		UserOnlineInfo bean = UserDaoImplDBTest.buildUserOnlineInfo();
		
		System.out.println("UserDaoImplCassandraTest");
		String cql2 = "INSERT INTO gamefun.user_online (userId , loginServerId) VALUES ('user2','server2');";
		
		Session session = CassandraManager.getInstance().getSession();
//		session.execute(cql2);
		
//		session.execute(
//				QueryBuilder.insertInto("mykeyspace", "tablename")
//				            .values(new String[]{"a","b"}, new Object[]{1,2}));
		
		
		String cql = "SELECT * FROM gamefun.user_online;";
		ResultSet resultSet = session.execute(cql);
		for (Definition definition : resultSet.getColumnDefinitions())
		{
			System.out.print(definition.getName() + " ");
		}
		System.out.println("=============================");
		for (Row row : resultSet)
		{
			System.out.println(String.format("%s\t%s\t%s", row.getString("userId"), row.getString("loginServerId"),row.getTimestamp("lastUpdateTime")));
		}
	}	
	
}
