package com.tiny.game.common.net;

import com.google.protobuf.GeneratedMessage;

public class NetMessage {

	private String name;
	private byte[] parameters;
	
	public NetMessage(String name, byte[] content) {
		this.name = name;
		this.parameters = content;
	}
	
	public NetMessage(GeneratedMessage pbMsg) {
		this.name = pbMsg.getClass().getSimpleName(); //.toLowerCase()
		this.parameters = pbMsg.toByteArray();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getParameters() {
		return parameters;
	}

	public void setParameters(byte[] content) {
		this.parameters = content;
	}
	
}
