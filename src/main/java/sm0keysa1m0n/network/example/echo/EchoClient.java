package sm0keysa1m0n.network.example.echo;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import sm0keysa1m0n.network.SocketClient;
import sm0keysa1m0n.network.example.echo.message.echo.EchoMessage;
import sm0keysa1m0n.network.example.echo.protocol.EchoProtocol;
import sm0keysa1m0n.network.pipeline.Session;

/**
 * Echo Client - connects to a server and sends an {@link EchoMessage}.
 * 
 * @author Joseph Tarbit
 *
 */
public class EchoClient {

	/**
	 * Entry point.
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		SocketClient client = new SocketClient((ch) -> new Session(ch, new EchoProtocol()) {
			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				this.channel.writeAndFlush(new EchoMessage("Hello Server!"));
			}
		});
		client.connect(new InetSocketAddress(25575)).sync().channel().closeFuture().sync();
		client.shutdown();
	}

}
