package sm0keysa1m0n.network.system;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import sm0keysa1m0n.network.message.MessageIndex;
import sm0keysa1m0n.network.pipeline.NettyChannelInitializer;
import sm0keysa1m0n.network.wrapper.Session;

public class NettyServer extends NettySystem<ServerChannel, ServerBootstrap> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

	private final List<Channel> endpoints = Collections.<Channel>synchronizedList(Lists.newArrayList());

	public NettyServer(boolean useEpoll) {
		super(ServerBootstrap.class, "server", EpollServerSocketChannel.class, NioServerSocketChannel.class,
				useEpoll);
	}

	public void addEndpoint(InetSocketAddress address, MessageIndex messageIndex, Supplier<? extends Session> sessionSupplier) throws IOException {
		if (address.getAddress() instanceof java.net.Inet6Address)
			System.setProperty("java.net.preferIPv4Stack", "false");

		ServerBootstrap bootstrap = this.getBootstrap();
		ChannelFuture future = bootstrap.childHandler(new NettyChannelInitializer(messageIndex, sessionSupplier))
				.childOption(ChannelOption.TCP_NODELAY, true).localAddress(address).bind().syncUninterruptibly();
		if (future.isSuccess()) {
			this.endpoints.add(future.channel());
			LOGGER.info("Bind success! Now listening for connections on {}", address.toString());
		} else {
			LOGGER.warn(
					"Bind failed! Check no other services are running on {} and that the address specified exists on the local host",
					address.toString());
		}

	}

	/**
	 * Close all open endpoints
	 */
	public void closeEndpoints() {
		for (Channel channel : this.endpoints) {
			try {
				channel.close().sync();
			} catch (InterruptedException var4) {
				LOGGER.error("Interrupted whilst closing endpoint channel");
			}
		}
	}
}
