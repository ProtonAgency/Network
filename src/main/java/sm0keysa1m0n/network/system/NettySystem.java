package sm0keysa1m0n.network.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public abstract class NettySystem<C extends Channel, B extends AbstractBootstrap<B, C>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettySystem.class);

	private final EventLoopGroup eventLoopGroup;
	private final B bootstrap;

	public NettySystem(Class<B> bootstrapClass, String name, Class<? extends C> epollChannel,
			Class<? extends C> nioChannel, boolean useEpoll) {
		try {
			this.bootstrap = bootstrapClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("Could not instantiate bootstrap class", e);
		}
		Class<? extends C> channelClass;
		if (Epoll.isAvailable() && useEpoll) {
			channelClass = epollChannel;
			this.eventLoopGroup = new EpollEventLoopGroup(0,
					new ThreadFactoryBuilder().setNameFormat("Netty Epoll " + name + " IO #%d").build());
			LOGGER.info("Using epoll channel type");
		} else {
			channelClass = nioChannel;
			this.eventLoopGroup = new NioEventLoopGroup(0,
					new ThreadFactoryBuilder().setNameFormat("Netty " + name + " IO #%d").build());
			LOGGER.info("Using default channel type");
		}
		this.bootstrap.group(this.eventLoopGroup).channel(channelClass);
	}

	protected B getBootstrap() {
		return this.bootstrap;
	}

	public void shutdown() {
		this.eventLoopGroup.shutdownGracefully();
	}

}