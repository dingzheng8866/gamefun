package com.tiny.game.common.dao.nosql.cassandra;

import org.junit.Test;

import com.tiny.game.common.dao.db.druid.UserDaoImplDBTest;
import com.tiny.game.common.domain.role.UserOnlineInfo;

public class UserDaoImplCassandraTest extends CassandraManagerTestBase {

	@Test
	public void testDaoFactory() {
		UserOnlineInfo bean = UserDaoImplDBTest.buildUserOnlineInfo();
		
	}	
	
}
