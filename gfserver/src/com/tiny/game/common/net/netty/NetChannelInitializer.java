package com.tiny.game.common.net.netty;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NetChannelInitializer extends ChannelInitializer<SocketChannel> {

	private List<ChannelHandler> handlers = new ArrayList<ChannelHandler>();
	
	public NetChannelInitializer(List<ChannelHandler> handlers) {
		this.handlers = handlers;
	}
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		
		p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN, 4096000, 0, 4, -4, 4, true));
		p.addLast(NetDecoder.class.getSimpleName(), new NetDecoder());
		p.addLast(NetEncoder.class.getSimpleName(), new NetEncoder());
		for(ChannelHandler handler : handlers) {
			p.addLast(handler.getClass().getSimpleName(), handler);
		}
	}

}
