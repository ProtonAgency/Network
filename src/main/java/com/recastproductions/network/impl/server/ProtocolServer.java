package com.recastproductions.network.impl.server;

import com.recastproductions.network.impl.Protocol;
import com.recastproductions.network.impl.Session;
import com.recastproductions.network.packet.IHandshakePacket;
import io.netty.channel.Channel;

import javax.annotation.Nullable;

/**
 * Server side protocol, used to handle incoming handshake requests
 *
 * @param <HS> - the {@link IHandshakePacket} that this {@link ProtocolServer}
 *        listens for
 * @author Sm0keySa1m0n
 */
public abstract class ProtocolServer<HS extends IHandshakePacket, S extends Session<?>> extends Protocol<HS> {

	/**
	 * Processes a handshake sent by the client
	 *
	 * @param packet - the handshake itself
	 * @param ch     - the channel sending the handshake
	 * @return if the client is allowed to send any more handshakes
	 */
	@Nullable
	public abstract S processHandshake(HS packet, Channel ch);

	/**
	 * Get the handshake packet assigned to this {@link Protocol}
	 *
	 * @return the packet {@link Class}
	 */
	public abstract Class<HS> getHandshakePacketClass();

}
