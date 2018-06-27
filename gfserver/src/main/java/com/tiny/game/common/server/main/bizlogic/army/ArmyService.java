package com.tiny.game.common.server.main.bizlogic.army;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.item.ItemCategory;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.role.OwnItem;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.InvalidParameterException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleUtil;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.C_JoinInBattleArmy;
import game.protocol.protobuf.GameProtocol.C_JoinInBattleHero;
import game.protocol.protobuf.GameProtocol.C_JoinOutBattleArmy;
import game.protocol.protobuf.GameProtocol.C_JoinOutBattleHero;
import game.protocol.protobuf.GameProtocol.C_SwitchJoinInBattleArmyId;
import game.protocol.protobuf.GameProtocol.OwnItemNotification;
import game.protocol.protobuf.GameProtocol.S_BatchOwnItemNotification;

public class ArmyService {
	private static final Logger logger = LoggerFactory.getLogger(ArmyService.class);
	
	public static void switchJoinInBattleArmyId(Role role, NetSession session, C_SwitchJoinInBattleArmyId req) {
		logger.info("Army: role " +role.getRoleId() + " switchJoinInBattleArmyId: " + req);
		OwnItem oi = role.getOwnItem(ItemId.usedJoinInArmyId);
		if(oi==null) {
			oi = RoleUtil.buildOwnItem(ItemId.usedJoinInArmyId, 1, req.getArmyId());
		} else {
			oi.setValue(req.getArmyId());
		}
		role.setOwnItem(oi);
		role.setLastUpdateTime(Calendar.getInstance().getTime());
		DaoFactory.getInstance().getUserDao().updateRole(role);
		// no need to notify user changed this, client will always use the new update one
	}
	
	public static void joinInBattleArmy(Role role, NetSession session, C_JoinInBattleArmy req) {
		logger.info("Army: role " +role.getRoleId() + " joinInBattleArmy: " + req);
		OwnItem usedJoinInArmyItem = role.getOwnItem(ItemId.usedJoinInArmyId);
		if(usedJoinInArmyItem==null) {
			usedJoinInArmyItem = RoleUtil.buildOwnItem(ItemId.usedJoinInArmyId, 1, req.getArmyId());
			role.setOwnItem(usedJoinInArmyItem);
		}
		
		int armyId = req.getArmyId();
		OwnItem oi = role.getOwnItem(ItemId.valueOf(req.getItemId()));
		if(oi ==null) {
			throw new InvalidParameterException("Role " + role.getRoleId() + " doesn't have item: " + req.getItemId());
		}
		if(oi.getItem().getCategory()!=ItemCategory.Army) {
			throw new InvalidParameterException("Role " + role.getRoleId() + " join in army is not army: " + req.getItemId());
		}
		
		if(role.hasItemContainsSubExtendAttributeValue(ItemId.usedJoinInArmyId, GameConst.JOININ_ARMY_SEQ_ID_PREFIX+armyId, req.getItemId()+"")) {
			role.addItemSubExtendAttributeValue(ItemId.usedJoinInArmyId, GameConst.JOININ_ARMY_SEQ_ID_PREFIX+armyId, req.getItemId()+"");
			role.setLastUpdateTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getUserDao().updateRole(role);
		}
		
		notifyChangedItems(session, NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Set, role.getOwnItem(ItemId.usedJoinInArmyId)));
	}
	
	public static void joinOutBattleArmy(Role role, NetSession session, C_JoinOutBattleArmy req) {
		logger.info("Army: role " +role.getRoleId() + " joinOutBattleArmy: " + req);
		int armyId = req.getArmyId();
		if(role.hasItemContainsSubExtendAttributeValue(ItemId.usedJoinInArmyId, GameConst.JOININ_ARMY_SEQ_ID_PREFIX+armyId, req.getItemId()+"")) {
			role.deleteItemSubExtendAttributeValue(ItemId.usedJoinInArmyId, GameConst.JOININ_ARMY_SEQ_ID_PREFIX+armyId, req.getItemId()+"");
			role.setLastUpdateTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getUserDao().updateRole(role);
		}
		notifyChangedItems(session, NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Set, role.getOwnItem(ItemId.usedJoinInArmyId)));
	}
	
	public static void joinInBattleHero(Role role, NetSession session, C_JoinInBattleHero req) {
		logger.info("Army: role " +role.getRoleId() + " joinInBattleHero: " + req);
		OwnItem usedJoinInArmyItem = role.getOwnItem(ItemId.usedJoinInArmyId);
		if(usedJoinInArmyItem==null) {
			usedJoinInArmyItem = RoleUtil.buildOwnItem(ItemId.usedJoinInArmyId, 1, req.getArmyId());
			role.setOwnItem(usedJoinInArmyItem);
		}
		
		int armyId = req.getArmyId();
		OwnItem oi = role.getOwnItem(ItemId.valueOf(req.getItemId()));
		if(oi ==null) {
			throw new InvalidParameterException("Role " + role.getRoleId() + " doesn't have item: " + req.getItemId());
		}
		if(oi.getItem().getCategory()!=ItemCategory.Hero) {
			throw new InvalidParameterException("Role " + role.getRoleId() + " join in army is not hero: " + req.getItemId());
		}
		
		if(role.hasItemContainsSubExtendAttributeValue(ItemId.usedJoinInArmyId, GameConst.JOININ_HERO_SEQ_ID_PREFIX+armyId, req.getItemId()+"")) {
			role.addItemSubExtendAttributeValue(ItemId.usedJoinInArmyId, GameConst.JOININ_HERO_SEQ_ID_PREFIX+armyId, req.getItemId()+"");
			role.setLastUpdateTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getUserDao().updateRole(role);
		}
		
		notifyChangedItems(session, NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Set, role.getOwnItem(ItemId.usedJoinInArmyId)));
	}
	
	public static void joinOutBattleHero(Role role, NetSession session, C_JoinOutBattleHero req) {
		logger.info("Army: role " +role.getRoleId() + " joinOutBattleHero: " + req);
		int armyId = req.getArmyId();
		if(role.hasItemContainsSubExtendAttributeValue(ItemId.usedJoinInArmyId, GameConst.JOININ_HERO_SEQ_ID_PREFIX+armyId, req.getItemId()+"")) {
			role.deleteItemSubExtendAttributeValue(ItemId.usedJoinInArmyId, GameConst.JOININ_HERO_SEQ_ID_PREFIX+armyId, req.getItemId()+"");
			role.setLastUpdateTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getUserDao().updateRole(role);
		}
		notifyChangedItems(session, NetMessageUtil.buildOwnItemNotification(OwnItemNotification.ItemChangeType.Set, role.getOwnItem(ItemId.usedJoinInArmyId)));
	}
	
	private static void notifyChangedItems(NetSession session, OwnItemNotification...itemNotifications) {
		S_BatchOwnItemNotification.Builder builder = S_BatchOwnItemNotification.newBuilder();
		for(OwnItemNotification on : itemNotifications) {
			builder.addNotification(on);
		}
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, builder.build());
	}
	
	
}
