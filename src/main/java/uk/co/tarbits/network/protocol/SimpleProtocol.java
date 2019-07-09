package uk.co.tarbits.network.protocol;

import java.util.Optional;
import java.util.function.BiConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import uk.co.tarbits.network.exception.UnknownMessageException;
import uk.co.tarbits.network.pipeline.Session;
import uk.co.tarbits.network.protocol.IProtocol.MessageEntry.IDecoder;
import uk.co.tarbits.network.protocol.IProtocol.MessageEntry.IEncoder;

/**
 * A simple implementation of {@link IProtocol} that uses fastutil to map objects.
 * 
 * @author Joseph Tarbit
 *
 * @param <S> - the {@link Session} to cast message consumers to <b>(unsafe)</b>
 */
public class SimpleProtocol<S extends Session> implements IProtocol<S> {

  private final Int2ObjectArrayMap<MessageEntry<?, S>> indicies = new Int2ObjectArrayMap<>();
  private final Object2ObjectArrayMap<Class<?>, MessageEntry<?, S>> types =
      new Object2ObjectArrayMap<>();

  public <M extends IMessage> IProtocol<S> registerMessage(int index, Class<M> messageType,
      IEncoder<M> encoder, IDecoder<M> decoder, BiConsumer<M, S> messageConsumer) {
    MessageEntry<M, S> handler =
        new MessageEntry<>(index, encoder, decoder, Optional.ofNullable(messageConsumer));
    this.indicies.put((short) (index & 0xff), handler);
    this.types.put(messageType, handler);
    return this;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <M extends IMessage> MessageEntry<M, S> getMessageEntry(Class<M> messageType)
      throws UnknownMessageException {
    MessageEntry<M, S> messageEntry = (MessageEntry<M, S>) this.types.get(messageType);
    if (messageEntry != null) {
      return messageEntry;
    } else {
      throw new UnknownMessageException("Unknown message type: " + messageType.getName());
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <M extends IMessage> MessageEntry<M, S> getMessageEntry(int index)
      throws UnknownMessageException {
    MessageEntry<M, S> messageEntry = (MessageEntry<M, S>) this.indicies.get(index);
    if (messageEntry != null) {
      return messageEntry;
    } else {
      throw new UnknownMessageException("Unknown message index: " + index);
    }
  }
}
