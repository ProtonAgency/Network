package sm0keysa1m0n.network.message;

public interface MessageIndex {

	Integer getDiscriminator(Class<? extends Message> msg);

	Class<? extends Message> getMessage(Integer discriminator);

	<MSG extends Message> MessageHandler<MSG, ?> getMessageHandler(Class<MSG> msg);

}
