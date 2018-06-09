package com.tiny.game.common.server.main.cmd.processor;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.domain.role.UserAcctBindInfo;
import com.tiny.game.common.domain.role.UserOnlineInfo;
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
import com.tiny.game.common.server.ServerContext;
import com.tiny.game.common.server.gate.cmd.processor.C_GetLoginServerInfoProcessor;
import com.tiny.game.common.server.main.MainGameServer;
import com.tiny.game.common.util.IdGenerator;

import game.protocol.protobuf.GameProtocol.C_GetLoginServerInfo;
import game.protocol.protobuf.GameProtocol.C_RegisterClient;
import game.protocol.protobuf.GameProtocol.C_RoleLogin;
import game.protocol.protobuf.GameProtocol.S_LoginServerInfo;

@NetCmdAnnimation(cmd = C_RoleLogin.class)
public class C_RoleLoginProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_RoleLoginProcessor.class);

	private User createUser(NetSession session, C_RoleLogin req) {
		User bean = new User();
		bean.setUserId(IdGenerator.genUniqueUserId());
		bean.setLoginAccountId(req.getLoginAccountId());
		bean.setLoginDeviceId(req.getDeviceId());
		bean.setLoginIp(session.getRemoteAddress());
		bean.setChannel(req.getChannel()+"");
		bean.setPlatform(req.getPlatform());
		bean.setPlatformAccountId(req.getAccount());
		bean.setPlatformAccountPassword(req.getToken());
		Date time = Calendar.getInstance().getTime();
		bean.setCreateTime(time);
		bean.setLastUpdateTime(time);
		bean.setLoginDeviceInfo(req.getDeviceInfo());
		DaoFactory.getInstance().getUserDao().createUser(bean);
		return bean;
	}
	
	@Override
	public void process(NetSession session, NetMessage msg) {
		C_RoleLogin req = NetUtil.getNetProtocolObject(C_RoleLogin.PARSER, msg);
		
		UserAcctBindInfo acctBindInfo = DaoFactory.getInstance().getUserDao().getUserAcctBindInfo(req.getLoginAccountId());
		if(acctBindInfo==null && !req.getLoginAccountId().equals(req.getDeviceId())) {
			acctBindInfo = DaoFactory.getInstance().getUserDao().getUserAcctBindInfo(req.getDeviceId());
		}
		User user = null;
		if(acctBindInfo==null) {
			logger.info("Try to create user because not found user by: " + req.getLoginAccountId() + "/" + req.getDeviceId());
			user = createUser(session, req);
		} else {
			user = DaoFactory.getInstance().getUserDao().getUserById(acctBindInfo.getUserId());
		}
		
		// check user online info
		String currentLoginServerId = ServerContext.getInstance().getServerUniqueTag();
		UserOnlineInfo userOnlineInfo = DaoFactory.getInstance().getUserDao().getUserOnlineInfo(user.getUserId());
		if(userOnlineInfo==null) {
			userOnlineInfo = new UserOnlineInfo();
			userOnlineInfo.setUserId(user.getUserId());
			userOnlineInfo.setLoginServerId(currentLoginServerId); 
			userOnlineInfo.setLastUpdateTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getUserDao().createUserOnlineInfo(userOnlineInfo);
		} else {
			if(!currentLoginServerId.equals(userOnlineInfo.getLoginServerId())) {
				// TODO: broadcast kickoff cmd
			} 
			userOnlineInfo.setLoginServerId(currentLoginServerId); 
			userOnlineInfo.setLastUpdateTime(Calendar.getInstance().getTime());
			DaoFactory.getInstance().getUserDao().updateUserOnlineInfo(userOnlineInfo);
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
