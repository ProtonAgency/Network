package uk.co.tarbits.network.pipeline;

import java.net.SocketAddress;
import java.util.function.BiConsumer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import uk.co.tarbits.network.protocol.IMessage;
import uk.co.tarbits.network.protocol.IProtocol;

/**
 * A {@link SimpleChannelInboundHandler} used to represent and handle a connection.
 * 
 * @author Joseph Tarbit
 *
 */
public class Session extends SimpleChannelInboundHandler<IMessage> {

  protected Channel channel;

  @Getter
  protected IProtocol<?> protocol;

  /**
   * Constructs a new {@link Session}.
   * 
   * @param protocol - the {@link IProtocol} used to encode, decode and handle {@link IMessages}
   */
  public Session(IProtocol<?> protocol) {
    this.protocol = protocol;
  }

  @Override
  public final void handlerAdded(ChannelHandlerContext ctx) {
    this.channel = ctx.channel();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected final void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
    this.protocol.getMessageEntry(msg.getClass()).getMessageConsumer().ifPresent((consumer) -> {
      ((BiConsumer<IMessage, Session>) consumer).accept(msg, this);
    });
  }

  public void sendMessage(IMessage msg) {
    if (this.channel.eventLoop().inEventLoop()) {
      ChannelFuture future = this.channel.writeAndFlush(msg);
      future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    } else {
      this.channel.eventLoop().execute(() -> {
        ChannelFuture future = this.channel.writeAndFlush(msg);
        future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      });
    }
  }

  public void closeChannel() {
    if (this.channel.isOpen()) {
      this.channel.close().awaitUninterruptibly();
    }
  }

  public SocketAddress getRemoteAddress() {
    return this.channel.remoteAddress();
  }
}
