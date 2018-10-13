package sm0keysa1m0n.network.system;

import java.net.InetSocketAddress;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import sm0keysa1m0n.network.message.MessageIndex;
import sm0keysa1m0n.network.pipeline.NettyChannelInitializer;
import sm0keysa1m0n.network.wrapper.NetworkManager;
import sm0keysa1m0n.network.wrapper.Session;

public class NettyClient extends NettySystem<Channel, Bootstrap> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

	public NettyClient(boolean useEpoll) {
		super(Bootstrap.class, "client", EpollSocketChannel.class, NioSocketChannel.class, useEpoll);
	}

	@Nullable
	public NetworkManager connect(InetSocketAddress address, MessageIndex messageIndex,
			Supplier<? extends Session> sessionSupplier) {
		if (address.getAddress() instanceof java.net.Inet6Address)
			System.setProperty("java.net.preferIPv4Stack", "false");

		Bootstrap bootstrap = this.getBootstrap();
		bootstrap.handler(new NettyChannelInitializer(messageIndex, sessionSupplier)).option(ChannelOption.TCP_NODELAY,
				true);
		ChannelFuture future = bootstrap.connect(address).syncUninterruptibly();

		if (future.isSuccess()) {
			LOGGER.info("Successfully connected to {}", address.toString());
			return future.channel().pipeline().get(NetworkManager.class);
		} else {
			LOGGER.warn(
					"Could not connect to {}, make sure the remote host is listening for connections on that address",
					address.toString());
			return null;
		}
	}

}
