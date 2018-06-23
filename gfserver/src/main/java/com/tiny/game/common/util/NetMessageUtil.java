package com.tiny.game.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.tiny.game.common.GameConst;
import com.tiny.game.common.domain.alliance.AllianceEvent;
import com.tiny.game.common.domain.item.ItemId;
import com.tiny.game.common.domain.item.LevelItem;
import com.tiny.game.common.domain.role.OwnItem;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.GameRuntimeException;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.main.bizlogic.role.RoleUtil;

import game.protocol.protobuf.GameProtocol.AllianceEventParameters;
import game.protocol.protobuf.GameProtocol.I_RouteMessage;
import game.protocol.protobuf.GameProtocol.IntKeyParameter;
import game.protocol.protobuf.GameProtocol.OwnItemNotification;
import game.protocol.protobuf.GameProtocol.RoleOwnItem;
import game.protocol.protobuf.GameProtocol.S_AllianceEvent;
import game.protocol.protobuf.GameProtocol.S_BatchOwnItemNotification;
import game.protocol.protobuf.GameProtocol.S_Exception;
import game.protocol.protobuf.GameProtocol.S_RoleData;
import game.protocol.protobuf.GameProtocol.StringKeyParameter;

public class NetMessageUtil {
	
	public static S_Exception buildS_Exception(Exception e) {
		int code = GameConst.Error_InternalBug;
		String desc = e.getMessage();
		if(e instanceof GameRuntimeException) {
			code = ((GameRuntimeException) e).getErrorCode();
		} 
		if(desc==null || "null".equals(desc)) {
			desc="";
		}
		return buildS_Exception(code, desc, getExceptionTrace(e));
	}
	
	public static S_Exception buildS_Exception(int code, String desc, String trace) {
		S_Exception.Builder builder = S_Exception.newBuilder();
		builder.setCode(code);
		builder.setDescription(desc);
		builder.setTrace(trace);
		return builder.build();
	}
	
	private static String getExceptionTrace(Exception e) {
		PrintWriter pw = null;
		StringWriter sw = null;
		String rt = "";
		try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            rt= sw.toString().replaceAll("\r\n", "\n");
        } finally {
        	if(sw!=null) {
        		try {
					sw.close();
				} catch (IOException e1) {
				}
        	}
        	if(pw!=null) {
        		pw.close();
        	}
        }
		return rt;
    }
	
	public static OwnItemNotification buildOwnItemNotification(OwnItemNotification.ItemChangeType changeType, OwnItem ownItem){
		OwnItemNotification.Builder builder = OwnItemNotification.newBuilder();
		builder.setChangeType(changeType);
		builder.setItem(NetMessageUtil.convertOwnItem(ownItem));
		return builder.build();
	}
	
	public static S_BatchOwnItemNotification buildRoleSingleNotifyOwnItem(OwnItemNotification.ItemChangeType changeType, OwnItem ownItem){
		S_BatchOwnItemNotification.Builder builder = S_BatchOwnItemNotification.newBuilder();
		builder.addNotification(buildOwnItemNotification(changeType, ownItem));
		return builder.build();
	}
	
	public static I_RouteMessage.Builder buildRouteMessage(NetCmd msg, String routeToTargetServerTag, boolean isRandomTargetServer, String finalRouteToRoleId, String originalServerId){
		I_RouteMessage.Builder proxy = I_RouteMessage.newBuilder();
		proxy.setMsgName(msg.getName());
		proxy.setMsgContent(ByteString.copyFrom(msg.getParameters()));
		proxy.setTargetServerTag(routeToTargetServerTag);
		proxy.setIsRandomServer(isRandomTargetServer);
		proxy.setFinalRouteToRoleId(finalRouteToRoleId);
		proxy.setOriginalFromServerUniqueTag(originalServerId);
		return proxy;
	}
	
	public static Role convertS_RoleData(S_RoleData roleData){
		Role role = new Role();
		role.setRoleId(roleData.getRoleId());
		for(RoleOwnItem item : roleData.getItemList()){
			role.addOwnItem(convertRoleOwnItem(item));
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
	
	private static OwnItem convertRoleOwnItem(RoleOwnItem item){
		OwnItem ownItem = RoleUtil.buildOwnItem(ItemId.valueOf(item.getItemId()), item.getLevel(), item.getValue());
		
		for(StringKeyParameter sp : item.getParameterList()){
			ownItem.addExtendProp(sp.getKey(), sp.getValue());
		}
		return ownItem;
	}
	
	public static RoleOwnItem convertOwnItem(OwnItem item){
		RoleOwnItem.Builder builder = RoleOwnItem.newBuilder();
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
	
	public static S_AllianceEvent.Builder convertAllianceEvent(AllianceEvent ae){
		S_AllianceEvent.Builder builder = S_AllianceEvent.newBuilder();
		builder.setAllianceId(ae.getAllianceId());
		builder.setAllianceEventType(ae.getAllianceEventType());
		builder.setEventId(ae.getEventId());
		builder.setTime(ae.getTime().getTime()+"");
		AllianceEventParameters aep = convertToAllianceEventParameters(ae.getParameters());
		builder.setParameters(aep);
		return builder;
	}
	
	public static AllianceEventParameters convertToAllianceEventParameters(Map<Integer, String> parameters) {
		AllianceEventParameters.Builder builder = AllianceEventParameters.newBuilder();
		for(Map.Entry<Integer, String> entry : parameters.entrySet()) {
			builder.addParameter(buildIntKeyParameter(entry.getKey(), entry.getValue()));
		}
		return builder.build();
	}
	
	public static Map<Integer, String> convertToAllianceEventParameters(byte[] parameters) {
		AllianceEventParameters data = null;
		try {
			data = AllianceEventParameters.PARSER.parseFrom(parameters, 0, parameters.length);
		} catch (InvalidProtocolBufferException e) {
			throw new InternalBugException("Invalid bin data to convert: " + e.getMessage(), e);
		}
		data.getParameterList();
		Map<Integer, String> map = new HashMap<Integer, String>();
		for(IntKeyParameter para : data.getParameterList()) {
			map.put(para.getKey(), para.getValue());
		}
		return map;
	}
	
	private static IntKeyParameter buildIntKeyParameter(int key, String value) {
		IntKeyParameter.Builder builder = IntKeyParameter.newBuilder();
		builder.setKey(key);
		builder.setValue(value);
		return builder.build();
	}
	
	public static byte[] getByteArrayFromByteBuffer(ByteBuffer byteBuffer) {
	    byte[] bytesArray = new byte[byteBuffer.remaining()];
	    byteBuffer.get(bytesArray, 0, bytesArray.length);
	    return bytesArray;
	}
}
