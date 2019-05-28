package sm0keysa1m0n.network.pipeline;

import java.util.function.BiConsumer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import sm0keysa1m0n.network.protocol.IMessage;
import sm0keysa1m0n.network.protocol.IProtocol;

/**
 * A {@link SimpleChannelInboundHandler} used to represent and handle a
 * connection.
 * 
 * @author Joseph Tarbit
 *
 */
public class Session extends SimpleChannelInboundHandler<IMessage> {

	@Getter
	protected final Channel channel;

	@Getter
	protected IProtocol<?> protocol;

	/**
	 * Constructs a new {@link Session}.
	 * 
	 * @param channel  - the associated {@link Channel}
	 * @param protocol - the {@link IProtocol} used to encode, decode and handle
	 *                 {@link IMessages}
	 */
	public Session(Channel channel, IProtocol<?> protocol) {
		this.channel = channel;
		this.protocol = protocol;
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

	@SuppressWarnings("unchecked")
	@Override
	protected final void channelRead0(ChannelHandlerContext ctx, IMessage msg) throws Exception {
		this.protocol.getMessageEntry(msg.getClass()).getMessageConsumer().ifPresent((consumer) -> {
			((BiConsumer<IMessage, Session>) consumer).accept(msg, this);
		});
	}
}
