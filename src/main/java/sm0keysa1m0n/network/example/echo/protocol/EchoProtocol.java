package sm0keysa1m0n.network.example.echo.protocol;

import sm0keysa1m0n.network.ByteBufUtil;
import sm0keysa1m0n.network.example.echo.message.echo.EchoMessage;
import sm0keysa1m0n.network.pipeline.Session;
import sm0keysa1m0n.network.protocol.SimpleProtocol;

/**
 * A simple protocol that registers the {@link HelloMessage}.
 * 
 * @author Joseph Tarbit
 *
 */
public class EchoProtocol extends SimpleProtocol<Session> {

	public EchoProtocol() {
		this.registerMessage(0x00, EchoMessage.class, (msg, out) -> {
			ByteBufUtil.writeUTF8(out, msg.getText());
		}, (in) -> {
			return new EchoMessage(ByteBufUtil.readUTF8(in));
		}, (msg, session) -> {
			System.out.println(
					"NEW MESSAGE FROM " + session.getChannel().remoteAddress().toString() + ": " + msg.getText());
			session.getChannel().close();
		});
	}

}
