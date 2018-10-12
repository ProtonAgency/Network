package sm0keysa1m0n.network.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import sm0keysa1m0n.network.util.ByteBufUtil;

public class NettyVarint21FrameEncoder extends MessageToByteEncoder<ByteBuf> {

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
