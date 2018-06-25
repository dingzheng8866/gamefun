package com.tiny.game.common.server.main.cmd.processor.chat;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.main.bizlogic.chat.ChatService;
import com.tiny.game.common.server.main.cmd.processor.AbstractPlayerCmdProcessor;

import game.protocol.protobuf.GameProtocol.C_SendChatMessage;

@NetCmdAnnimation(cmd = C_SendChatMessage.class)
public class C_SendChatMessageProcessor extends AbstractPlayerCmdProcessor {

	@Override
	public void process(Role role, NetSession session, NetMessage msg) {
		C_SendChatMessage req = NetUtils.getNetProtocolObject(C_SendChatMessage.PARSER, msg);
		ChatService.sendChatMessage(role, session, req);
	}
	
}