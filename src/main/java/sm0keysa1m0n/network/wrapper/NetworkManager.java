package sm0keysa1m0n.network.wrapper;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import sm0keysa1m0n.network.message.Message;
import sm0keysa1m0n.network.message.MessageHandler;
import sm0keysa1m0n.network.message.MessageIndex;

public class NetworkManager extends SimpleChannelInboundHandler<Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(NetworkManager.class);

	private final Supplier<? extends Session> sessionSupplier;

	private Channel channel;

	private MessageIndex messageIndex;

	private Session session;

	public NetworkManager(Supplier<? extends Session> sessionSupplier) {
		this.sessionSupplier = sessionSupplier;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		this.channel = ctx.channel();
		this.session = sessionSupplier.get();
		this.session.sessionActive(this);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
		@SuppressWarnings("unchecked")
		MessageHandler<Message, ?> messageHandler = (MessageHandler<Message, ?>) this.messageIndex
				.getMessageHandler(msg.getClass());
		if (messageHandler != null) {
			Message reply = messageHandler.processMessage(msg, this.session);
			if (reply != null)
				this.sendMessage(reply);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		this.session.sessionInactive(this);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) throws Exception {
		this.session.exceptionCaught(this, t);
	}

	public MessageIndex getMessageIndex() {
		return this.messageIndex;
	}

	public NetworkManager setMessageIndex(MessageIndex messageIndex) {
		this.messageIndex = messageIndex;
		return this;
	}

	public Session getSession() {
		return this.session;
	}

	public void sendMessage(Message msg) {
		this.channel.writeAndFlush(msg);
	}

	public NetworkManager close() {
		try {
			this.channel.close().sync();
		} catch (InterruptedException e) {
			LOGGER.error("Interrupted whilst closing channel");
		}
		return this;
	}
}
