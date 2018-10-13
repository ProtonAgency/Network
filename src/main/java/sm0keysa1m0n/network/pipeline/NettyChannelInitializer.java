package sm0keysa1m0n.network.pipeline;

import java.util.function.Supplier;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import sm0keysa1m0n.network.message.MessageIndex;
import sm0keysa1m0n.network.wrapper.NetworkManager;
import sm0keysa1m0n.network.wrapper.Session;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

	private final MessageIndex messageIndex;
	private final Supplier<? extends Session> sessionSupplier;

	public NettyChannelInitializer(MessageIndex messageIndex, Supplier<? extends Session> sessionSupplier) {
		this.messageIndex = messageIndex;
		this.sessionSupplier = sessionSupplier;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("frame_decoder", new NettyVarint21FrameDecoder())
				.addLast("message_decoder", new NettyMessageDecoder())
				.addLast("frame_encoder", new NettyVarint21FrameEncoder())
				.addLast("message_encoder", new NettyMessageEncoder()).addLast("network_manager",
						new NetworkManager(sessionSupplier).setMessageIndex(messageIndex));
	}

}