package com.recastproductions.network.impl.pipeline;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.recastproductions.network.impl.NetworkRegistry;
import com.recastproductions.network.impl.PacketHeartbeat;
import com.recastproductions.network.impl.Protocol;
import com.recastproductions.network.impl.server.NetworkRegistryServer;
import com.recastproductions.network.packet.IPacket;
import com.recastproductions.network.util.ByteBufUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PacketDecoder extends ByteToMessageDecoder {

	public static final String PIPELINE_NAME = "packet_decoder";

	private static final Logger LOGGER = LoggerFactory.getLogger(PacketDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() != 0) {
			int discriminator = ByteBufUtils.readVarInt(in);
			Class<? extends IPacket> packetClass = null;

			if (discriminator == -1) {
				NetworkRegistry registry = ctx.channel().attr(NetworkRegistry.INSTANCE_ATTR).get();
				if (registry instanceof NetworkRegistryServer) {
					String protocolName = ByteBufUtils.readUTF8(in);
					packetClass = ((NetworkRegistryServer) registry).getProtocol(protocolName)
							.getHandshakePacketClass();
				}
			} else if (discriminator == -2) {
				packetClass = PacketHeartbeat.class;
			} else {
				Protocol<?> protocol = ctx.channel().attr(Protocol.PROTOCOL_ATTR).get();
				if (protocol != null) {
					packetClass = protocol.getPacket(discriminator);
				}
			}

			if (packetClass != null) {
				IPacket packet = packetClass.newInstance();
				try {
					packet.fromBytes(in);
				} catch (Exception e) {
					LOGGER.warn("An exception was thrown while decoding " + packet.getClass().getCanonicalName(), e);
				}
				if (in.readableBytes() > 0) {
					throw new IOException("Packet " + discriminator + " (" + packetClass.getCanonicalName()
							+ ") was larger than I expected, found " + in.readableBytes()
							+ " bytes extra whilst reading packet " + discriminator);
				}
				out.add(packet);
			} else {
				LOGGER.info("Bad packet discriminator {}", discriminator);
			}
		}
	}

}
