package uk.co.tarbits.network.protocol;

import java.util.Optional;
import java.util.function.BiConsumer;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NonNull;
import uk.co.tarbits.network.exception.UnknownMessageException;
import uk.co.tarbits.network.pipeline.Session;

/**
 * A registry that holds {@link MessageEntry}s; these can be used to encode, decode and handle
 * {@link IMessage}s.
 * 
 * @author Joseph Tarbit
 *
 * @param <S> - the {@link Session} to cast message consumers to <b>(unsafe)</b>
 */
public interface IProtocol<S extends Session> {

  /**
   * Gets a {@link MessageEntry} by its {@link IMessage} type.
   * 
   * @param <M> - the {@link IMessage} type
   * @param messageType - the {@link Class}
   * @return the {@link MessageEntry}
   * @throws UnknownMessageException if no message of that type exists
   */
  @NonNull
  <M extends IMessage> MessageEntry<M, S> getMessageEntry(Class<M> messageType)
      throws UnknownMessageException;

  /**
   * Gets a {@link MessageEntry} by its index.
   * 
   * @param <M> - the {@link IMessage} type
   * @param index - the index
   * @return the {@link MessageEntry}
   * @throws UnknownMessageException if no message with that index exists
   */
  @NonNull
  <M extends IMessage> MessageEntry<M, S> getMessageEntry(int index) throws UnknownMessageException;

  /**
   * A simple data {@link class} that holds essential information for an {@link IMessage}.
   * 
   * @author Joseph Tarbit
   *
   * @param <M> - the {@link IMessage} type
   * @param <S> - the {@link Session} to cast message consumers to <b>(unsafe)</b>
   */
  @Data
  class MessageEntry<M extends IMessage, S> {

    private final int index;
    private final IEncoder<M> encoder;
    private final IDecoder<M> decoder;
    private final Optional<BiConsumer<M, S>> messageConsumer;

    /**
     * Encodes messages of the specified type.
     * 
     * @author Joseph Tarbit
     *
     * @param <M> - the {@link IMessage} type
     */
    @FunctionalInterface
    public interface IEncoder<M extends IMessage> {

      void encode(M msg, ByteBuf out) throws Exception;
    }

    /**
     * Decodes messages of the specified type.
     * 
     * @author Joseph Tarbit
     *
     * @param <M> - the {@link IMessage} type
     */
    @FunctionalInterface
    public interface IDecoder<M extends IMessage> {

      M decode(ByteBuf in) throws Exception;
    }
  }
}
