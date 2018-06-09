package com.tiny.game.common.dao.db.druid;


import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;

import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.dao.db.druid.UserDaoImplDB;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;

// Manually run this
public class UserDaoImplDBTest extends BaseDaoDBTest {

	private UserOnlineInfo buildUserOnlineInfo() {
		UserOnlineInfo bean = new UserOnlineInfo();
		bean.setUserId("1");
		bean.setLoginServerId("gs1");
		bean.setLastUpdateTime(Calendar.getInstance().getTime());
		return bean;
	}
	
	private UserAcctBindInfo buildUserAcctBindInfo() {
		UserAcctBindInfo bean = new UserAcctBindInfo();
		bean.setBindedAccountId("1");
		bean.setUserId("1");
		return bean;
	}

	private User buildUser() {
		User bean = new User();
		bean.setUserId("1");
		bean.setLoginAccountId("22222");
		bean.setLoginDeviceId("22222");
		bean.setLoginIp("192.168.1.2");
		bean.setChannel("android");
		bean.setPlatform("xiaomi");
		bean.setPlatformAccountId("testuser1");
		bean.setPlatformAccountPassword("testpass1");
		bean.setCreateTime(Calendar.getInstance().getTime());
		bean.setLastUpdateTime(Calendar.getInstance().getTime());
		bean.setLoginDeviceInfo("device: xiaomi android9.0");
		return bean;
	}
	
	@Ignore
	@Test
	public void testDaoFactory() {
		UserOnlineInfo bean = buildUserOnlineInfo();
		
		DaoFactory.getInstance().getUserDao().createUserOnlineInfo(bean);
		
		bean = DaoFactory.getInstance().getUserDao().getUserOnlineInfo("1");
		assert("1".equals(bean.getUserId()));
		
		bean.setLoginServerId("gs2");
		DaoFactory.getInstance().getUserDao().updateUserOnlineInfo(bean);
		bean = DaoFactory.getInstance().getUserDao().getUserOnlineInfo("1");
		assert("gs2".equals(bean.getLoginServerId()));
		
		DaoFactory.getInstance().getUserDao().deleteUserOnlineInfo("1");
		bean = DaoFactory.getInstance().getUserDao().getUserOnlineInfo("1");
		assert(bean==null);
	}	
	
	@Ignore
	@Test
	public void testUserOnlineInfo() {
		UserOnlineInfo bean = buildUserOnlineInfo();
		
		UserDaoImplDB.getInstance().createUserOnlineInfo(bean);
		
		bean = UserDaoImplDB.getInstance().getUserOnlineInfo("1");
		assert("1".equals(bean.getUserId()));
		
		bean.setLoginServerId("gs2");
		UserDaoImplDB.getInstance().updateUserOnlineInfo(bean);
		bean = UserDaoImplDB.getInstance().getUserOnlineInfo("1");
		assert("gs2".equals(bean.getLoginServerId()));
		
		UserDaoImplDB.getInstance().deleteUserOnlineInfo("1");
		bean = UserDaoImplDB.getInstance().getUserOnlineInfo("1");
		assert(bean==null);
	}	
	
	@Ignore
	@Test
	public void testUserAcctBindInfo() {
		UserAcctBindInfo bean = buildUserAcctBindInfo();
		
		UserDaoImplDB.getInstance().createUserAcctBindInfo(bean);
		
		bean = UserDaoImplDB.getInstance().getUserAcctBindInfo("1");
		assert("1".equals(bean.getUserId()));
		
		UserDaoImplDB.getInstance().deleteUserAcctBindInfo(bean);
		bean = UserDaoImplDB.getInstance().getUserAcctBindInfo("1");
		assert(bean==null);
		
		bean = buildUserAcctBindInfo();
		UserDaoImplDB.getInstance().deleteUserAcctBindInfo(bean);
	}
	
	
	@Ignore
	@Test
	public void testUser() {
		User bean = buildUser();
		
		UserDaoImplDB.getInstance().createUser(bean);
		
		bean = UserDaoImplDB.getInstance().getUserById("1");
		assert("22222".equals(bean.getLoginDeviceId()));
		
		bean.setLoginAccountId("3333");
		try {
			Thread.currentThread().sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		bean.setLastUpdateTime(Calendar.getInstance().getTime());
		UserDaoImplDB.getInstance().updateUser(bean);
		bean = UserDaoImplDB.getInstance().getUserById("1");
		assert("3333".equals(bean.getLoginAccountId()));
		
	}
}
