package com.recastproductions.network.impl.client;

import com.recastproductions.network.impl.Protocol;
import com.recastproductions.network.impl.NetworkRegistry;
import com.recastproductions.network.impl.Session;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class NetworkRegistryClient extends NetworkRegistry {

	private ProtocolClient<?, ?> protocol;

	public NetworkRegistryClient(ProtocolClient<?, ?> protocol) {
		this.protocol = protocol;
	}

	public ProtocolClient<?, ?> getProtocol() {
		return this.protocol;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		LOGGER.info("Sending handshake packet");
		try {
			ctx.channel().attr(Protocol.PROTOCOL_ATTR).set(protocol);
			ctx.channel().writeAndFlush(this.protocol.getHandshakePacket()).channel().pipeline()
					.addLast(Session.PIPELINE_NAME, this.protocol.onConnected(ctx.channel()));
		} catch (Exception e) {
			throw new RuntimeException("Could not send handshake packet", e);
		}
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		;
	}

}
