package com.tiny.game.common.net.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.NetMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;


@Sharable
public class NetChannelInboundMessageHandler extends SimpleChannelInboundHandler<NetMessage> {
	
	private static Logger logger = LoggerFactory.getLogger(NetChannelInboundMessageHandler.class);
	
	private NetSessionHandler sessionHandler = null;
	
	public NetSessionHandler getSessionHandler() {
		return sessionHandler;
	}

	public void setSessionHandler(NetSessionHandler sessionHandler) {
		this.sessionHandler = sessionHandler;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelActive, remote_ip={}", ctx.channel().remoteAddress());
		sessionHandler.initNetSession(ctx.channel());
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("channelClose, remote_ip={}!", ctx.channel().remoteAddress());
		sessionHandler.closeNetSession(ctx.channel());
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//		logger.debug("channel registered with remote_ip={}!", ctx.channel().remoteAddress());
	}
	
	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//		logger.debug("channel unregistered with remote_ip={}!", ctx.channel().remoteAddress());
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, NetMessage msg)
			throws Exception {
		sessionHandler.processInboundMessage(ctx.channel(), msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("Unexpected exception from downstream.", cause);
		// TODO: let channel inactive?
		ctx.close();
	}
}
