package com.tiny.game.common.server.main.bizlogic.role;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.tiny.game.common.GameConst;
import com.tiny.game.common.conf.LocalConfManager;
import com.tiny.game.common.conf.item.ItemLevelAttrConfReader;
import com.tiny.game.common.conf.role.RoleExpConfReader;
import com.tiny.game.common.conf.role.RoleSignConfReader;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.email.Email;
import com.tiny.game.common.domain.email.EmailFactory;
import com.tiny.game.common.domain.item.ItemAttr;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.role.OwnItem;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.RoleExp;
import com.tiny.game.common.domain.role.RoleSign;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.exception.InvalidParameterException;
import com.tiny.game.common.exception.GameRuntimeException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.cmd.NetCmdFactory;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.broadcast.RouterService;
import com.tiny.game.common.util.GameUtil;
import com.tiny.game.common.util.IdGenerator;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.I_RouteMessage;
import game.protocol.protobuf.GameProtocol.OwnItemNotification;
import game.protocol.protobuf.GameProtocol.S_BatchEmail;
import game.protocol.protobuf.GameProtocol.S_BatchOwnItemNotification;
import game.protocol.protobuf.GameProtocol.C_AskAsFriend;
import game.protocol.protobuf.GameProtocol.C_ChangeSetting;
import game.protocol.protobuf.GameProtocol.C_GetPlayerBaseCityInfo;
import game.protocol.protobuf.GameProtocol.C_GetPlayerMoreInfo;
import game.protocol.protobuf.GameProtocol.C_MakeAsFriend;
import game.protocol.protobuf.GameProtocol.C_RoleLogin;
import game.protocol.protobuf.GameProtocol.I_KickoutRole;
import game.protocol.protobuf.GameProtocol.S_ErrorInfo;
import game.protocol.protobuf.GameProtocol.S_RoleData;

public class RoleService {

	private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
	
	private static int maxConfigRoleSignDay = 1;
	private static int maxConfigRoleLevel = 1;
	
	public static void addMaxConfigRoleLevel(int level){
		if(level > maxConfigRoleLevel){
			maxConfigRoleLevel = level;
		}
	}
	public static int getRoleMaxConfigLevel(){
		return maxConfigRoleLevel;
	}
	
	public static void addMaxConfigRoleSignDay(int day){
		if(day > maxConfigRoleSignDay){
			maxConfigRoleSignDay = day;
		}
	}
	
