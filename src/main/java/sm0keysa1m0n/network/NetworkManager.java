package sm0keysa1m0n.network;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import sm0keysa1m0n.network.message.IMessage;
import sm0keysa1m0n.network.pipeline.NettyChannelInitializer;
import sm0keysa1m0n.network.protocol.IProtocol;
import sm0keysa1m0n.network.protocol.ISession;

public class NetworkManager extends SimpleChannelInboundHandler<IMessage> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkManager.class);

	private Channel channel;

	private IProtocol protocol;

	private ISession session;

	public NetworkManager(IProtocol bootstrapProtocol) {
		this.protocol = bootstrapProtocol;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.channel = ctx.channel();
		this.setProtocol(this.protocol);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
		this.protocol.getMessageListener().messageReceived(msg, this);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
		LOGGER.warn("Exception caught in session", t);
	}

	public void setProtocol(IProtocol protocol) {
		this.protocol = protocol;
		this.session.sessionInactive();
		this.session = this.protocol.newSession(this);
		this.session.sessionActive();
	}

	public void sendMessage(IMessage msg) {
		this.channel.writeAndFlush(msg);
	}

	public IProtocol getProtocol() {
		return this.protocol;
	}

	public static NetworkManager connectToServer(InetSocketAddress address, boolean useNativeTransport,
			IProtocol bootstrapProtocol) {
		if (address.getAddress() instanceof java.net.Inet6Address)
			System.setProperty("java.net.preferIPv4Stack", "false");

		Class<? extends SocketChannel> channelClass;
		EventLoopGroup eventLoopGroup;

		if (Epoll.isAvailable() && useNativeTransport) {
			channelClass = EpollSocketChannel.class;
			eventLoopGroup = new EpollEventLoopGroup(0,
					new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").build());
			LOGGER.info("Using epoll channel type");
		} else {
			channelClass = NioSocketChannel.class;
			eventLoopGroup = new NioEventLoopGroup(0,
					new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").build());
			LOGGER.info("Using default channel type");
		}

		Bootstrap bootstrap = new Bootstrap().group(eventLoopGroup)
				.handler(new NettyChannelInitializer(bootstrapProtocol)).option(ChannelOption.TCP_NODELAY, true)
				.channel(channelClass);

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
