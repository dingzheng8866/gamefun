package com.tiny.game.common.dao.impl;

import com.tiny.game.common.dao.UserDao;
import com.tiny.game.common.dao.db.druid.UserDaoImplDB;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;

public class UserDaoImpl implements UserDao {

	@Override
	public void createUser(User user) {
		UserDaoImplDB.getInstance().createUser(user);
	}

	@Override
	public void updateUser(User user) {
		UserDaoImplDB.getInstance().updateUser(user);
	}

	@Override
	public User getUserById(String userId) {
		return UserDaoImplDB.getInstance().getUserById(userId);
	}

	@Override
	public void createUserAcctBindInfo(UserAcctBindInfo info) {
		UserDaoImplDB.getInstance().createUserAcctBindInfo(info);
	}

	@Override
	public void deleteUserAcctBindInfo(UserAcctBindInfo info) {
		UserDaoImplDB.getInstance().deleteUserAcctBindInfo(info);
	}

	@Override
	public UserAcctBindInfo getUserAcctBindInfo(String acctBindId) {
		return UserDaoImplDB.getInstance().getUserAcctBindInfo(acctBindId);
	}

	@Override
	public void createUserOnlineInfo(UserOnlineInfo info) {
		UserDaoImplDB.getInstance().createUserOnlineInfo(info);
	}

	@Override
	public void updateUserOnlineInfo(UserOnlineInfo info) {
		UserDaoImplDB.getInstance().updateUserOnlineInfo(info);
	}

	@Override
	public void deleteUserOnlineInfo(String userId) {
		UserDaoImplDB.getInstance().deleteUserOnlineInfo(userId);
	}

	@Override
	public UserOnlineInfo getUserOnlineInfo(String userId) {
		return UserDaoImplDB.getInstance().getUserOnlineInfo(userId);
	}

}
