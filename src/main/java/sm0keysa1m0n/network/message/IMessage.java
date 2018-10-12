package sm0keysa1m0n.network.message;

import io.netty.buffer.ByteBuf;

public interface IMessage {

	/**
	 * Serialise the {@link IMessage} into a {@link ByteBuf}
	 *
	 * @param out - the {@link ByteBuf} to write to
	 */
	void toBytes(ByteBuf out) throws Exception;

	/**
	 * Deserialise the {@link IMessage} from a {@link ByteBuf}
	 *
	 * @param in - the {@link ByteBuf} to read from
	 */
	void fromBytes(ByteBuf in) throws Exception;

}
