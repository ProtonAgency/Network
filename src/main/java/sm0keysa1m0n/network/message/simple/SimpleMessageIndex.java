package sm0keysa1m0n.network.message.simple;

import java.util.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;

import sm0keysa1m0n.network.message.IMessage;
import sm0keysa1m0n.network.message.IMessageIndex;

public class SimpleMessageIndex implements IMessageIndex {

	private final BiMap<Integer, Class<? extends IMessage>> messages = HashBiMap.create();

	public <MSG extends IMessage> void registerMessage(Integer discriminator, Class<MSG> msg) {
		if (this.messages.containsKey(discriminator))
			throw new RuntimeException("A message with the discriminator " + discriminator + " already exists");
		else
			this.messages.put(discriminator, msg);
	}

	public Class<? extends IMessage> unregisterMessage(Integer discriminator) {
		return this.messages.remove(discriminator);
	}

	public List<Class<? extends IMessage>> getMessages() {
		return ImmutableList.copyOf(this.messages.values());
	}

	@Override
	public Integer getDiscriminator(Class<? extends IMessage> msg) {
		return this.messages.inverse().get(msg);
	}

	@Override
	public Class<? extends IMessage> getMessage(Integer discriminator) {
		return this.messages.get(discriminator);
	}

}
