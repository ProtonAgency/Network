package sm0keysa1m0n.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import sm0keysa1m0n.network.pipeline.NettyChannelInitializer;
import sm0keysa1m0n.network.protocol.IProtocol;

public class NetworkServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServer.class);

	private final List<ChannelFuture> endpoints = Collections.<ChannelFuture>synchronizedList(Lists.newArrayList());

	public void addEndpoint(InetSocketAddress address, boolean useNativeTransports, IProtocol bootstrapProtocol)
			throws IOException {
		if (address.getAddress() instanceof java.net.Inet6Address)
			System.setProperty("java.net.preferIPv4Stack", "false");

		synchronized (this.endpoints) {
			Class<? extends ServerSocketChannel> channelClass;
			EventLoopGroup eventLoopGroup;

			if (Epoll.isAvailable() && useNativeTransports) {
				channelClass = EpollServerSocketChannel.class;
				eventLoopGroup = new EpollEventLoopGroup(0,
						(new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").build());
				LOGGER.info("Using epoll channel type");
			} else {
				channelClass = NioServerSocketChannel.class;
				eventLoopGroup = new NioEventLoopGroup(0,
						(new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").build());
				LOGGER.info("Using default channel type");
			}

			this.endpoints.add(new ServerBootstrap().channel(channelClass)
					.childHandler(new NettyChannelInitializer(bootstrapProtocol))
					.childOption(ChannelOption.TCP_NODELAY, true).group(eventLoopGroup).localAddress(address).bind()
					.addListener(new GenericFutureListener<Future<? super Void>>() {

						@Override
						public void operationComplete(Future<? super Void> f) throws Exception {
							if (f.isSuccess()) {
								LOGGER.info("Bind success! Now listening for connections on {}", address.toString());
							} else {
								LOGGER.warn(
										"Bind failed! Check no other services are running on {} and that the address specified exists on the local host",
										address.toString());
							}
						}

					}).syncUninterruptibly());
		}
	}

	/**
	 * Shuts down all open endpoints
	 */
	public void terminateEndpoints() {
		for (ChannelFuture channelfuture : this.endpoints) {
			try {
				channelfuture.channel().close().sync();
			} catch (InterruptedException var4) {
				LOGGER.error("Interrupted whilst closing channel");
			}
		}
	}
}
