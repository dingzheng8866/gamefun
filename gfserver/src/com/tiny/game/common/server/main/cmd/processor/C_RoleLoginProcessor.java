package com.tiny.game.common.server.main.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.domain.role.RoleBean;
import com.tiny.game.common.error.ErrorCode;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.NetUtil;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdFactory;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.gate.cmd.processor.C_GetLoginServerInfoProcessor;
import com.tiny.game.common.server.main.MainGameServer;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;
import game.protocol.protobuf.GameProtocol.C_RegisterClient;
import game.protocol.protobuf.GameProtocol.C_RoleLogin;
import game.protocol.protobuf.GameProtocol.S_LoginServerInfo;

@NetCmdAnnimation(cmd = C_RoleLogin.class)
public class C_RoleLoginProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_RoleLoginProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		C_RoleLogin req = NetUtil.getNetProtocolObject(C_RoleLogin.PARSER, msg);
		
		String acctBindId = req.getDeviceId(); // TODO: put other one
		RoleBean role = null;
		// find role by unique acct bind id
		if(role==null) {
			// create role
		} else {
			String roleOnlineServer = ""; // TODO: get this
			if(roleOnlineServer!=null && roleOnlineServer.trim().length() > 1) {
				// TODO: broadcast kickoff cmd
				
			}
		}
		
		// sync user data to client
		
		
//		NetSessionManager.getInstance().addSession(req.getClientType(), session);
//		logger.info("C_RegisterClient: type: "+req.toString());
//		NetSession gameServerSession = NetSessionManager.getInstance().getSession(GameServer.class.getSimpleName());
//		if(gameServerSession==null) {
//			logger.error("No active game server found for user: " + session.getRemoteAddress());
//			NetLayerManager.getInstance().asyncSendOutboundMessage(session, NetCmdFactory.factoryCmdS_ErrorInfo(ErrorCode.Error_NoActiveGameServer.getValue(), ""));
//			return ;
//		}
//		C_RegisterClient client = gameServerSession.getClientRegisterInfo();
//		if(client==null) {
//			throw new InternalBugException("Bug: not set C_RegisterClient on session while init!");
//		}
//		
//		S_LoginServerInfo.Builder response = S_LoginServerInfo.newBuilder();
//		response.setIpAddress(client.getParameter1());
//		response.setPort(Integer.parseInt(client.getParameter2()));
//		
//		NetLayerManager.getInstance().asyncSendOutboundMessage(session, response.build());
//		System.out.println("Send main server info to client: " + response.build());
	}
	
}
