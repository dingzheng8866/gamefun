package com.tiny.game.common.server.main.cmd.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;

public abstract class AbstractPlayerCmdProcessor extends NetCmdProcessor {

//	private static final Logger logger = LoggerFactory.getLogger(AbstractPlayerCmdProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		Role role = session.getPlayerRole();
		if(role==null) {
			throw new InternalBugException("Not found role on session");
		} else {
			process(role, session, msg);
		}
	}
	
	protected abstract void process(Role role, NetSession session, NetMessage msg);
	
}
