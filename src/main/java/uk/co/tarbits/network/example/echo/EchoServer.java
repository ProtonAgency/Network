package uk.co.tarbits.network.example.echo;

import java.net.InetSocketAddress;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import uk.co.tarbits.network.pipeline.BasicChannelInitializer;
import uk.co.tarbits.network.pipeline.Session;
import uk.co.tarbits.network.protocol.SimpleProtocol;
import uk.co.tarbits.network.util.BootstrapUtil;
import uk.co.tarbits.network.util.ByteBufUtil;

/**
 * Listens for connections and terminates when {@link EchoMessage} is received.
 * 
 * @author Joseph Tarbit
 *
 */
public class EchoServer {

  /**
   * Entry point.
   * 
   * @param args - program args
   */
  public static void main(String[] args) {
    EventLoopGroup group = BootstrapUtil.createBestEventLoopGroup(0, null);
    Channel channel = new ServerBootstrap() //
        .group(group) //
        .channel(LocalServerChannel.class) //
        .childHandler(new BasicChannelInitializer(() -> new Session(new SimpleProtocol<Session>() {
          {
            this.registerMessage(0x00, EchoMessage.class, (msg, out) -> {
              ByteBufUtil.writeUtf8(out, msg.getText());
            }, (in) -> new EchoMessage(ByteBufUtil.readUtf8(in)), (msg, session) -> {
              System.out.println("NEW MESSAGE FROM " + session.getRemoteAddress().toString() + ": "
                  + msg.getText());
            });
          }
        }))) //
        .bind(new InetSocketAddress(25575)).syncUninterruptibly().channel();
    channel.closeFuture().syncUninterruptibly();
    group.shutdownGracefully().syncUninterruptibly();
  }
}
