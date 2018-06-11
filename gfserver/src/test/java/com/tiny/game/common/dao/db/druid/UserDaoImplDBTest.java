package com.tiny.game.common.dao.db.druid;


import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;

import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.dao.db.druid.UserDaoImplDB;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;
import com.tiny.game.common.server.main.bizlogic.role.RoleUtil;

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
	
	private Role buildRole(){
		Role role = new Role();
		role.setRoleId("123456");
		role.setLastUpdateTime(Calendar.getInstance().getTime());
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.roleLevel, 0, 1));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 0, 0));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.mainBase, 3, 1));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.defenseTowerLeft, 2, 1));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.defenseTowerRight, 1, 1));
		return role;
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
	
	@Test
	public void testRole() {
		Role role = buildRole();
		
		UserDaoImplDB.getInstance().createRole(role);
		
		role = UserDaoImplDB.getInstance().getRole("123456");
		assert(role!=null);
		
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.buyGem, 1, 50));
		role.setLastUpdateTime(Calendar.getInstance().getTime());
		UserDaoImplDB.getInstance().updateRole(role);
		role = UserDaoImplDB.getInstance().getRole("123456");
		assert(50==role.getOwnItemValue(ItemId.buyGem));
		
	}
	
}
