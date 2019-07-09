package uk.co.tarbits.network.pipeline;

import java.util.concurrent.Callable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * Initialises all {@link Channel}s.
 * 
 * @author Joseph Tarbit
 *
 */
public class BasicChannelInitializer extends ChannelInitializer<Channel> {

  private final Callable<Session> sessionFactory;

  /**
   * Constructs a new {@link BasicChannelInitializer}.
   * 
   * @param sessionFactory - supplies a {@link Session}
   */
  public BasicChannelInitializer(Callable<Session> sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  protected void initChannel(Channel ch) throws Exception {
    ch.pipeline().addLast("frame_decoder", new Varint21FrameDecoder())
        .addLast("message_decoder", new MessageDecoder())
        .addLast("frame_encoder", new Varint21FrameEncoder())
        .addLast("message_encoder", new MessageEncoder())
        .addLast("session", this.sessionFactory.call());
  }
}
