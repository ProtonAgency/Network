package sm0keysa1m0n.network.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import sm0keysa1m0n.network.ByteBufUtil;
import sm0keysa1m0n.network.protocol.IMessage;
import sm0keysa1m0n.network.protocol.IProtocol;
import sm0keysa1m0n.network.protocol.IProtocol.MessageEntry;

/**
 * A {@link MessageToByteEncoder} which encodes {@link IMessage}s into
 * {@link ByteBuf}s.
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
			MessageEntry<IMessage, ?> messageEntry = (MessageEntry<IMessage, ?>) protocol
					.getMessageEntry(msg.getClass());
			ByteBufUtil.writeVarInt(out, messageEntry.getIndex());
			messageEntry.getEncoder().encode(msg, out);
		}
	}
}