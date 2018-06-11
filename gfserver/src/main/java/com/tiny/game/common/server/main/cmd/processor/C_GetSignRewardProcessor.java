package com.tiny.game.common.server.main.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;

import game.protocol.protobuf.GameProtocol.C_GetSignReward;

@NetCmdAnnimation(cmd = C_GetSignReward.class)
public class C_GetSignRewardProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_GetSignRewardProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		C_GetSignReward req = NetUtils.getNetProtocolObject(C_GetSignReward.PARSER, msg);
		Role role = session.getPlayerRole();
		if(role!=null) {
			RoleService.getSignReward(role, req.getDay());
		} else {
			throw new InternalBugException("Not found role on session");
		}
	}
	
}
