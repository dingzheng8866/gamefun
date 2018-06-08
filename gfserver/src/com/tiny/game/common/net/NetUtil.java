package com.tiny.game.common.net;

import org.apache.commons.lang3.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

public class NetUtil {
	
	private static Logger logger = LoggerFactory.getLogger(NetUtil.class);
	
//	public static int checkCode = 0xB52713;  // 协议通信校验码
	
	public static String keyStr = "14c4697e5b8f8e0bd0c7341f7376d177";  // 消息通信加密秘钥

	public static <T extends MessageLite> T getNetProtocolObject(Parser<T> aParser, NetMessage msg) {
		try {
			return aParser.parseFrom(msg.getParameters(), 0, msg.getParameters().length);
		} catch (InvalidProtocolBufferException e) {
			logger.error("", e);
			throw new RuntimeException("Failed to convert to pb message: "+e.getMessage(), e);
		}
	}
	
	public static String toHexString(int num) {		
		return "0x" + Integer.toHexString(num);
	}
	
	public static byte[] shortToByteArray(short num) {
		byte[] tmp = new byte[2];
		
		return Conversion.shortToByteArray(num, 0, tmp, 0, 2);
	}
	
	public static byte[] intToByteArray(int num) {
		byte[] tmp = new byte[4];
		
		Conversion.intToByteArray(num, 0, tmp, 0, 4);
		
		System.out.println("num " + num);
		for(int i=0; i!=4; i++) {
			System.out.print(Integer.toHexString(tmp[i]) + " ");
		}
		System.out.println("");
		
		return tmp;
	}
	
}
