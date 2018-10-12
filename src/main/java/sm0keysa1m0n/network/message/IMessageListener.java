package sm0keysa1m0n.network.message;

import sm0keysa1m0n.network.NetworkManager;

public interface IMessageListener {

	void messageReceived(IMessage msg, NetworkManager networkManager);

}
