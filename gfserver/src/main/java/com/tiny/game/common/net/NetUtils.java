package com.tiny.game.common.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.Conversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import sun.net.util.IPAddressUtil;

public class NetUtils {
	
	private static Logger logger = LoggerFactory.getLogger(NetUtils.class);
	
//	public static int checkCode = 0xB52713;  // 鍗忚閫氫俊鏍￠獙鐮�
	
	public static String keyStr = "14c4697e5b8f8e0bd0c7341f7376d177";  // 娑堟伅閫氫俊鍔犲瘑绉橀挜

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
	
	public static boolean isInternalIp(String ip) {
	    byte[] addr = IPAddressUtil.textToNumericFormatV4(ip);
	    return isInternalIp(addr);
	}

	private static boolean isInternalIp(byte[] addr) {
	    final byte b0 = addr[0];
	    final byte b1 = addr[1];
	    //10.x.x.x/8
	    final byte SECTION_1 = 0x0A;
	    //172.16.x.x/12
	    final byte SECTION_2 = (byte) 0xAC;
	    final byte SECTION_3 = (byte) 0x10;
	    final byte SECTION_4 = (byte) 0x1F;
	    //192.168.x.x/16
	    final byte SECTION_5 = (byte) 0xC0;
	    final byte SECTION_6 = (byte) 0xA8;
	    switch (b0) {
	        case SECTION_1:
	            return true;
	        case SECTION_2:
	            if (b1 >= SECTION_3 && b1 <= SECTION_4) {
	                return true;
	            }
	        case SECTION_5:
	            switch (b1) {
	                case SECTION_6:
	                    return true;
	            }
	        default:
	            return false;

	    }
	}
	
	public static String getLocalHostName() {  
	    String hostName;  
	    try {  
	        InetAddress addr = InetAddress.getLocalHost();  
	        hostName = addr.getHostName();  
	    } catch (Exception e) {  
	    	logger.error("Failed to get local hostname: "+e.getMessage(), e);
	        hostName = "Unknown";  
	    }  
	    return hostName;  
	}  
	
	public static String getLocalExternalNetworkAddress(List<String> addressList) { 
		for(String ip : addressList){
			if(!isInternalIp(ip)){
				return ip;
			}
		}
		return null;
	}
	
	public static String getLocalInternalNetworkAddress(List<String> addressList) { 
		for(String ip : addressList){
			if(isInternalIp(ip)){
				return ip;
			}
		}
		return null;
	}
	
	public static List<String> getNetworkIpAddress() {  
	    List<String> result = new ArrayList<String>();  
	    Enumeration<NetworkInterface> netInterfaces;  
	    try {  
	        netInterfaces = NetworkInterface.getNetworkInterfaces();  
	        InetAddress ip;  
	        while (netInterfaces.hasMoreElements()) {  
	            NetworkInterface ni = netInterfaces.nextElement();  
	            Enumeration<InetAddress> addresses=ni.getInetAddresses();  
	            while(addresses.hasMoreElements()){  
	                ip = addresses.nextElement();  
	                if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {  
	                    result.add(ip.getHostAddress());  
	                    logger.info("Local ip: " + ip.getHostAddress());
	                }  
	            }  
	        }  
	        return result;  
	    } catch (Exception e) {  
	    	logger.error("Failed to get local network address: "+e.getMessage(), e);
	        return result;  
	    }  
	}  
	
}