	public static void notifyChangedItems(NetSession session, OwnItemNotification...itemNotifications) {
		S_BatchOwnItemNotification.Builder builder = S_BatchOwnItemNotification.newBuilder();
		for(OwnItemNotification on : itemNotifications) {
			builder.addNotification(on);
		}
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, builder.build());
	}
	
	public static void fixupRoleExpChange(Role role){
		int tempRoleLevel = role.getLevel();
		if(tempRoleLevel > maxConfigRoleLevel){
			tempRoleLevel = maxConfigRoleLevel;
		}
		
		RoleExp roleExp = (RoleExp)LocalConfManager.getInstance().getConfReader(RoleExpConfReader.class).getConfBean(tempRoleLevel+"");
		int levelMaxExp = roleExp.getExp();
		int currentExp = role.getOwnItemValue(ItemId.roleExp);
		if(currentExp > levelMaxExp){
			role.subOwnItem(RoleUtil.buildOwnItem(ItemId.roleExp, 1, levelMaxExp)); // exp-max==>then fixup again
			role.addOwnItem(RoleUtil.buildOwnItem(ItemId.roleLevel, 1, 1)); // level+1
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
		checkUpgradingItems(role);
		markRoleSignTag(role);
		
		UserOnlineInfo userOnlineInfo = DaoFactory.getInstance().getUserDao().getUserOnlineInfo(user.getUserId());
		if(userOnlineInfo==null) {
			createUserOnlineInfo(user.getUserId());
		} else {
			if(!ServerContext.getInstance().getServerUniqueTag().equals(userOnlineInfo.getLoginServerId())) {
				kickoffUserToOffline(user.getUserId(), userOnlineInfo.getLoginServerId());
			} 
			updateUserOnlineInfo(userOnlineInfo);
		}
		
		DaoFactory.getInstance().getUserDao().updateRole(role);
		return role;
	}
	
	private static void kickoffUserToOffline(String userId, String specifiedLoginServerId){
		I_KickoutRole.Builder builder = I_KickoutRole.newBuilder();
		builder.setRoleId(userId);
		builder.setReasonCode(GameConst.Error_AnotherDeviceLogin);
		I_RouteMessage.Builder req = NetMessageUtil.buildRouteMessage(new NetCmd(builder.build()), specifiedLoginServerId, false, userId, ServerContext.getInstance().getServerUniqueTag());
		RouterService.routeToTarget(req.build());
	}
	
	public static Role createUserAndRole(C_RoleLogin req, String userIp, String loginAcctId){
		User user = buildUser(req, userIp);
		Role role = RoleUtil.buildRole(user.getUserId());
		initRoleSignTag(role);
		
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
	
	public static void deleteUserOnlineInfo(String userId) {
		DaoFactory.getInstance().getUserDao().deleteUserOnlineInfo(userId);
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
	
	public static void initRoleSignTag(Role role){
//		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.signFinishTag, 0, 0));
		role.addOwnItem(RoleUtil.buildOwnItem(ItemId.signGotRewardTag, 1, 0));
		
//		OwnItem ownItem = role.getOwnItem(ItemId.signTag);
//		ownItem.addExtendProp("firstSignDay", System.currentTimeMillis()+"");
		markRoleSignTag(role);
	}
	
	private static void markRoleSignTag(Role role){
		int totalLoginDays = getTotalSignLoginDays(role);
		if(totalLoginDays < maxConfigRoleSignDay){
			// check login day changed or not
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String loginDay = df.format(Calendar.getInstance().getTime());
			if(!role.hasItemContainsSubExtendAttributeValue(ItemId.internalTagRoleLoginDay, ItemId.internalTagRoleLoginDay.name(), loginDay)){
				role.addOwnItem(RoleUtil.buildOwnItem(ItemId.signTag, 1, 1));
				role.addItemSubExtendAttributeValue(ItemId.internalTagRoleLoginDay, ItemId.internalTagRoleLoginDay.name(), loginDay);
			}
		}
	}
	
	private static int getTotalSignLoginDays(Role role){
		int totalLoginDays = 0;
		OwnItem signTag = role.getOwnItem(ItemId.signTag);
		if(signTag!=null){
			totalLoginDays = signTag.getValue();
		}
		return totalLoginDays;
	}
	
	public static OwnItem getSignReward(Role role, int day){
		OwnItem gotItem = null;
		int totalDay = getTotalSignLoginDays(role);
		if(day > totalDay){
			throw new RuntimeException();
		}
		
		if(role.hasItemContainsSubExtendAttributeValue(ItemId.signGotRewardTag, ItemId.signGotRewardTag.name(), day+"")){
			logger.error("Role: " + role.getRoleId() + ", has already got sign reward to day: " + day);
		} else {
			RoleSign reward = (RoleSign)LocalConfManager.getInstance().getConfReader(RoleSignConfReader.class).getConfBean(day+"");
			if(reward!=null){
				gotItem = RoleUtil.buildOwnItem(reward.getItemId(), 1, reward.getItemCount());
				role.addOwnItem(gotItem);
				role.addItemSubExtendAttributeValue(ItemId.signGotRewardTag, ItemId.signGotRewardTag.name(), day+"");
				DaoFactory.getInstance().getUserDao().updateRole(role);
				
				logger.info("Role: " + role.getRoleId() + ", get sign reward: " + reward);
			} else {
				throw new InternalBugException("Not found sign reward to day: " + day);
			}
		}
		return gotItem;
	}
	
	private static boolean checkUpgradingItems(Role role){
		List<OwnItem> upgradingItems = role.getAllOwnItemsContainsAttrKey(ItemAttr.beginUpgradeTime);
		boolean changed = false;
		for(OwnItem ownItem : upgradingItems){
			changed = upgradeItem(role, null, ownItem.getItem().getItemId().getValue(), ownItem.getLevel(), false);
		}
		return changed;
	}
	
	// client will send this action when it almost finished
	// user login will check upgrade and update the related values
	public static boolean upgradeItem(Role role, NetSession session, int itemId, int currentLevel, boolean persistent) {
		boolean changed = false;
		OwnItem ownItem = role.getOwnItem(ItemId.valueOf(itemId), currentLevel);
		if(ownItem==null) {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not found item:" + itemId+"-"+currentLevel);
		}
		int maxValue = ItemLevelAttrConfReader.getMaxLevel(ItemId.valueOf(itemId));
		if(currentLevel >= maxValue) {
			throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Item "+itemId+" current level:" + currentLevel+" exceed max level"+maxValue);
		}
		
		float beginUpgradeTime = ownItem.getAttrFloatValue(ItemAttr.beginUpgradeTime);
		if(beginUpgradeTime >0) {
			float leftUpgradeTime = ownItem.getAttrFloatValue(ItemAttr.leftUpgradeTime);
			
			if((System.currentTimeMillis() - beginUpgradeTime)/1000 >=leftUpgradeTime-1) {
				// finish
				// effect now
				OwnItem newLevelItem = RoleUtil.buildOwnItem(ItemId.valueOf(itemId), currentLevel+1, ownItem.getValue());
				//newLevelItem.getExtendedProps().putAll(ownItem.getExtendedProps()); // ?
				role.removeOwnItem(ownItem.getKey());
				role.addOwnItem(newLevelItem);
				changed = true;
				if(persistent){
					DaoFactory.getInstance().getUserDao().updateRole(role);
				}
				
				if(session!=null){
					S_BatchOwnItemNotification.Builder builder = S_BatchOwnItemNotification.newBuilder();
					builder.addNotification(NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Add, newLevelItem));
					builder.addNotification(NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Del, ownItem));
					NetLayerManager.getInstance().asyncSendOutboundMessage(session, builder.build());
				}
			}
		} else {
			// check unlock condition
			String unlockCondition = ownItem.getItem().getAttr(ItemAttr.unlockCondition);
			if(StringUtils.isNotEmpty(unlockCondition)) {
				for(String cond: GameUtil.splitToStringList(unlockCondition, ",")) {
					List<String> strList = GameUtil.splitToStringList(cond, "=");
					if(strList.size()!=2) {
						throw new InternalBugException("Invalid unlock condition: " + cond +" to item: " + ownItem.getKey());
					}
					ItemId checkItemId = ItemId.valueOf(Integer.parseInt(strList.get(0)));
					int checkLevel = Integer.parseInt(strList.get(1));
					if(!role.hasOwnItemMeetMinLevelCondition(checkItemId, checkLevel)) {
						throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "not meet unlock condition: " + cond +" to item: " + ownItem.getKey());
					}
				}
			}
			
			float upgradeNeedTime = ownItem.getAttrFloatValue(ItemAttr.upgradeNeedTime);
			if(upgradeNeedTime <=0) {
				// effect now
				OwnItem newLevelItem = RoleUtil.buildOwnItem(ItemId.valueOf(itemId), currentLevel+1, ownItem.getValue());
				//newLevelItem.getExtendedProps().putAll(ownItem.getExtendedProps()); // ?
				role.removeOwnItem(ownItem.getKey());
				role.addOwnItem(newLevelItem);
				changed = true;
				if(persistent){
					DaoFactory.getInstance().getUserDao().updateRole(role);
				}
				
				if(session!=null){
					S_BatchOwnItemNotification.Builder builder = S_BatchOwnItemNotification.newBuilder();
					builder.addNotification(NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Add, newLevelItem));
					builder.addNotification(NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Del, ownItem));
					NetLayerManager.getInstance().asyncSendOutboundMessage(session, builder.build());
				}
			} else {
				// notify user upgrade need time
				long beginTime = System.currentTimeMillis();
				// add to timer task
				
				ownItem.setExtendAttrValue(ItemAttr.beginUpgradeTime, beginTime+"");
				ownItem.setExtendAttrValue(ItemAttr.leftUpgradeTime, upgradeNeedTime+"");
				DaoFactory.getInstance().getUserDao().updateRole(role);
				
				if(session!=null){
					S_BatchOwnItemNotification response = NetMessageUtil.buildRoleSingleNotifyOwnItem(OwnItemNotification.ItemChangeType.Set, ownItem);
					NetLayerManager.getInstance().asyncSendOutboundMessage(session, response);
				}
			}
		}
		return changed;
	}
	
	public static void changeSetting(Role role, NetSession session, C_ChangeSetting req) {
		logger.info("Role: changeSetting " +role.getRoleId() + req);
		role.setOwnItemValue(ItemId.valueOf(req.getItemId()), req.getValue());
		role.setLastUpdateTime(Calendar.getInstance().getTime());
		DaoFactory.getInstance().getUserDao().updateRole(role);
		notifyChangedItems(session, NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Set, role.getOwnItem(ItemId.valueOf(req.getItemId()))));
	}
	
	public static void getPlayerMoreInfo(Role role, NetSession session, C_GetPlayerMoreInfo req) {
		logger.info("Role: getPlayerMoreInfo " +role.getRoleId() + req);
		Role checkRole = DaoFactory.getInstance().getUserDao().getRole(req.getPlayerId());
		
		S_RoleData roleData = NetMessageUtil.convertRoleForOtherToShow(checkRole);
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, roleData);
	}
	
	public static void getPlayerBaseCityInfo(Role role, NetSession session, C_GetPlayerBaseCityInfo req) {
		logger.info("Role: getPlayerBaseCityInfo " +role.getRoleId() + req);
		Role checkRole = DaoFactory.getInstance().getUserDao().getRole(req.getPlayerId());
		
		S_RoleData roleData = NetMessageUtil.convertRoleForBaseCityToShow(checkRole);
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, roleData);
	}
	
	public static void askAsFriend(Role role, NetSession session, C_AskAsFriend req) {
		logger.info("Role: askAsFriend " +role.getRoleId() + req);
		Role targetRole = DaoFactory.getInstance().getUserDao().getRole(req.getTargetFriendRoleId());
		if(targetRole == null) {
			throw new InvalidParameterException("Not exist role: " + req.getTargetFriendRoleId() +" to be friend");
		}
		
//		OwnItem targetRole.getOwnItem(ItemId.myFriends);
		
		Email email = EmailFactory.buildApplyFriendEmail(targetRole.getRoleId(), role.getRoleId());
		DaoFactory.getInstance().getEmailDao().createEmail(email);
		RouterService.routeToRole(targetRole.getRoleId(), NetMessageUtil.convert(email));
	}
	
	public static void makeAsFriend(Role role, NetSession session, C_MakeAsFriend req) {
		logger.info("Role: makeAsFriend " +role.getRoleId() + req);
		Role targetRole = DaoFactory.getInstance().getUserDao().getRole(req.getTargetFriendRoleId());
		if(targetRole == null) {
			throw new InvalidParameterException("Not exist role: " + req.getTargetFriendRoleId() +" to make as friend");
		}
		
		Email email = EmailFactory.buildApplyFriendEmail(targetRole.getRoleId(), role.getRoleId());
		DaoFactory.getInstance().getEmailDao().createEmail(email);
		RouterService.routeToRole(targetRole.getRoleId(), NetMessageUtil.convert(email));
	}
	
}
