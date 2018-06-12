package com.tiny.game.common.util;

import java.util.Map;

import com.google.protobuf.ByteString;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.item.LevelItem;
import com.tiny.game.common.domain.role.OwnItem;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.server.main.bizlogic.role.RoleUtil;

import game.protocol.protobuf.GameProtocol.I_RouteMessage;
import game.protocol.protobuf.GameProtocol.S_OwnItem;
import game.protocol.protobuf.GameProtocol.S_RoleData;
import game.protocol.protobuf.GameProtocol.StringKeyParameter;

public class NetMessageUtil {
	
	public static I_RouteMessage buildRouteMessage(NetCmd msg, String routeToTargetServerTag, String finalClientTag){
		I_RouteMessage.Builder proxy = I_RouteMessage.newBuilder();
		proxy.setMsgName(msg.getName());
		proxy.setMsgContent(ByteString.copyFrom(msg.getParameters()));
		proxy.setTargetServerTag(routeToTargetServerTag);
		proxy.setFinalTargetClientType(finalClientTag);
		return proxy.build();
	}
	
	public static Role convertS_RoleData(S_RoleData roleData){
		Role role = new Role();
		role.setRoleId(roleData.getRoleId());
		for(S_OwnItem item : roleData.getItemList()){
			role.addOwnItem(convertS_OwnItem(item));
		}
		return role;
	}
	
	
	public static S_RoleData convertRole(Role role){
		S_RoleData.Builder builder = S_RoleData.newBuilder();
		builder.setRoleId(role.getRoleId());
		for(OwnItem item : role.getOwnItems()){
			builder.addItem(convertOwnItem(item));
		}
		return builder.build();
	}
	
	private static OwnItem convertS_OwnItem(S_OwnItem item){
		OwnItem ownItem = RoleUtil.buildOwnItem(ItemId.valueOf(item.getItemId()), item.getLevel(), item.getValue());
		
		for(StringKeyParameter sp : item.getParameterList()){
			ownItem.addExtendProp(sp.getKey(), sp.getValue());
		}
		return ownItem;
	}
	
	private static S_OwnItem convertOwnItem(OwnItem item){
		S_OwnItem.Builder builder = S_OwnItem.newBuilder();
		builder.setItemId(item.getItem().getItemId().getValue());
		builder.setValue(item.getValue());
		if(item.getItem() instanceof LevelItem){
			builder.setLevel(((LevelItem)item.getItem()).getLevel());
		}
		for(Map.Entry<String, String> entry : item.getExtendedProps().entrySet()){
			builder.addParameter(buildStringKeyParameter(entry.getKey(), entry.getValue()));
		}
		return builder.build();
	}
	
	private static StringKeyParameter buildStringKeyParameter(String key, String value){
		StringKeyParameter.Builder builder = StringKeyParameter.newBuilder();
		builder.setKey(key);
		builder.setValue(value);
		return builder.build();
	}
	
}
