package sm0keysa1m0n.network;

import java.net.InetSocketAddress;
import java.util.function.Function;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import sm0keysa1m0n.network.pipeline.BasicChannelInitializer;
import sm0keysa1m0n.network.pipeline.Session;

/**
 * A simple socket client.
 * 
 * @author Joseph Tarbit
 *
 */
public class SocketClient {

	protected final EventLoopGroup workerGroup;
	protected final Bootstrap bootstrap;

	/**
	 * Constructs a new {@link SocketClient}.
	 * 
	 * @param sessionFactory - a simple {@link Function} that returns a new
	 *                       {@link Session} for the designated {@link Channel}
	 */
	public SocketClient(Function<Channel, Session> sessionFactory) {
		this.workerGroup = TransportSelector.createBestEventLoopGroup();
		this.bootstrap = new Bootstrap();
		this.bootstrap.group(this.workerGroup).channel(NioSocketChannel.class)
				.handler(new BasicChannelInitializer(sessionFactory));
	}

	/**
	 * Connects to the specified {@link InetSocketAddress}.
	 * 
	 * @param address - the {@link InetSocketAddress} in which to connect to
	 * @return the {@link ChannelFuture}
	 */
	public ChannelFuture connect(InetSocketAddress address) {
		return this.bootstrap.connect(address);
	}

	/**
	 * Shuts down all client processes.
	 * 
	 * @throws InterruptedException
	 */
	public void shutdown() throws InterruptedException {
		this.bootstrap.config().group().shutdownGracefully();
		this.bootstrap.config().group().terminationFuture().sync();
	}
}