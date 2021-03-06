package com.tiny.game.common.net.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.domain.role.Role;
import com.tiny.game.common.net.NetMessage;

import game.protocol.protobuf.GameProtocol.I_RegisterClient;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.Timeout;

public class NetSession {

	private static final Logger logger = LoggerFactory.getLogger(NetSession.class);
	
	private Channel channel;
	
	private Long sessionId;

	private long lastVisitTime;
	
	private String clientType="";

	private String peerUniqueId;
	
	private Role playerRole;
	
	private I_RegisterClient clientRegisterInfo = null;
	
	private Timeout timeoutTask = null;
	
	public NetSession(Channel channel) {
		this.setChannel(channel);
	}

	public boolean equals(Object o) {
		if(o==null || !(o instanceof NetSession)) {
			return false;
		}
		
		return channel.id().equals(((NetSession) o).channel.id());
	}
	
	public I_RegisterClient getClientRegisterInfo() {
		return clientRegisterInfo;
	}

	public void setClientRegisterInfo(I_RegisterClient clientRegisterInfo) {
		this.clientRegisterInfo = clientRegisterInfo;
		this.peerUniqueId = clientRegisterInfo.getClientUniqueId();
	}

	public String getPeerUniqueId() {
		return peerUniqueId;
	}
	
	public String getCientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getRemoteAddress() {
		return getRemoteAddress(channel);
	}

	public static String getRemoteAddress(Channel channel) {
		String addr = "";
		if (channel.remoteAddress() != null) {
			addr += channel.remoteAddress().toString();
		}
		addr = addr.split(":")[0].substring(1);
		
		return addr;
	}
	
	public ByteBufAllocator alloc() {
		return getChannel().alloc();
	}

	public boolean isChannelActive() {
		return channel.isActive();
	}
	
	public ChannelFuture write(NetMessage msg) {
		if (!channel.isActive()) {
			return null;
		}
		
		return channel.write(msg);
	}
	
	public ChannelFuture writeAndFlush(NetMessage msg) {
		if (!channel.isActive()) {
			return null;
		}
		
		return channel.writeAndFlush(msg);
	}

	public void flush() {
		channel.flush();
	}
	
	public ChannelFuture close() {
		return channel.close();
	}

	public Long getSessionId() {
		return sessionId;
	}

	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public long getLastVisitTime() {
		return lastVisitTime;
	}

	public void setLastVisitTime(long lastVisitTime) {
		this.lastVisitTime = lastVisitTime;
	}

	public Role getPlayerRole() {
		return playerRole;
	}

	public void setPlayerRole(Role playerRole) {
		this.playerRole = playerRole;
	}

	public Timeout getTimeoutTask() {
		return timeoutTask;
	}

	public void setTimeoutTask(Timeout timeoutTask) {
		this.timeoutTask = timeoutTask;
	}

}
