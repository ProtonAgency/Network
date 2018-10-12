package sm0keysa1m0n.network.message.simple;

import sm0keysa1m0n.network.message.IMessage;

public interface IMessageHandler<REQ extends IMessage, REPLY extends IMessage, CTX> {

	REPLY processMessage(REQ msg, CTX ctx);

}
