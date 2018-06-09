package com.tiny.game.common.net.netty;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetSessionIpFilter implements NetSessionFilter {

	public static final Logger logger = LoggerFactory.getLogger(NetSessionIpFilter.class);
	
	private List<String> disableIps = new ArrayList<String>();
	
	public List<String> getDisableIps() {
		return disableIps;
	}

	public void setDisableIps(List<String> disableIps) {
		this.disableIps = disableIps;
	}

	@Override
	public boolean onInitNetSession(NetSession session) {
		InetSocketAddress remoteAddress = (InetSocketAddress)session.getChannel().remoteAddress();
		String ip = remoteAddress.getAddress().getHostAddress();
		System.out.println("initNetSession: addr: "+remoteAddress.toString()+", ip " + ip +" ==> " + NetSession.getRemoteAddress(session.getChannel()));
		
		// TODO: ip disable
		if(disableIps.contains(ip)) {
			logger.info("Disable ip: " + ip + " -- " + NetSession.getRemoteAddress(session.getChannel()));
			return false;
		}
		return true;
	}

	@Override
	public void onCloseNetSession(NetSession session) {
	}

}
