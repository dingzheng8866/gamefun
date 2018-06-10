package com.tiny.game.common.util;

import java.util.Map;

import com.tiny.game.common.domain.item.LevelItem;
import com.tiny.game.common.domain.role.OwnItem;
import com.tiny.game.common.domain.role.Role;

import game.protocol.protobuf.GameProtocol.S_LoginServerInfo;
import game.protocol.protobuf.GameProtocol.S_OwnItem;
import game.protocol.protobuf.GameProtocol.S_RoleData;
import game.protocol.protobuf.GameProtocol.StringKeyParameter;

public class NetMessageUtil {

	public static S_LoginServerInfo buildS_LoginServerInfo(String serverIp, int port){
		S_LoginServerInfo.Builder builder = S_LoginServerInfo.newBuilder();
		builder.setIpAddress(serverIp);
		builder.setPort(port);
		return builder.build();
	}
	
	public static S_RoleData convertRole(Role role){
		S_RoleData.Builder builder = S_RoleData.newBuilder();
		builder.setRoleId(role.getRoleId());
		for(OwnItem item : role.getOwnItems()){
			builder.addItem(convertOwnItem(item));
		}
		return builder.build();
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
