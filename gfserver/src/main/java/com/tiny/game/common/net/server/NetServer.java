package com.tiny.game.common.net.server;

import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tiny.game.common.net.netty.NetChannelInitializer;
import com.tiny.game.common.server.ContextParameter;
import com.tiny.game.common.server.ServerContext;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NetServer {

	private static Logger logger = LoggerFactory.getLogger(NetServer.class);
	
	private int port;
	private ServerBootstrap bootstrap = null; 
	private EventLoopGroup bossGroup;

	private EventLoopGroup workerGroup;

	private Channel serverChannel;
	private List<ChannelHandler> channelHandlers;

	public NetServer(int port, List<ChannelHandler> channelHandlers) {
		this.port = port;
		this.channelHandlers = channelHandlers;
		bootstrap = new ServerBootstrap();
	}
	
	public void start() throws Exception {
		Class<? extends ServerChannel> sc = EpollServerSocketChannel.class;
		int workerThreads = ServerContext.getInstance().getPropertyInt(ContextParameter.NET_SERVER_INBOUND_WORKER_THREADS, "8");
		int coreNumber = Runtime.getRuntime().availableProcessors();
		if(workerThreads < coreNumber) {
			workerThreads = coreNumber;
		}
		if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC) { 
			sc = NioServerSocketChannel.class;
			bossGroup = new NioEventLoopGroup(2);
			workerGroup = new NioEventLoopGroup(workerThreads);
		} else {
			bossGroup = new EpollEventLoopGroup(2);
			workerGroup = new EpollEventLoopGroup(workerThreads);
		}
		
		bootstrap.group(bossGroup, workerGroup).channel(sc)
		.option(ChannelOption.SO_BACKLOG, 128)
		.option(ChannelOption.TCP_NODELAY, true)
		.childOption(ChannelOption.SO_KEEPALIVE, true)
		.childOption(ChannelOption.SO_REUSEADDR, true)
		.childHandler(new NetChannelInitializer(channelHandlers));
		
		serverChannel = bootstrap.bind(port).sync().channel();
		logger.info("Net server listen on port:{} successfully!", port);
	}

	public void shutdown() {
		try {
			// Wait until the server socket is closed.
			// In this example, this does not happen, but you can do that to gracefully
			// shut down your server.
			serverChannel.closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("Failed to close net server:" + e.getMessage(), e);
		}
		
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}

	
}
