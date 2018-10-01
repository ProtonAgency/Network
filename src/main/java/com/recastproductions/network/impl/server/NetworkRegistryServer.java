package com.recastproductions.network.impl.server;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.recastproductions.network.impl.NetworkRegistry;
import com.recastproductions.network.impl.pipeline.HandshakeHandler;
import com.recastproductions.network.packet.IHandshakePacket;

import io.netty.channel.Channel;

public class NetworkRegistryServer extends NetworkRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkRegistryServer.class);

	private BiMap<String, Class<? extends IHandshakePacket>> handshakeMap = HashBiMap.create();
	private BiMap<String, ProtocolServer<?, ?>> handlerMap = HashBiMap.create();

	public void registerProtocol(ProtocolServer<?, ?> protocolServer) {
		if (!this.handlerMap.containsKey(protocolServer.getName())) {
			this.handlerMap.put(protocolServer.getName(), protocolServer);
			this.handshakeMap.put(protocolServer.getName(), protocolServer.getHandshakePacketClass());
		} else {
			LOGGER.warn("A protocol with the name {} is already registered under the class {}",
					protocolServer.getName(), handlerMap.get(protocolServer.getName()).getClass().getCanonicalName());
		}
	}

	public void removeProtocol(String name) {
		handshakeMap.remove(name);
		handlerMap.remove(name);
	}

	public String getName(Class<? extends IHandshakePacket> handshakeMessage) {
		return handshakeMap.inverse().get(handshakeMessage);
	}

	public ProtocolServer<?, ?> getProtocol(String name) {
		return handlerMap.get(name);
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		for (Entry<String, ProtocolServer<?, ?>> entry : handlerMap.entrySet()) {
			ch.pipeline().addLast(entry.getKey(), this.wrapHandshakeHandler((ProtocolServer<?, ?>) entry.getValue()));
		}
	}

	private <T extends IHandshakePacket, P extends ProtocolServer<T, ?>> HandshakeHandler<T, P> wrapHandshakeHandler(
			P protocol) {
		return new HandshakeHandler<T, P>(protocol.getHandshakePacketClass(), protocol);
	}

}
