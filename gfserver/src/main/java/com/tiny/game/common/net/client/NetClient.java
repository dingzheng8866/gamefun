package com.tiny.game.common.net.client;

import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.netty.NetChannelInitializer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NetClient {
	private static Logger logger = LoggerFactory.getLogger(NetClient.class);
	
	public Bootstrap bootstrap = new Bootstrap();
	public EventLoopGroup workerGroup = null;
	
	private List<ChannelHandler> channelHandlers;
	
	public NetClient(List<ChannelHandler> channelHandlers) {
		this.channelHandlers = channelHandlers;
		Class<? extends Channel> sc = EpollSocketChannel.class;
		if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC) { 
			sc = NioSocketChannel.class;
			workerGroup = new NioEventLoopGroup(1);
		} else {
			workerGroup = new EpollEventLoopGroup(1);
		}
		
		bootstrap.group(workerGroup).channel(sc);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
		bootstrap.option(ChannelOption.SO_LINGER, 0);
		bootstrap.handler(new NetChannelInitializer(channelHandlers));
	}
	
//	public Channel connect(String host, int port) {
//		try {
//			ChannelFuture future = bootstrap.connect(host, port).sync();
//			if (future.isCancelled()) {
//				logger.error("cancelled to connect to: " + host +"," + port);
//				return null;
//			} else if (!future.isSuccess()) {
//				logger.error("failed to connect to: " + host +"," + port);
//				return null;
//			}
//			clientChannel = future.channel();
//			
//			return clientChannel;
//		} catch (Exception e) {
//			logger.error("failed to connect to: " + host +"," + port +", error: " + e.getMessage());
//			return null;
//		}
//	}

	public Channel connect(String host, int port) {
		try {
			ChannelFuture future = bootstrap.connect(host, port).sync();
			future.awaitUninterruptibly();

			assert future.isDone();

			if (future.isCancelled()) {
				// Connection attempt cancelled by user
				logger.error("cancelled to connect to: " + host +"," + port);
				return null;
			} else if (!future.isSuccess()) {
				logger.error("failed to connect to: " + host +"," + port);
				return null;
			}
			
			Channel channel = future.channel();
			logger.info("successfully connect to server!host={},port={}", host, port);
			return channel;
		} catch (Exception e) {
			logger.info("Failed to connnect to: " + host+":"+port+", error: " + e.getMessage());
			return null;
		}
	}	
	
	public void close() {
		workerGroup.shutdownGracefully();
	}
}
