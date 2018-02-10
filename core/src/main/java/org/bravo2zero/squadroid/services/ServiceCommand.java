package org.bravo2zero.squadroid.services;

import sx.blah.discord.handle.obj.IMessage;

import java.util.Arrays;
import java.util.Optional;

public class ServiceCommand {

	private IMessage message;
	private String[] splitted;

	public ServiceCommand(IMessage message) {
		this.message = message;
		this.splitted = message.getContent() == null ? new String[0] : message.getContent().substring(1).split(" ");
	}

	public IMessage getMessage() {
		return message;
	}

	public Optional<String> getType() {
		return splitted.length > 0 ? Optional.of(splitted[0]) : Optional.empty();
	}

	public Optional<String> getArgument(int index) {
		try {
			return Optional.of(splitted[index]);
		} catch (ArrayIndexOutOfBoundsException ioob) {
			return Optional.empty();
		}
	}

	public Optional<String> getBody() {
		if (splitted.length > 1) {
			String[] bodyParts = Arrays.copyOfRange(splitted, 1, splitted.length);
			return Optional.of(String.join(" ", Arrays.asList(bodyParts)));
		}
		return Optional.empty();
	}
}
