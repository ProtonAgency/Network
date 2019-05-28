package sm0keysa1m0n.network.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Thrown when a message is not registered
 * 
 * @author Joseph Tarbit
 *
 */
@RequiredArgsConstructor
@Getter
public class UnknownMessageException extends Exception {

	private static final long serialVersionUID = -7549248059945284173L;

	public UnknownMessageException(String message) {
		super(message);
	}

}
