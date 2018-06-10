package com.tiny.game.common.dao;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;

public interface UserDao {

	public void createUser(User user);
	public void updateUser(User user);
	public User getUserById(String userId);
	
	public void createUserAcctBindInfo(UserAcctBindInfo info);
	public void deleteUserAcctBindInfo(UserAcctBindInfo info);
	public UserAcctBindInfo getUserAcctBindInfo(String acctBindId);
	
	public void createUserOnlineInfo(UserOnlineInfo info);
	public void updateUserOnlineInfo(UserOnlineInfo info);
	public void deleteUserOnlineInfo(String userId);
	public UserOnlineInfo getUserOnlineInfo(String userId);
	
	public void createRole(Role role);
	public void updateRole(Role role);
	public Role getRole(String roleId);
	
}
