package sm0keysa1m0n.network.example.echo.message.echo;

import lombok.Data;
import sm0keysa1m0n.network.protocol.IMessage;

/**
 * A {@link IMessage} that holds a {@link String}.
 * 
 * @author Joseph Tarbit
 *
 */
@Data
public class EchoMessage implements IMessage {

	private final String text;

}
