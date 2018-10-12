package sm0keysa1m0n.network.message.simple;

import java.util.Map;

import com.google.common.collect.Maps;

import sm0keysa1m0n.network.NetworkManager;
import sm0keysa1m0n.network.message.IMessage;
import sm0keysa1m0n.network.message.IMessageListener;

public abstract class SimpleMessageListener<CTX> implements IMessageListener {

	private final Map<Class<? extends IMessage>, IMessageHandler<?, ?, CTX>> messageHandlers = Maps.newHashMap();

	public <MSG extends IMessage> void registerMessage(Class<MSG> msg, IMessageHandler<MSG, ?, CTX> messageHandler) {
		this.messageHandlers.put(msg, messageHandler);
	}

	public void unregisterMessage(Class<? extends IMessage> msg) {
		this.messageHandlers.remove(msg);
	}

	@Override
	public final void messageReceived(IMessage msg, NetworkManager networkManager) {
		@SuppressWarnings("unchecked")
		IMessageHandler<IMessage, ?, CTX> messageHandler = (IMessageHandler<IMessage, ?, CTX>) this.messageHandlers
				.get(msg.getClass());
		if (messageHandler != null)
			messageHandler.processMessage(msg, this.createMessageContext(networkManager));
	}

	protected abstract CTX createMessageContext(NetworkManager networkManager);
}