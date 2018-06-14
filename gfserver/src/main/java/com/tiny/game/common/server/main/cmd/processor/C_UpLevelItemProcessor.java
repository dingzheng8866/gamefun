package com.tiny.game.common.server.main.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;

import game.protocol.protobuf.GameProtocol.C_UpLevelItem;

@NetCmdAnnimation(cmd = C_UpLevelItem.class)
public class C_UpLevelItemProcessor extends AbstractPlayerCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_UpLevelItemProcessor.class);

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_UpLevelItem req = NetUtils.getNetProtocolObject(C_UpLevelItem.PARSER, msg);
		RoleService.upgradeItem(role, session, req.getItemId(), req.getCurrentLevel());
	}
	
}
