package com.tiny.game.common.net.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetSessionManager;

public class NetSessionManagerFilter implements NetSessionFilter {

	public static final Logger logger = LoggerFactory.getLogger(NetSessionIpFilter.class);
	
	@Override
	public boolean onInitNetSession(NetSession session) {
		return true;
	}

	@Override
	public void onCloseNetSession(NetSession session) {
		NetSessionManager.getInstance().removeSession(session);
	}

}
