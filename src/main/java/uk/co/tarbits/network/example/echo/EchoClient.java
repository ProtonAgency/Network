package uk.co.tarbits.network.example.echo;

import java.net.InetSocketAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalChannel;
import uk.co.tarbits.network.pipeline.BasicChannelInitializer;
import uk.co.tarbits.network.pipeline.Session;
import uk.co.tarbits.network.protocol.SimpleProtocol;
import uk.co.tarbits.network.util.BootstrapUtil;
import uk.co.tarbits.network.util.ByteBufUtil;

/**
 * Connects to a server, sends an {@link EchoMessage} and then terminates.
 * 
 * @author Joseph Tarbit
 *
 */
public class EchoClient {

  /**
   * Entry point.
   * 
   * @param args - program args
   */
  public static void main(String[] args) {
    EventLoopGroup group = BootstrapUtil.createBestEventLoopGroup(0, null);
    Channel channel = new Bootstrap() //
        .group(group) //
        .channel(LocalChannel.class) //
        .handler(new BasicChannelInitializer(() -> new Session(new SimpleProtocol<Session>() {
          {
            this.registerMessage(0x00, EchoMessage.class, (msg, out) -> {
              ByteBufUtil.writeUtf8(out, msg.getText());
            }, (in) -> new EchoMessage(ByteBufUtil.readUtf8(in)), null);
          }
        }))) //
        .connect(new InetSocketAddress(25575)).syncUninterruptibly().channel();
    channel.writeAndFlush(new EchoMessage("Hello Server!"));
    channel.close().syncUninterruptibly();
    group.shutdownGracefully().syncUninterruptibly();
  }
}
