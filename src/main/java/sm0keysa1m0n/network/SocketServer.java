package sm0keysa1m0n.network;

import java.net.InetSocketAddress;
import java.util.function.Function;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import sm0keysa1m0n.network.pipeline.BasicChannelInitializer;
import sm0keysa1m0n.network.pipeline.Session;

/**
 * A simple socket server.
 * 
 * @author Joseph Tarbit
 *
 */
public class SocketServer {

	protected final EventLoopGroup bossGroup;
	protected final EventLoopGroup workerGroup;
	protected final ServerBootstrap bootstrap;

	protected Channel channel;

	/**
	 * Constructs a new {@link SocketServer}.
	 * 
	 * @param sessionFactory - a simple {@link Function} that returns a new
	 *                       {@link Session} for the designated {@link Channel}
	 */
	public SocketServer(Function<Channel, Session> sessionFactory) {
		this.bossGroup = TransportSelector.createBestEventLoopGroup();
		this.workerGroup = TransportSelector.createBestEventLoopGroup();
		this.bootstrap = new ServerBootstrap();
		this.bootstrap.group(this.bossGroup, this.workerGroup).channel(TransportSelector.bestServerSocketChannel())
				.childHandler(new BasicChannelInitializer(sessionFactory)).childOption(ChannelOption.TCP_NODELAY, true)
				.childOption(ChannelOption.SO_KEEPALIVE, true);
	}

	/**
	 * Binds to the specified {@link InetSocketAddress}.
	 * 
	 * @param address - the {@link InetSocketAddress} in which to bind to
	 * @return the {@link ChannelFuture}
	 */
	public ChannelFuture bind(InetSocketAddress address) {
		ChannelFuture cfuture = this.bootstrap.bind(address);
		this.channel = cfuture.channel();
		return cfuture;
	}

	/**
	 * Closes the {@link ServerChannel} and shuts down all server processes.
	 * 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException {
		this.channel.close();
		this.bootstrap.config().group().shutdownGracefully();
		this.bootstrap.config().childGroup().shutdownGracefully();
		this.bootstrap.config().group().terminationFuture().sync();
		this.bootstrap.config().childGroup().terminationFuture().sync();
	}
}