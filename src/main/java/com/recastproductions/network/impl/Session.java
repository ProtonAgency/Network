package com.recastproductions.network.impl;

import com.recastproductions.network.packet.IPacket;
import com.recastproductions.network.packet.IPacketContext;
import com.recastproductions.network.packet.IPacketHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public abstract class Session<P extends Protocol<?>> extends SimpleChannelInboundHandler<IPacket>
		implements IPacketContext {

	public static final String PIPELINE_NAME = "session";

	private final P protocol;

	private final Channel channel;

	public Session(Channel ch, P protocol) {
		this.channel = ch;
		this.protocol = protocol;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IPacket packet) throws Exception {
		this.handlePacket(packet);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			ctx.writeAndFlush(new PacketHeartbeat()).addListener((ChannelFutureListener) future -> {
				if (!future.isSuccess()) {
					future.channel().close();
				}
			});
		}
	}

	protected <T extends IPacket> void handlePacket(T packet) {
		@SuppressWarnings("unchecked")
		IPacketHandler<T, ?, ? super Session<?>> handler = (IPacketHandler<T, ?, ? super Session<?>>) protocol
				.getPacketHandler(packet.getClass());
		if (handler != null) {
			handler.processPacket(packet, this);
		}
	}

	public void sendPacket(IPacket packet) {
		this.channel.writeAndFlush(packet);
	}

	public P getProtocol() {
		return this.protocol;
	}

	public Channel getChannel() {
		return this.channel;
	}

}
