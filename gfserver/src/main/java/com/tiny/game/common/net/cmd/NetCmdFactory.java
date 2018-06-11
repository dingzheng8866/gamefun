package com.tiny.game.common.net.cmd;

import game.protocol.protobuf.GameProtocol.S_CReqCmd_Ack;
import game.protocol.protobuf.GameProtocol.S_ErrorInfo;
import game.protocol.protobuf.GameProtocol.S_HintInfo;

public class NetCmdFactory {

	public static NetCmd factoryCmdAck(String ackToCmd) {
		S_CReqCmd_Ack.Builder builder = S_CReqCmd_Ack.newBuilder();
		builder.setCmd(ackToCmd);
		NetCmd cmd = new NetCmd(builder.build());
		return cmd;
	}
	
	public static NetCmd factoryCmdS_ErrorInfo(int code, String parameter) {
		S_ErrorInfo.Builder builder = S_ErrorInfo.newBuilder();
		builder.setErrorCode(code);
		if(parameter!=null && parameter.trim().length() > 0) {
			builder.setParameter(parameter.trim());
		}
		NetCmd cmd = new NetCmd(builder.build());
		return cmd;
	}
	
	public static NetCmd factoryCmdS_HintInfo(int code, String parameter) {
		S_HintInfo.Builder builder = S_HintInfo.newBuilder();
		builder.setHintCode(code);
		builder.setParameter(parameter);
		NetCmd cmd = new NetCmd(builder.build());
		return cmd;
	}
	
}
