package com.recastproductions.network.impl.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.recastproductions.network.impl.NetworkRegistry;
import com.recastproductions.network.impl.PacketHeartbeat;
import com.recastproductions.network.impl.Protocol;
import com.recastproductions.network.impl.client.NetworkRegistryClient;
import com.recastproductions.network.packet.IHandshakePacket;
import com.recastproductions.network.packet.IPacket;
import com.recastproductions.network.util.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketEncoder extends MessageToByteEncoder<IPacket> {

	public static final String PIPELINE_NAME = "packet_endcoder";

	private static final Logger LOGGER = LoggerFactory.getLogger(PacketEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, IPacket packet, ByteBuf out) throws Exception {
		if (packet instanceof IHandshakePacket) {
			NetworkRegistry registry = ctx.channel().attr(NetworkRegistry.INSTANCE_ATTR).get();
			if (registry instanceof NetworkRegistryClient) {
				String protocolName = ((NetworkRegistryClient) registry).getProtocol().getName();
				// Write discriminator
				ByteBufUtils.writeVarInt(out, -1);
				// Write net handler name
				ByteBufUtils.writeUTF8(out, protocolName);
				// Write message
				try {
					packet.toBytes(out);
				} catch (Exception e) {
					LOGGER.warn("An exception was thrown while encoding " + packet.getClass().getCanonicalName(), e);
				}
			} else {
				LOGGER.warn("Sending handshake packet from wrong side!");
				return;
			}
		} else if (packet instanceof PacketHeartbeat) {
			ByteBufUtils.writeVarInt(out, -2);
		} else {
			Protocol<?> protocol = ctx.channel().attr(Protocol.PROTOCOL_ATTR).get();
			if (protocol != null) {
				Integer discriminator = protocol.getDiscriminator(packet.getClass());
				if (discriminator != null) {
					// Write discriminator
					ByteBufUtils.writeVarInt(out, discriminator);
					// Write message
					try {
						packet.toBytes(out);
					} catch (Exception e) {
						LOGGER.error("An exception was thrown while encoding a packet", e);
					}
				} else {
					LOGGER.warn("No packet discriminator found for {}", packet.getClass().getCanonicalName());
				}
			} else {
				LOGGER.warn("No protocol found!");
				return;
			}
		}
	}

}
