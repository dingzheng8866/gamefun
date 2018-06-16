package com.tiny.game.common.util;

import java.util.Random;

import com.tiny.game.common.server.ServerContext;

public class IdGenerator {

	private static Random random = new Random(System.currentTimeMillis());
	
	private static String charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static String genUniqueUserId(){
		return genUniqueId(ServerContext.getInstance().getUserIdLen());
	}
	
	public static String genUserName(){
		return genUniqueId(ServerContext.getInstance().getUserNameLen()).toLowerCase();
	}
	
	public static String genServerTagUniqueId(String prefix){
//		return prefix+"_" + genUniqueId(6)+"_" + ServerContext.getInstance().getLocalAnyIp();
		return prefix+"_" + ServerContext.getInstance().getLocalAnyIp();
	}
	
	public static String genUniqueId(int len){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<len; i++){
			int index = random.nextInt(charPool.length());
			if(index >= charPool.length()){
				index = charPool.length() -1;
			}
			sb.append(charPool.charAt(index));
		}
		return sb.toString();
	}
	
}
