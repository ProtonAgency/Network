package sm0keysa1m0n.network.wrapper;

public interface Session {

	default void sessionActive(NetworkManager networkManager) throws Exception {
		;
	}

	default void sessionInactive(NetworkManager networkManager) throws Exception {
		;
	}

	void exceptionCaught(NetworkManager networkManager, Throwable t) throws Exception;

}
