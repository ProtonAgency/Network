package sm0keysa1m0n.network.message;

import sm0keysa1m0n.network.wrapper.Session;

public interface MessageHandler<REQ extends Message, REPLY extends Message> {

	REPLY processMessage(REQ msg, Session session);
	
}
