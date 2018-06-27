package com.tiny.game.common.server.main.cmd.processor.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.domain.role.OwnItem;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.role.RoleService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;
import com.tiny.game.common.util.NetMessageUtil;

import game.protocol.protobuf.GameProtocol.C_GetSignReward;
import game.protocol.protobuf.GameProtocol.OwnItemNotification;
import game.protocol.protobuf.GameProtocol.S_BatchOwnItemNotification;

@NetCmdAnnimation(cmd = C_GetSignReward.class)
public class C_GetSignRewardProcessor extends AbstractPlayerCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(C_GetSignRewardProcessor.class);

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_GetSignReward req = NetUtils.getNetProtocolObject(C_GetSignReward.PARSER, msg);
		OwnItem gotItem = RoleService.getSignReward(role, req.getDay());
		if(gotItem!=null){
			S_BatchOwnItemNotification response = NetMessageUtil.buildRoleSingleNotifyOwnItem(OwnItemNotification.ItemChangeType.Add, gotItem);
			NetLayerManager.getInstance().asyncSendOutboundMessage(session, response);
		}
	}
	
}
