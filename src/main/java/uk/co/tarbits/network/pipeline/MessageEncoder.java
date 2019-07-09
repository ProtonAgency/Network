package uk.co.tarbits.network.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import uk.co.tarbits.network.protocol.IMessage;
import uk.co.tarbits.network.protocol.IProtocol;
import uk.co.tarbits.network.protocol.IProtocol.MessageEntry;
import uk.co.tarbits.network.util.ByteBufUtil;

/**
 * A {@link MessageToByteEncoder} which encodes {@link IMessage}s into {@link ByteBuf}s.
 * 
 * @author Joseph Tarbit
 *
 */
public class MessageEncoder extends MessageToByteEncoder<IMessage> {

  @Override
  protected void encode(ChannelHandlerContext ctx, IMessage msg, ByteBuf out) throws Exception {
    IProtocol<?> protocol = ((Session) ctx.pipeline().get("session")).getProtocol();
    if (protocol == null) {
      throw new IllegalStateException("No protocol has been set for the channel");
    } else {
      @SuppressWarnings("unchecked")
      MessageEntry<IMessage, ?> messageEntry =
          (MessageEntry<IMessage, ?>) protocol.getMessageEntry(msg.getClass());
      ByteBufUtil.writeVarInt(out, messageEntry.getIndex());
      messageEntry.getEncoder().encode(msg, out);
    }
  }
}
