package sm0keysa1m0n.network.protocol;

import java.util.Optional;
import java.util.function.BiConsumer;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.NonNull;
import sm0keysa1m0n.network.exception.UnknownMessageException;
import sm0keysa1m0n.network.pipeline.Session;

/**
 * A registry that holds {@link MessageEntry}s; these can be used to encode,
 * decode and handle {@link IMessage}s.
 * 
 * @author Joseph Tarbit
 *
 * @param <S> - the {@link Session} to cast message consumers to <b>(unsafe)</b>
 */
public interface IProtocol<S extends Session> {

	/**
	 * Gets a {@link MessageEntry} by its {@link IMessage} type.
	 * 
	 * @param <MSG>       - the {@link IMessage} type
	 * @param messageType - the {@link Class}
	 * @return the {@link MessageEntry}
	 * @throws UnknownMessageException if no message of that type exists
	 */
	@NonNull
	<MSG extends IMessage> MessageEntry<MSG, S> getMessageEntry(Class<MSG> messageType) throws UnknownMessageException;

	/**
	 * Gets a {@link MessageEntry} by its index.
	 * 
	 * @param <MSG> - the {@link IMessage} type
	 * @param index - the index
	 * @return the {@link MessageEntry}
	 * @throws UnknownMessageException if no message with that index exists
	 */
	@NonNull
	<MSG extends IMessage> MessageEntry<MSG, S> getMessageEntry(int index) throws UnknownMessageException;

	/**
	 * A simple data {@link class} that holds essential information for an
	 * {@link IMessage}.
	 * 
	 * @author Joseph Tarbit
	 *
	 * @param <MSG> - the {@link IMessage} type
	 * @param <S>   - the {@link Session} to cast message consumers to
	 *              <b>(unsafe)</b>
	 */
	@Data
	class MessageEntry<MSG extends IMessage, S> {

		private final int index;
		private final IEncoder<MSG> encoder;
		private final IDecoder<MSG> decoder;
		private final Optional<BiConsumer<MSG, S>> messageConsumer;

		/**
		 * Encodes messages of the specified type.
		 * 
		 * @author Joseph Tarbit
		 *
		 * @param <MSG> - the {@link IMessage} type
		 */
		@FunctionalInterface
		public interface IEncoder<MSG extends IMessage> {

			void encode(MSG msg, ByteBuf out) throws Exception;

		}

		/**
		 * Decodes messages of the specified type.
		 * 
		 * @author Joseph Tarbit
		 *
		 * @param <MSG> - the {@link IMessage} type
		 */
		@FunctionalInterface
		public interface IDecoder<MSG extends IMessage> {

			MSG decode(ByteBuf in) throws Exception;

		}
	}
}
