package com.tiny.game.common.net.netty;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.NetMessage;
import com.tiny.game.common.net.cmd.NetCmdProcessor;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public class NetSessionHandler {

	public static final Logger logger = LoggerFactory.getLogger(NetSessionHandler.class);

	public static final AttributeKey<NetSession> KEY_GAME_SESSION = AttributeKey
			.valueOf("NetServer" + ".KEY_GAME_SESSION");
	
	private List<NetSessionFilter> filters = null;
	
	public List<NetSessionFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<NetSessionFilter> filters) {
		this.filters = filters;
	}

	public void initNetSession(Channel channel) {
		//String ip = channel.remoteAddress().toString().split(":")[0].replace("/", "");
//		if (!gameServer.checkLanIp(ip)) {
//			logger.error("Lan channel address {} is not in white ip list", ip);
//			channel.close();
//			return ;
//		}
		
		// TODO: ip limit
		NetSession session = new NetSession(channel);
		if(filters!=null) {
			for(NetSessionFilter filter : filters) {
				if(!filter.onInitNetSession(session)) {
					logger.info("Not pass session filter: ", filter.getClass().getSimpleName());
					channel.close();
					return ;
				}
			}
		}
		
		channel.attr(KEY_GAME_SESSION).set(session);
	}
	
	public void closeNetSession(Channel channel) {
		NetSession session = channel.attr(KEY_GAME_SESSION).get();
		if (session == null) {
			return;
		}
		channel.attr(KEY_GAME_SESSION).remove();
		
		if(filters!=null) {
			for(NetSessionFilter filter : filters) {
				filter.onCloseNetSession(session);
			}
		}
		
		if(session.getTimeoutTask()!=null) {
			session.getTimeoutTask().cancel();
		}
	}
	
	public void processInboundMessage(Channel channel, NetMessage msg) {
		NetSession session = channel.attr(KEY_GAME_SESSION).get();
		if (session == null) {
			logger.error("Session is null: " + channel.remoteAddress().toString() + ", msg: " +msg.getName());
			return;
		}
		session.setLastVisitTime(System.currentTimeMillis());
		NetLayerManager.getInstance().processNetMessage(session, msg);
	}
}
