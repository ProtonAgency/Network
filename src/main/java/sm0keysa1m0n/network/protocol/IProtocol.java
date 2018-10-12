package sm0keysa1m0n.network.protocol;

import sm0keysa1m0n.network.NetworkManager;
import sm0keysa1m0n.network.message.IMessageIndex;
import sm0keysa1m0n.network.message.IMessageListener;

public interface IProtocol {

	IMessageIndex getMessageIndex();

	IMessageListener getMessageListener();

	ISession newSession(NetworkManager networkManager);

}
