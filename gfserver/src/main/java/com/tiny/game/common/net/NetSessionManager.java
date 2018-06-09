package com.tiny.game.common.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.util.GameUtil;

import game.protocol.protobuf.GameProtocol.C_RegisterClient;

public class NetSessionManager {

	private static NetSessionManager instance = new NetSessionManager();
	private NetSessionManager() {
	}

	public static NetSessionManager getInstance() {
		return instance;
	}
	
	private Map<String, Map<String, NetSession>> sessions = new ConcurrentHashMap<String, Map<String, NetSession>>();
	
	public void addSession(C_RegisterClient req, NetSession session) {
		session.setClientRegisterInfo(req);
		String clientType = req.getClientType();
		Map<String, NetSession> subSessions = sessions.get(clientType);
		if(subSessions==null) {
			subSessions = new ConcurrentHashMap<String, NetSession>();
			sessions.put(clientType, subSessions);
		}
		session.setClientType(clientType);
		subSessions.put(session.getKey(), session);
		System.out.println("add session: "+clientType+"==>" + session.getKey());
	}
	
	public void removeSession(NetSession session) {
		Map<String, NetSession> subSessions = sessions.get(session.getCientType());
		if(subSessions!=null) {
			subSessions.remove(session.getKey());
			System.out.println("remove session: " + session.getKey());
		}
	}
	
	public NetSession getSession(String clientType) {
		Map<String, NetSession> subSessions = sessions.get(clientType);
		if(subSessions!=null) {
			if(subSessions.size() > 0) {
				if(subSessions.size() == 1) {
					return subSessions.values().iterator().next();
				} else {
					List<String> keys = new ArrayList<String>(subSessions.keySet());
					return subSessions.get(keys.get(GameUtil.randomRange(0, keys.size())));
				}
			}
		}
		return null;
	}
	
}
