package com.tiny.game.common.net.cmd;

import com.google.protobuf.GeneratedMessage;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.netty.NetSession;

public class NetCmd extends NetMessage {

	public NetCmd(String name, byte[] parameters) {
		super(name, parameters);
	}
	
	public NetCmd(GeneratedMessage pbMsg) {
		super(pbMsg);
	}
	
	public void syncExecuteOnRouter(NetSession outSession, boolean flush) {
		NetLayerManager.getInstance().syncSendOutboundMessage(outSession, this, flush);
	}
	
	public void asyncExecuteOnRouter(NetSession outSession) {
		NetLayerManager.getInstance().asyncSendOutboundMessage(outSession, this);
	}
	
}
