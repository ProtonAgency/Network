package sm0keysa1m0n.network.pipeline;

import java.util.function.Function;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Initialises all {@link Channel}s.
 * 
 * @author Joseph Tarbit
 *
 */
public class BasicChannelInitializer extends ChannelInitializer<SocketChannel> {

	private final Function<Channel, Session> sessionFactory;

	/**
	 * Constructs a new {@link BasicChannelInitializer}.
	 * 
	 * @param sessionFactory - a simple {@link Function} that returns a new
	 *                       {@link Session} for the designated {@link Channel}
	 */
	public BasicChannelInitializer(Function<Channel, Session> sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	protected final void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("frame_decoder", new Varint21FrameDecoder())
				.addLast("message_decoder", new MessageDecoder()).addLast("frame_encoder", new Varint21FrameEncoder())
				.addLast("message_encoder", new MessageEncoder()).addLast("session", this.sessionFactory.apply(ch));
	}
}
