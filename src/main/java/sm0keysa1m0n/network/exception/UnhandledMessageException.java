package sm0keysa1m0n.network.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import sm0keysa1m0n.network.pipeline.Session;
import sm0keysa1m0n.network.protocol.IMessage;

/**
 * Thrown when no message consumer is found
 * 
 * @author Joseph Tarbit
 *
 */
@RequiredArgsConstructor
@Getter
public class UnhandledMessageException extends Exception {

	private static final long serialVersionUID = -7661305251257254588L;

	private final IMessage unhandledMessage;
	private final Session session;

}
