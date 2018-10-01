package com.recastproductions.network.impl.example;

import java.net.InetSocketAddress;

import com.recastproductions.network.NetworkClient;
import com.recastproductions.network.NetworkServer;
import com.recastproductions.network.impl.client.NetworkRegistryClient;
import com.recastproductions.network.impl.example.client.ProtocolClientTest;
import com.recastproductions.network.impl.example.server.ProtocolServerTest;
import com.recastproductions.network.impl.server.NetworkRegistryServer;

public class ImplExample {

	public static void main(String[] args) {
		if (args.length > 0) {
			if (args[0].equals("client")) {
				runClient();
			} else if (args[0].equals("server")) {
				runServer();
			}
		}
	}

	private static void runClient() {
		NetworkRegistryClient client = new NetworkRegistryClient(new ProtocolClientTest());

		NetworkClient netClient = new NetworkClient(client.getChannelInitializer());
		netClient.connect(new InetSocketAddress("127.0.0.1", 25545));
	}

	private static void runServer() {
		NetworkRegistryServer server = new NetworkRegistryServer();
		server.registerProtocol(new ProtocolServerTest());

		NetworkServer netServer = new NetworkServer(server.getChannelInitializer());
		netServer.bind(new InetSocketAddress("127.0.0.1", 25545));
	}

}
