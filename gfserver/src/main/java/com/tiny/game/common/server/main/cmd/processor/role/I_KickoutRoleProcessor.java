package com.tiny.game.common.server.main.cmd.processor.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.NetUtils;
import com.tiny.game.common.net.cmd.NetCmdAnnimation;
import com.tiny.game.common.net.cmd.NetCmdProcessor;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.server.ServerTimer;
import com.tiny.game.common.server.main.bizlogic.role.RoleSessionService;

import game.protocol.protobuf.GameProtocol.I_KickoutRole;
import game.protocol.protobuf.GameProtocol.S_Exception;
import io.netty.util.Timeout;

@NetCmdAnnimation(cmd = I_KickoutRole.class)
public class I_KickoutRoleProcessor extends NetCmdProcessor {

	private static final Logger logger = LoggerFactory.getLogger(I_KickoutRoleProcessor.class);

	@Override
	public void process(NetSession session, NetMessage msg) {
		I_KickoutRole req = NetUtils.getNetProtocolObject(I_KickoutRole.PARSER, msg);
		
		NetSession roleSession = RoleSessionService.getRoleSession(req.getRoleId());
		if(roleSession == null) {
			logger.info("Role " + req.getRoleId() + " is not online now, ignore kickout message");
			return ;
		} else {
			// send 1 message to user and then close session
			S_Exception.Builder builder = S_Exception.newBuilder();
			builder.setCode(req.getReasonCode());
			
			NetLayerManager.getInstance().asyncSendOutboundMessage(roleSession, builder.build());
			// let client close it? so server no need to maintain timer to close it
			
			Timeout timeout = ServerTimer.getInstance().newTimeout((Timeout to) -> {
				if(session!=null && session.isChannelActive()) {
					try {
						session.close();
					}catch(Exception e) {
					}
				}
			}, 3);
			roleSession.setTimeoutTask(timeout);
		}
	}
	
}
