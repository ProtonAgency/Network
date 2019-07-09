package uk.co.tarbits.network.example.echo;

import lombok.Data;
import uk.co.tarbits.network.protocol.IMessage;

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
