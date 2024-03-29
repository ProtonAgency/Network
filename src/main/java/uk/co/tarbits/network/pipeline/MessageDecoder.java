package uk.co.tarbits.network.pipeline;

import java.io.IOException;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import uk.co.tarbits.network.protocol.IMessage;
import uk.co.tarbits.network.protocol.IProtocol;
import uk.co.tarbits.network.util.ByteBufUtil;

/**
 * A {@link ByteToMessageDecoder} which decodes {@link ByteBuf}s into {@link IMessage}s.
 * 
 * @author Joseph Tarbit
 *
 */
public class MessageDecoder extends ByteToMessageDecoder {

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (in.readableBytes() != 0) {
      int index = ByteBufUtil.readVarInt(in);
      IProtocol<?> protocol = ((Session) ctx.pipeline().get("session")).getProtocol();
      if (protocol == null) {
        throw new IllegalStateException("No protocol has been set for the channel");
      } else {
        IMessage msg = protocol.getMessageEntry(index).getDecoder().decode(in);
        if (in.readableBytes() > 0) {
          throw new IOException(
              "Message [" + protocol + ":" + index + "] " + msg.getClass().getName()
                  + " was larger than I expected, found " + in.readableBytes() + " bytes extra");
        } else {
          out.add(msg);
        }
      }
    }
  }
}
