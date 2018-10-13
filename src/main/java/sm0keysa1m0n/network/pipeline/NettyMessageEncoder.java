package sm0keysa1m0n.network.pipeline;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import sm0keysa1m0n.network.message.Message;
import sm0keysa1m0n.network.message.MessageIndex;
import sm0keysa1m0n.network.util.ByteBufUtil;
import sm0keysa1m0n.network.wrapper.NetworkManager;

public class NettyMessageEncoder extends MessageToByteEncoder<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NettyMessageEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		MessageIndex index = ctx.pipeline().get(NetworkManager.class).getMessageIndex();
		if (index == null) {
			throw new IllegalStateException("No message index has been set for the channel");
		} else {
			Integer discriminator = index.getDiscriminator(msg.getClass());
			if (discriminator == null) {
				throw new IOException("Can't serialize unregistered message");
			} else {
				ByteBufUtil.writeVarInt(out, discriminator.intValue());
				msg.toBytes(out);
				LOGGER.debug("MESSAGE OUT: [{}:{}] {}", index, discriminator, msg.getClass().getName());
			}
		}
	}

}