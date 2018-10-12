package sm0keysa1m0n.network.pipeline;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import sm0keysa1m0n.network.NetworkManager;
import sm0keysa1m0n.network.protocol.IProtocol;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {

	private final IProtocol bootstrapProtocol;

	public NettyChannelInitializer(IProtocol bootstrapProtocol) {
		this.bootstrapProtocol = bootstrapProtocol;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast("frame_decoder", new NettyVarint21FrameDecoder())
				.addLast("message_decoder", new NettyMessageDecoder())
				.addLast("frame_encoder", new NettyVarint21FrameEncoder())
				.addLast("message_encoder", new NettyMessageEncoder())
				.addLast("network_manager", new NetworkManager(bootstrapProtocol));
	}

}