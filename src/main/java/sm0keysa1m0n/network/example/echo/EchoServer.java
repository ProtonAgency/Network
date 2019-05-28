package sm0keysa1m0n.network.example.echo;

import java.net.InetSocketAddress;

import sm0keysa1m0n.network.SocketServer;
import sm0keysa1m0n.network.example.echo.protocol.EchoProtocol;
import sm0keysa1m0n.network.pipeline.Session;

/**
 * Echo Server - listens for connections.
 * 
 * @author Joseph Tarbit
 *
 */
public class EchoServer {

	/**
	 * Entry point.
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SocketServer server = new SocketServer((ch) -> new Session(ch, new EchoProtocol()));
		server.bind(new InetSocketAddress(25575)).sync().channel().closeFuture().sync();
	}

}
