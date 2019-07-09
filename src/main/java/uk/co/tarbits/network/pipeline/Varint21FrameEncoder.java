package uk.co.tarbits.network.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import uk.co.tarbits.network.util.ByteBufUtil;

/**
 * A {@link MessageToByteEncoder} which encodes {@link ByteBuf}s into frames.
 * 
 * @author Joseph Tarbit
 *
 */
public class Varint21FrameEncoder extends MessageToByteEncoder<ByteBuf> {

  @Override
  protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
    int readableBytes = in.readableBytes();
    int size = ByteBufUtil.getVarIntSize(readableBytes);

    if (size > 3) {
      throw new IllegalArgumentException("Unable to fit " + readableBytes + " into " + 3);
    } else {
      out.ensureWritable(size + readableBytes);
      ByteBufUtil.writeVarInt(out, readableBytes);
      out.writeBytes(in, in.readerIndex(), readableBytes);
    }
  }
}
