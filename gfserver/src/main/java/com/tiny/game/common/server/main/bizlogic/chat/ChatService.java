package com.tiny.game.common.server.main.bizlogic.chat;

import com.tiny.game.common.GameConst;
import com.tiny.game.common.dao.DaoFactory;
import com.tiny.game.common.domain.alliance.Alliance;
import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.GameRuntimeException;
import com.tiny.game.common.net.netty.NetSession;

import game.protocol.protobuf.GameProtocol.C_ApproveJoinInAlliance;
import game.protocol.protobuf.GameProtocol.C_SendChatMessage;

public class ChatService {

	public static void sendChatMessage(Role role, NetSession session, C_SendChatMessage req) {
		
		int chatGroup = req.getChatGroup();
		if(chatGroup == GameConst.CHAT_GROUP_ALLIANCE) {
			
		}
		
//		Alliance alliance = DaoFactory.getInstance().getAllianceDao().getAllianceById(req.getAllianceId());
//		if(alliance == null) {
//			throw new GameRuntimeException(GameConst.Error_Alliance_Not_Exist, "Not found alliance by id: " + req.getAllianceId());
//		}
//		Role joinInRole = validateOperRoleJoinInData(role, session, req.getRoleId(), req.getAllianceId());
//		DaoFactory.getInstance().getAllianceDao().deleteAllianceEventByEventId(req.getAllianceId(), req.getEventId());
//		approveRoleJoinInAlliace(alliance, joinInRole, role.getRoleId(), role.getRoleName());
	}
	
}
