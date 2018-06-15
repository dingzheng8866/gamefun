package com.tiny.game.common.server.gate.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.exception.GameRuntimeException;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdFactory;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.MainGameServer;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;
import game.protocol.protobuf.GameProtocol.I_RegisterClient;
import game.protocol.protobuf.GameProtocol.S_LoginServerInfo;


@NetCmdAnnimation(cmd = C_GetLoginServerInfo.class)
public class C_GetLoginServerInfoProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_GetLoginServerInfoProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		NetSession gameServerSession = NetSessionManager.getInstance().getRandomSessionByPeerType(MainGameServer.class.getSimpleName());
		if(gameServerSession==null) {
			throw new GameRuntimeException(GameConst.Error_NoActiveServer, "not found server: " + MainGameServer.class.getSimpleName());
//			NetLayerManager.getInstance().asyncSendOutboundMessage(session, NetCmdFactory.factoryCmdS_ErrorInfo(ErrorCode.Error_NoActiveGameServer.getValue(), ""));
//			return ;
		}
		I_RegisterClient client = gameServerSession.getClientRegisterInfo();
		if(client==null) {
			throw new InternalBugException("Bug: not set I_RegisterClient on session while init!");
		}
		
		S_LoginServerInfo response = buildS_LoginServerInfo(client.getServerIp(), Integer.parseInt(client.getServerPort()));
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, response);
		System.out.println("Send main server info "+response+"to client: " + session.getRemoteAddress());
	}
	
	private static S_LoginServerInfo buildS_LoginServerInfo(String serverIp, int port){
		S_LoginServerInfo.Builder builder = S_LoginServerInfo.newBuilder();
		builder.setIpAddress(serverIp);
		builder.setPort(port);
		return builder.build();
	}
	
}
