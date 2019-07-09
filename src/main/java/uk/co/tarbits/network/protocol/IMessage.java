package uk.co.tarbits.network.protocol;

/**
 * Implementers of this class represent the data of a message to be sent. There are a two rules that
 * {@link IMessage}s should follow:
 * <ol>
 * <li>All message fields should be immutable. This ensures thread-safety and makes it so
 * {@link IMessage} objects can be safely stored.</li>
 * <li>All fields in an {@link IMessage} should be protocol-primitive (can be written directly via
 * {@link ByteBuf} methods or via a *single* {@link ByteBufUtil} method).</li>
 * </ol>
 * 
 * @author Joseph Tarbit
 */
public interface IMessage {

}
