package sm0keysa1m0n.network.protocol;

import java.util.Optional;
import java.util.function.BiConsumer;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import sm0keysa1m0n.network.exception.UnknownMessageException;
import sm0keysa1m0n.network.pipeline.Session;
import sm0keysa1m0n.network.protocol.IProtocol.MessageEntry.IDecoder;
import sm0keysa1m0n.network.protocol.IProtocol.MessageEntry.IEncoder;

/**
 * A simple implementation of {@link IProtocol} that uses fastutil to map
 * objects.
 * 
 * @author Joseph Tarbit
 *
 * @param <S> - the {@link Session} to cast message consumers to <b>(unsafe)</b>
 */
public class SimpleProtocol<S extends Session> implements IProtocol<S> {

	private final Int2ObjectArrayMap<MessageEntry<?, S>> indicies = new Int2ObjectArrayMap<>();
	private final Object2ObjectArrayMap<Class<?>, MessageEntry<?, S>> types = new Object2ObjectArrayMap<>();

	public <MSG extends IMessage> IProtocol<S> registerMessage(int index, Class<MSG> messageType, IEncoder<MSG> encoder,
			IDecoder<MSG> decoder, BiConsumer<MSG, S> messageConsumer) {
		MessageEntry<MSG, S> handler = new MessageEntry<>(index, encoder, decoder,
				Optional.ofNullable(messageConsumer));
		this.indicies.put((short) (index & 0xff), handler);
		this.types.put(messageType, handler);
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <MSG extends IMessage> MessageEntry<MSG, S> getMessageEntry(Class<MSG> messageType)
			throws UnknownMessageException {
		MessageEntry<MSG, S> messageEntry = (MessageEntry<MSG, S>) this.types.get(messageType);
		if (messageEntry != null)
			return messageEntry;
		else
			throw new UnknownMessageException("Unknown message type: " + messageType.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <MSG extends IMessage> MessageEntry<MSG, S> getMessageEntry(int index) throws UnknownMessageException {
		MessageEntry<MSG, S> messageEntry = (MessageEntry<MSG, S>) this.indicies.get(index);
		if (messageEntry != null)
			return messageEntry;
		else
			throw new UnknownMessageException("Unknown message index: " + index);
	}
}