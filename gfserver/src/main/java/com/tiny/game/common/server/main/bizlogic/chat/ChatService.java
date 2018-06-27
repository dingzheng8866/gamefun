package com.tiny.game.common.server.main.bizlogic.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.GameRuntimeException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.broadcast.RouterService;
import com.tiny.game.common.server.main.bizlogic.alliance.AllianceService;

import game.protocol.protobuf.GameProtocol.C_SendChatMessage;
import game.protocol.protobuf.GameProtocol.S_ChatMessage;

public class ChatService {

	private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
	
	public static void sendChatMessage(Role role, NetSession session, C_SendChatMessage req) {
		int chatGroup = req.getChatGroup();
		String content = req.getContent();
		// TODO: filter content
		logger.info("Chat: " + role.getRoleId() + " ==> " + req.toString());
		if(chatGroup == GameConst.CHAT_GROUP_ALLIANCE) {
			AllianceService.sendAllianceChatMessage(role, session, content);
		} else if(chatGroup == GameConst.CHAT_GROUP_GENERAL) {
			S_ChatMessage msg = buildS_ChatMessage(role, chatGroup, content);
			RouterService.routeToAllOnlineRoles(msg);
		} else if(chatGroup == GameConst.CHAT_GROUP_PRIVATE) {
			String chatToRoleId = req.getTargetRoleId();
			Role chatToRole = DaoFactory.getInstance().getUserDao().getRole(chatToRoleId);
			if(chatToRole == null){
				throw new GameRuntimeException(GameConst.Error_InvalidRequestParameter, "Not found chat to role: " + chatToRoleId);
			}
			S_ChatMessage msg = buildS_ChatMessage(role, chatGroup, content);
			RouterService.routeToRole(chatToRoleId, msg);
			NetLayerManager.getInstance().asyncSendOutboundMessage(session, msg);
		} else {
			throw new GameRuntimeException(GameConst.Error_Alliance_Not_Exist, "Invalid chat group: " + chatGroup);
		}
	}
	
	private static S_ChatMessage buildS_ChatMessage(Role role, int chatGroup, String content) {
		S_ChatMessage.Builder builder = S_ChatMessage.newBuilder();
		builder.setChatGroup(chatGroup);
		builder.setContent(content);
		builder.setRoleId(role.getRoleId());
		builder.setRoleName(role.getRoleName());
		builder.setHeadIcon(""); // TODO:
		
		return builder.build();
	}
	
}
