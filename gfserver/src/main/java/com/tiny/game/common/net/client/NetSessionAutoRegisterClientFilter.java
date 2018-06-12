package com.tiny.game.common.net.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.net.netty.NetSessionFilter;

import game.protocol.protobuf.GameProtocol.I_RegisterClient;

public class NetSessionAutoRegisterClientFilter implements NetSessionFilter {

	public static final Logger logger = LoggerFactory.getLogger(NetSessionAutoRegisterClientFilter.class);
	
	@Override
	public boolean onInitNetSession(NetSession session) {
		I_RegisterClient registerMessage = NetClientManager.getInstance().getRegisterMessage();
		if(registerMessage!=null) {
			logger.info("About to auto register client message to : "+session.getRemoteAddress()+", message: " + registerMessage);
			session.writeAndFlush(new NetMessage(registerMessage));
		}
		return true;
	}

	@Override
	public void onCloseNetSession(NetSession session) {
	}

}
