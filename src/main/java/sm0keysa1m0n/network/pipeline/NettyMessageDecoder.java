package sm0keysa1m0n.network.pipeline;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import sm0keysa1m0n.network.message.Message;
import sm0keysa1m0n.network.message.MessageIndex;
import sm0keysa1m0n.network.util.ByteBufUtil;
import sm0keysa1m0n.network.wrapper.NetworkManager;

public class NettyMessageDecoder extends ByteToMessageDecoder {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyMessageDecoder.class);

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() != 0) {
			int discriminator = ByteBufUtil.readVarInt(in);
			MessageIndex index = ctx.pipeline().get(NetworkManager.class).getMessageIndex();
			if (index == null) {
				throw new IllegalStateException("No message index has been set for the channel");
			} else {
				Class<? extends Message> msgClass = index.getMessage(discriminator);
				if (msgClass == null) {
					throw new IOException("Unknown message discriminator " + discriminator);
				} else {
					Message msg = msgClass.newInstance();
					msg.fromBytes(in);
					if (in.readableBytes() > 0) {
						throw new IOException("Message [" + index + ":" + discriminator + "] " + msgClass.getName()
								+ " was larger than I expected, found " + in.readableBytes() + " bytes extra");
					} else {
						out.add(msg);
						LOGGER.debug("MESSAGE IN: [{}:{}] {}", index, discriminator, msgClass.getName());
					}
				}
			}

		}
	}

}