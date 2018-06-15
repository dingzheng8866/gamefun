package com.tiny.game.common.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.util.GameUtil;

import game.protocol.protobuf.GameProtocol.I_RegisterClient;

public class NetSessionManager {

	private static NetSessionManager instance = new NetSessionManager();
	private NetSessionManager() {
	}

	public static NetSessionManager getInstance() {
		return instance;
	}
	
	private Map<String, List<NetSession>> sessions = new ConcurrentHashMap<String, List<NetSession>>();
	private Map<String, NetSession> sessionMap = new ConcurrentHashMap<String, NetSession>();
	
	
	public void addSession(I_RegisterClient req, NetSession session) {
		session.setClientRegisterInfo(req);
		String clientType = req.getClientType();
		session.setClientType(clientType);
		
		List<NetSession> subSessions = sessions.get(clientType);
		if(subSessions==null) {
			subSessions = Collections.synchronizedList(new ArrayList<NetSession>());
			sessions.put(clientType, subSessions);
		}
		subSessions.add(session);
		
		sessionMap.put(session.getPeerUniqueId(), session);
		System.out.println("add session: "+clientType+"==>" + session.getPeerUniqueId());
	}
	
	public void removeSession(NetSession session) {
		List<NetSession> subSessions = sessions.get(session.getCientType());
		if(subSessions!=null) {
			subSessions.remove(session);
			System.out.println("remove session: " + session.getPeerUniqueId());
		}
		if(session.getPeerUniqueId()!=null) {
			sessionMap.remove(session.getPeerUniqueId());
		}
	}
	
	public NetSession getRandomSessionByPeerType(String clientType) {
		List<NetSession> subSessions = sessions.get(clientType);
		if(subSessions!=null) {
			if(subSessions.size() > 0) {
				if(subSessions.size() == 1) {
					return subSessions.get(0);
				} else {
					return subSessions.get(GameUtil.randomRange(0, subSessions.size()-1));
				}
			}
		}
		return null;
	}
	
	public NetSession getSessionByPeerUniqueId(String clientUniqueId) {
		return sessionMap.get(clientUniqueId);
	}
	
}
