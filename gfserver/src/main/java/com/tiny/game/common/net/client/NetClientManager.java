package com.tiny.game.common.net.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.GeneratedMessage;
import com.tiny.game.common.exception.InternalBugException;
import com.tiny.game.common.net.NetLayerManager;
import com.tiny.game.common.net.cmd.NetCmd;
import com.tiny.game.common.net.netty.NetChannelInboundMessageHandler;
import com.tiny.game.common.net.netty.NetSession;
import com.tiny.game.common.net.netty.NetSessionFilter;
import com.tiny.game.common.net.netty.NetSessionHandler;
import com.tiny.game.common.net.netty.NetSessionIpFilter;
import com.tiny.game.common.net.netty.NetSessionManagerFilter;
import com.tiny.game.common.util.GameUtil;

import game.protocol.protobuf.GameProtocol.C_RegisterClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

public class NetClientManager {

	private volatile boolean keeperThreadRunFlag = false;
	
	private volatile int msgId=0;
	
	private NetClient netClient = null;
	
	private Map<String, List<ConnectTarget>> connectTargets = new ConcurrentHashMap<String, List<ConnectTarget>>();
	
	private NetKeeperThread connectionKeeperThread = null;
	
	private C_RegisterClient registerMessage = null;
	
	private static NetClientManager instance = new NetClientManager();
	private NetClientManager() {
	}

	public static NetClientManager getInstance() {
		return instance;
	}
	
	public C_RegisterClient getRegisterMessage() {
		return registerMessage;
	}
	
	public synchronized int getNextMessageId() {
		if(msgId >= Integer.MAX_VALUE) {
			msgId = 0;
		}
		return msgId++;
	}
	
	public void addConnectTarget(String targetServerTag, String host, int port) {
		List<ConnectTarget> targets = connectTargets.get(targetServerTag);
		if(targets==null) {
			targets = new ArrayList<ConnectTarget>();
			connectTargets.put(targetServerTag, targets);
		}
		targets.add(new ConnectTarget(targetServerTag, host, port));
	}
	
	// TODO: remove connect target
	
	private ConnectTarget getRandomActiveTarget(List<ConnectTarget> targets, List<ConnectTarget> excludeTargets) {
		if(targets.size() < 1) {
			return null;
		}
		ConnectTarget target = targets.get(GameUtil.randomRange(0, targets.size()));
		excludeTargets.add(target);
		targets.remove(target);
		return target;
	}
	
	public void sendMsg(String routerTag, GeneratedMessage msg) {
		sendMsg(routerTag, new NetCmd(msg));
	}
	
	public void sendMsg(String routerTag, NetCmd msg) {
		List<ConnectTarget> targets = connectTargets.get(routerTag);
		if(targets==null || targets.size() < 1) {
			throw new InternalBugException("No router targets to use: " + routerTag);
		}
		
		synchronized (this) {
			List<ConnectTarget> activeTargets = new ArrayList<ConnectTarget>();
			List<ConnectTarget> excludeTargets = new ArrayList<ConnectTarget>();
			for(ConnectTarget target : targets) {
				if(target.isConnected()) {
					activeTargets.add(target);
				} else {
					excludeTargets.add(target);
				}
			}
			
			if(activeTargets.size() < 1) {
				throw new InternalBugException("No active router target to use: " + routerTag);
			}
			
			while(activeTargets.size() > 0) {
				ConnectTarget target = getRandomActiveTarget(activeTargets, excludeTargets);
				NetSession session = target.channel.attr(NetSessionHandler.KEY_GAME_SESSION).get();
				if(session!=null) {
					NetLayerManager.getInstance().asyncSendOutboundMessage(session, msg);
					// TODO: msg failure, drop, reconnect handle
					break;
				}
			}
		}
	}
	
	public synchronized void start(C_RegisterClient registerMessage) {
		this.registerMessage = registerMessage;
		if(netClient ==null) {
			netClient = buildNetClient();
			connectionKeeperThread = new NetKeeperThread();
			connectionKeeperThread.start();
		}
	}
	
	private synchronized void connectToTargets() {
		for(List<ConnectTarget> targets : connectTargets.values()) {
			for(ConnectTarget target : targets) {
				if(!target.isConnected()) {
					Channel channel = netClient.connect(target.host, target.port);
					if(channel!=null) {
						target.channel = channel;
					}
				}
			}
		}
	}
	
	public synchronized void shutdown() {
		keeperThreadRunFlag = false;
		for(List<ConnectTarget> targets : connectTargets.values()) {
			for(ConnectTarget target : targets) {
				if(target.channel!=null) {
					target.channel.close();
				}
			}
		}
		if(netClient!=null) {
			netClient.close();
			netClient = null;
		}
		if(connectionKeeperThread!=null) {
			connectionKeeperThread.stop();
		}
	}
	
	static class ConnectTarget {
		String targetServerTag;
		String host;
		int port;
		Channel channel = null;
		
		public boolean isConnected() {
			if(channel!=null) {
				return channel.isActive() && channel.attr(NetSessionHandler.KEY_GAME_SESSION).get()!=null;
			}
			return false;
		}
		
		public ConnectTarget(String targetServerTag, String host, int port) {
			this.targetServerTag = targetServerTag;
			this.host = host;
			this.port = port;
		}
		
		public boolean equals(Object o) {
			if(o==null || !(o instanceof ConnectTarget)) {
				return false;
			}
			
			ConnectTarget t = (ConnectTarget)o;
			if(t.targetServerTag.equals(targetServerTag) && t.host.equals(host) && t.port==port) {
				return true;
			}
			
			return false;
		}
		
	}
	
	class NetKeeperThread extends Thread {
		public void run() {
			keeperThreadRunFlag = true;
			while(keeperThreadRunFlag) {
				try {
					Thread.sleep(500);
					connectToTargets();
					Thread.sleep(2500);
				}catch(Exception e) {
					e.printStackTrace(); // no need extra handling
				}
			}
		}
	}
	
	public NetClient buildNetClient() {
		
		List<NetSessionFilter> filters = new ArrayList<NetSessionFilter>();
		NetSessionAutoRegisterClientFilter filter = new NetSessionAutoRegisterClientFilter();
		filters.add(filter);
		
		NetSessionHandler sessionHandler = new NetSessionHandler();
		sessionHandler.setFilters(filters);
		
		NetChannelInboundMessageHandler channelHandler = new NetChannelInboundMessageHandler();
		channelHandler.setSessionHandler(sessionHandler);
		List<ChannelHandler> channelHandlers = new ArrayList<ChannelHandler>();
		channelHandlers.add(channelHandler);
		
		NetClient client = new NetClient(channelHandlers);
		return client;
	}
	
}
