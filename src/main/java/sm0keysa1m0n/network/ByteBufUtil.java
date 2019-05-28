
package sm0keysa1m0n.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import io.netty.buffer.ByteBuf;

/**
 * A class containing various utility methods that act on {@link ByteBuf}s.
 */
public class ByteBufUtil {

	/**
	 * Reads a {@link String} from a {@link ByteBuf}.
	 *
	 * @param buf - the {@link ByteBuf} to read from
	 * @return the {@link String}
	 * @throws IOException if reading fails
	 */
	public static String readUTF8(ByteBuf buf) throws IOException {
		// Read the string's length
		final int len = readVarInt(buf);
		final byte[] bytes = new byte[len];
		buf.readBytes(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	/**
	 * Writes a {@link String} into a {@link ByteBuf}.
	 *
	 * @param buf   - the {@link ByteBuf} to write to
	 * @param value - the {@link String} to write
	 * @throws IOException if writing fails
	 */
	public static void writeUTF8(ByteBuf buf, String value) throws IOException {
		final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		if (bytes.length >= Short.MAX_VALUE) {
			throw new IOException("Attempt to write a string with a length greater than Short.MAX_VALUE to ByteBuf!");
		}
		// Write the string's length
		writeVarInt(buf, bytes.length);
		buf.writeBytes(bytes);
	}

	/**
	 * Reads an {@link int} written into a {@link ByteBuf} as one of various bit
	 * sizes.
	 *
	 * @param buf - the {@link ByteBuf} to read from
	 * @return the {@link int}
	 * @throws IOException if reading fails
	 */
	public static int readVarInt(ByteBuf buf) throws IOException {
		int out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = buf.readByte();
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 5) {
				throw new IOException("Attempt to read int bigger than allowed for a varint!");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;
	}

	/**
	 * Writes an {@link int} into a {@link ByteBuf} using the least possible amount
	 * of bits.
	 *
	 * @param buf   - the {@link ByteBuf} to write to
	 * @param value - the {@link int} to write
	 */
	public static void writeVarInt(ByteBuf buf, int value) {
		byte part;
		while (true) {
			part = (byte) (value & 0x7F);
			value >>>= 7;
			if (value != 0) {
				part |= 0x80;
			}
			buf.writeByte(part);
			if (value == 0) {
				break;
			}
		}
	}

	/**
	 * Reads a {@link long} written into a {@link ByteBuf} as one of various bit
	 * sizes.
	 *
	 * @param buf - the {@link ByteBuf} to read from
	 * @return the {@link long}
	 * @throws IOException if reading fails
	 */
	public static long readVarLong(ByteBuf buf) throws IOException {
		long out = 0;
		int bytes = 0;
		byte in;
		while (true) {
			in = buf.readByte();
			out |= (in & 0x7F) << (bytes++ * 7);
			if (bytes > 10) {
				throw new IOException("Attempt to read long bigger than allowed for a varlong!");
			}
			if ((in & 0x80) != 0x80) {
				break;
			}
		}
		return out;
	}

	/**
	 * Writes a {@link long} into a {@link ByteBuf} using the least possible amount
	 * of bits.
	 *
	 * @param buf   - the {@link ByteBuf} to write to
	 * @param value - the {@link long} to write
	 */
	public static void writeVarLong(ByteBuf buf, long value) {
		byte part;
		while (true) {
			part = (byte) (value & 0x7F);
			value >>>= 7;
			if (value != 0) {
				part |= 0x80;
			}
			buf.writeByte(part);
			if (value == 0) {
				break;
			}
		}
	}

	/**
	 * Writes a {@link UUID} into a {@link ByteBuf}.
	 * 
	 * @param buf  - the {@link ByteBuf} to write to
	 * @param uuid - the {@link UUID} to write
	 */
	public static void writeUUID(ByteBuf buf, UUID uuid) {
		byte[] bytes = getBytesFromUUID(uuid);
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}

	/**
	 * Reads a {@link UUID} from a {@link ByteBuf}.
	 * 
	 * @param buf - the {@link ByteBuf} to read from
	 * @return the {@link UUID}
	 */
	public static UUID readUUID(ByteBuf buf) {
		int length = buf.readInt();
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);
		return getUUIDFromBytes(bytes);
	}

	/**
	 * Writes a {@link String[]} to a {@link ByteBuf}.
	 * 
	 * @param buf   - the {@link ByteBuf} to write to
	 * @param array - the {@link String[]} to write
	 * @throws IOException if writing fails
	 */
	public static void writeStringArray(ByteBuf buf, String[] array) throws IOException {
		buf.writeInt(array.length);
		for (String item : array) {
			writeUTF8(buf, item);
		}
	}

	/**
	 * Reads a {@link String[]} from a {@link ByteBuf}.
	 * 
	 * @param buf - the {@link ByteBuf} to read from
	 * @return the {@link String[]}
	 * @throws IOException if reading fails
	 */
	public static String[] readStringArray(ByteBuf buf) throws IOException {
		int length = buf.readInt();
		String[] array = new String[length];
		for (int i = 0; i < length; i++) {
			array[i] = readUTF8(buf);
		}
		return array;
	}

	/**
	 * Writes an {@link int[]} into a {@link ByteBuf}.
	 * 
	 * @param buf   - the {@link ByteBuf} to write to
	 * @param array - the {@link int[]} to write
	 * @throws IOException if writing fails
	 */
	public static void writeIntegerArray(ByteBuf buf, int[] array) throws IOException {
		buf.writeInt(array.length);
		for (int item : array) {
			buf.writeInt(item);
		}
	}

	/**
	 * Reads an {@link int[]} from a {@link ByteBuf}.
	 * 
	 * @param buf - the {@link ByteBuf} to read from
	 * @return the {@link int[]}
	 * @throws IOException if reading fails
	 */
	public static int[] readIntegerArray(ByteBuf buf) throws IOException {
		int length = buf.readInt();
		int[] array = new int[length];
		for (int i = 0; i < length; i++) {
			array[i] = buf.readInt();
		}
		return array;
	}

	/**
	 * Writes a {@link UUID[]} into a {@link ByteBuf}.
	 * 
	 * @param buf   - the {@link ByteBuf} to write to
	 * @param array - the {@link UUID[]} to write
	 * @throws IOException if writing fails
	 */
	public static void writeUUIDArray(ByteBuf buf, UUID[] array) throws IOException {
		buf.writeInt(array.length);
		for (UUID item : array) {
			writeUUID(buf, item);
		}
	}

	/**
	 * Reads a {@link UUID[]} from a {@link ByteBuf}.
	 * 
	 * @param buf - the {@link ByteBuf} to read from
	 * @return the {@link UUID[]}
	 * @throws IOException if reading fails
	 */
	public static UUID[] readUUIDArray(ByteBuf buf) throws IOException {
		int length = buf.readInt();
		UUID[] array = new UUID[length];
		for (int i = 0; i < length; i++) {
			array[i] = readUUID(buf);
		}
		return array;
	}

	/**
	 * Converts a {@link UUID} into a {@link byte[]}.
	 * 
	 * @param uuid - the {@link UUID} to convert
	 * @return the {@link byte[]}
	 */
	public static byte[] getBytesFromUUID(UUID uuid) {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}

	/**
	 * Converts a {@link byte[]} into a {@link UUID}.
	 * 
	 * @param bytes - the {@link byte[]} to read from
	 * @return the {@link UUID}
	 */
	public static UUID getUUIDFromBytes(byte[] bytes) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		Long high = byteBuffer.getLong();
		Long low = byteBuffer.getLong();
		return new UUID(high, low);
	}

	/**
	 * Calculates the number of bytes required to fit the supplied {@link int} (0-5)
	 * if it were to be read/written using {@link #readVarInt(ByteBuf)} or
	 * {@link #writeVarInt(ByteBuf, int)}.
	 * 
	 * @param input - the {@link int} to calculate the var int size for
	 * @return the size
	 */
	public static int getVarIntSize(int input) {
		for (int i = 1; i < 5; ++i) {
			if ((input & -1 << i * 7) == 0) {
				return i;
			}
		}
		return 5;
	}
}