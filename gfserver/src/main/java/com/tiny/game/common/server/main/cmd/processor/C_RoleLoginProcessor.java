package com.tiny.game.common.server.main.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.error.ErrorCode;
import com.tiny.game.common.exception.InvalidRequestParameter;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.server.main.bizlogic.role.RoleSessionService;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.C_RoleLogin;
import game.protocol.protobuf.GameProtocol.S_RoleData;

@NetCmdAnnimation(cmd = C_RoleLogin.class)
public class C_RoleLoginProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_RoleLoginProcessor.class);

	private void validateReq(C_RoleLogin req){
		if(StringUtils.isEmpty(req.getLoginAccountId()) && StringUtils.isEmpty(req.getDeviceId())){
			throw new InvalidRequestParameter(ErrorCode.Error_InvalidRequestParameter, "empty login acct id and device id");
		}
	}
	
	@Override
	public void process(NetSession session, NetMessage msg) {
		C_RoleLogin req = NetUtils.getNetProtocolObject(C_RoleLogin.PARSER, msg);
		validateReq(req);
		String loginAcctId = req.getLoginAccountId();
		if(StringUtils.isEmpty(loginAcctId)){
			loginAcctId = req.getDeviceId();
		}
		Role role = null;
		User user = RoleService.getUser(loginAcctId, req.getDeviceId());
		if(user==null){
			logger.info("Try to create user for not found user by: " + loginAcctId + "," + req.getDeviceId());
			role = RoleService.createUserAndRole(req, session.getRemoteAddress(), loginAcctId);
		} else {
			role = RoleService.updateUserAndRole(user, req, session.getRemoteAddress());
		}
		
		// save user session
		RoleSessionService.saveRoleSession(role, session);
		
		// sync user data to client
		S_RoleData roleData = NetMessageUtil.convertRole(role);
		NetLayerManager.getInstance().asyncSendOutboundMessage(session, roleData);
		System.out.println("Send role data: " + roleData);
	}
	
}
