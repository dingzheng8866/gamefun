package com.tiny.game.common.net.cmd;

import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.netty.NetSession;

public abstract class NetCmdProcessor {

	protected boolean enable = true;
	protected String cmd;
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public abstract void process(NetSession session, NetMessage msg);
	
}
