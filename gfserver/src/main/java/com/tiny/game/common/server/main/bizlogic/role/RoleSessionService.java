package com.tiny.game.common.server.main.bizlogic.role;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.domain.role.User;
import com.tiny.game.common.net.NetSessionManager;
import com.tiny.game.common.net.netty.NetSession;

import game.protocol.protobuf.GameProtocol.C_RegisterClient;

public class RoleSessionService {

	public static void saveRoleSession(Role role, NetSession session) {
		C_RegisterClient.Builder req = C_RegisterClient.newBuilder();
		req.setClientType(role.getRoleId());
		req.setTag(User.class.getSimpleName());
		
		session.setPlayerRole(role);
		
		NetSessionManager.getInstance().addSession(req.build(), session);
	}
	
	public static Role getRole(String roleId) {
		NetSession session = getRoleSession(roleId);
		if(session!=null) {
			return session.getPlayerRole();
		}
		return null;
	}
	
	public static NetSession getRoleSession(String roleId) {
		NetSession session = NetSessionManager.getInstance().getSession(roleId);
		return session;
	}
	
}
