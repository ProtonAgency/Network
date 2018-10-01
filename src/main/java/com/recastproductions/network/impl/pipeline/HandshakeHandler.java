package com.recastproductions.network.impl.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.recastproductions.network.impl.Protocol;
import com.recastproductions.network.impl.Session;
import com.recastproductions.network.impl.server.ProtocolServer;
import com.recastproductions.network.packet.IHandshakePacket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HandshakeHandler<REQ extends IHandshakePacket, P extends ProtocolServer<REQ, ?>>
		extends SimpleChannelInboundHandler<REQ> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HandshakeHandler.class);

	private final P protocol;

	public HandshakeHandler(Class<REQ> handshakePacketType, P protocol) {
		super(handshakePacketType);
		this.protocol = protocol;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, REQ msg) throws Exception {
		Session<?> session = protocol.processHandshake(msg, ctx.channel());
		if (session != null) {
			ctx.channel().attr(Protocol.PROTOCOL_ATTR).set(protocol);
			ctx.channel().pipeline().addLast(Session.PIPELINE_NAME, session);
			LOGGER.info("A new session has been established with the address {}",
					ctx.channel().remoteAddress().toString());
		} else {
			LOGGER.info("{} has failed to establish a session. Failed the handshake?",
					ctx.channel().remoteAddress().toString());
		}
	}

}
