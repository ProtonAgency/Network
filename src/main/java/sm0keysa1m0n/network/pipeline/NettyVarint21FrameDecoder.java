package sm0keysa1m0n.network.pipeline;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import sm0keysa1m0n.network.util.ByteBufUtil;

public class NettyVarint21FrameDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		in.markReaderIndex();
		byte[] abyte = new byte[3];

		for (int i = 0; i < abyte.length; ++i) {
			if (!in.isReadable()) {
				in.resetReaderIndex();
				return;
			}

			abyte[i] = in.readByte();

			if (abyte[i] >= 0) {
				ByteBuf packetBytes = Unpooled.wrappedBuffer(abyte);

				try {
					int j = ByteBufUtil.readVarInt(packetBytes);

					if (in.readableBytes() >= j) {
						out.add(in.readBytes(j));
						return;
					}

					in.resetReaderIndex();
				} finally {
					packetBytes.release();
				}

				return;
			}
		}

		throw new CorruptedFrameException("Length wider than 21-bit");
	}

}
