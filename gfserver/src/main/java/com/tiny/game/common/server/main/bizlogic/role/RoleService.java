package com.tiny.game.common.server.main.bizlogic.role;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.conf.role.RoleExpConfReader;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.RoleExp;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.util.IdGenerator;

import game.protocol.protobuf.GameProtocol.C_RoleLogin;

public class RoleService {

	private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
	
	private static int maxConfigRoleLevel = 1;
	public static void addMaxConfigRoleLevel(int level){
		if(level > maxConfigRoleLevel){
			maxConfigRoleLevel = level;
		}
	}
	public static int getRoleMaxConfigLevel(){
		return maxConfigRoleLevel;
	}
	
	public static void fixupRoleExpChange(Role role){
		int tempRoleLevel = role.getLevel();
		if(tempRoleLevel > maxConfigRoleLevel){
			tempRoleLevel = maxConfigRoleLevel;
		}
		
		RoleExp roleExp = (RoleExp)LocalConfManager.getInstance().getConfReader(RoleExpConfReader.class).getConfBean(tempRoleLevel+"");
		int levelMaxExp = roleExp.getExp();
		int currentExp = role.getRoleOwnItemValue(ItemId.roleExp);
		if(currentExp > levelMaxExp){
			role.deleteRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 0, levelMaxExp)); // exp-max==>then fixup again
			role.addRoleOwnItem(RoleUtil.buildOwnItem(ItemId.roleLevel, 0, 1)); // level+1
			//TODO: broadcast role level up, maybe give some reward
			logger.info("TODO: broadcast role level up, maybe give some reward to level: " + role.getLevel());
			fixupRoleExpChange(role);
		}
	}
	
	public static User getUser(String acctBindId, String deviceId){
		UserAcctBindInfo acctBindInfo = DaoFactory.getInstance().getUserDao().getUserAcctBindInfo(acctBindId);
		if(acctBindInfo==null && !acctBindId.equals(deviceId)) {
			acctBindInfo = DaoFactory.getInstance().getUserDao().getUserAcctBindInfo(deviceId);
		}
		User user = null;
		if(acctBindInfo!=null) {
			user = DaoFactory.getInstance().getUserDao().getUserById(acctBindInfo.getUserId());
		} 
		return user;
	}
	
	public static Role updateUserAndRole(User user, C_RoleLogin req, String userIp){
		updateUserLoginInfo(user, req, userIp);
		DaoFactory.getInstance().getUserDao().updateUser(user);
		Role role = DaoFactory.getInstance().getUserDao().getRole(user.getUserId());
		// do we need to update lastupdatetime of role, seems need
		role.setLastUpdateTime(Calendar.getInstance().getTime());
		DaoFactory.getInstance().getUserDao().updateRole(role);
		
		UserOnlineInfo userOnlineInfo = DaoFactory.getInstance().getUserDao().getUserOnlineInfo(user.getUserId());
		if(userOnlineInfo==null) {
			createUserOnlineInfo(user.getUserId());
		} else {
			if(!ServerContext.getInstance().getServerUniqueTag().equals(userOnlineInfo.getLoginServerId())) {
				kickoffUserToOffline(user.getUserId(), userOnlineInfo.getLoginServerId());
			} 
			updateUserOnlineInfo(userOnlineInfo);
		}
		return role;
	}
	
	private static void kickoffUserToOffline(String userId, String specifiedLoginServerId){
		// TODO: broadcast kickoff cmd
	}
	
	public static Role createUserAndRole(C_RoleLogin req, String userIp, String loginAcctId){
		User user = buildUser(req, userIp);
		Role role = RoleUtil.buildRole(user.getUserId());
		
		DaoFactory.getInstance().getUserDao().createUser(user);
		DaoFactory.getInstance().getUserDao().createRole(role);
		
		// create user acct bind info
		UserAcctBindInfo acctBindInfo = new UserAcctBindInfo();
		acctBindInfo.setUserId(user.getUserId());
		acctBindInfo.setLastUpdateTime(Calendar.getInstance().getTime());
		acctBindInfo.setBindedAccountId(loginAcctId);
		DaoFactory.getInstance().getUserDao().createUserAcctBindInfo(acctBindInfo);
		
		// if login acct id !=device id, need to auto bind device id as well?
		if(!loginAcctId.equals(req.getDeviceId()) && StringUtils.isNotEmpty(req.getDeviceId())){
			acctBindInfo.setBindedAccountId(req.getDeviceId());
			DaoFactory.getInstance().getUserDao().createUserAcctBindInfo(acctBindInfo);
		}
		
		createUserOnlineInfo(user.getUserId());
		return role;
	}
	
	private static void createUserOnlineInfo(String userId){
		UserOnlineInfo userOnlineInfo = new UserOnlineInfo();
		userOnlineInfo.setUserId(userId);
		userOnlineInfo.setLoginServerId(ServerContext.getInstance().getServerUniqueTag()); 
		userOnlineInfo.setLastUpdateTime(Calendar.getInstance().getTime());
		logger.info("Create on line info: "+userOnlineInfo.toString());
		DaoFactory.getInstance().getUserDao().createUserOnlineInfo(userOnlineInfo);
	}
	
	private static void updateUserOnlineInfo(UserOnlineInfo userOnlineInfo){
		userOnlineInfo.setLoginServerId(ServerContext.getInstance().getServerUniqueTag()); 
		userOnlineInfo.setLastUpdateTime(Calendar.getInstance().getTime());
		DaoFactory.getInstance().getUserDao().updateUserOnlineInfo(userOnlineInfo);
	}
	
	private static void updateUserLoginInfo(User user, C_RoleLogin req, String userIp){
		user.setLoginAccountId(req.getLoginAccountId());
		user.setLoginDeviceId(req.getDeviceId());
		user.setLoginIp(userIp);
		user.setChannel(req.getChannel()+"");
		user.setPlatform(req.getPlatform());
		user.setPlatformAccountId(req.getAccount());
		user.setPlatformAccountPassword(req.getToken());
		user.setLoginDeviceInfo(req.getDeviceInfo());
		user.setLastUpdateTime(Calendar.getInstance().getTime());
	}
	
	private static User buildUser(C_RoleLogin req, String userIp) {
		User bean = new User();
		bean.setUserId(IdGenerator.genUniqueUserId());
		bean.setLoginAccountId(req.getLoginAccountId());
		bean.setLoginDeviceId(req.getDeviceId());
		bean.setLoginIp(userIp);
		bean.setChannel(req.getChannel()+"");
		bean.setPlatform(req.getPlatform());
		bean.setPlatformAccountId(req.getAccount());
		bean.setPlatformAccountPassword(req.getToken());
		Date time = Calendar.getInstance().getTime();
		bean.setCreateTime(time);
		bean.setLastUpdateTime(time);
		bean.setLoginDeviceInfo(req.getDeviceInfo());
		return bean;
	}
	
	public static void recordSignDay(Role role){
		
	}
	
}
