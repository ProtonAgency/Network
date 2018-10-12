package sm0keysa1m0n.network.message;

public interface IMessageIndex {

	Integer getDiscriminator(Class<? extends IMessage> msg);

	Class<? extends IMessage> getMessage(Integer discriminator);

}
