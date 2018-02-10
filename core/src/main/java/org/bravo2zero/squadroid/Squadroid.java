package org.bravo2zero.squadroid;

import org.bravo2zero.squadroid.services.Service;
import org.bravo2zero.squadroid.services.ServiceCommand;
import org.bravo2zero.squadroid.services.ServiceException;
import org.bravo2zero.squadroid.services.dummy.DummyService;
import org.bravo2zero.squadroid.services.rcon.RconService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Squadroid implements IListener<MessageReceivedEvent>, ServiceListener {

	public static final Logger LOGGER = LoggerFactory.getLogger(Squadroid.class);
	public static final int MESSAGE_LIMIT = 2000;
	private Config config;
	private IDiscordClient client;
	private IChannel workingChannel;
	private List<Service> services;

	public Squadroid(Config config) {
		this.config = config;

		// config client
		client = new ClientBuilder().withToken(config.getToken()).login();

		// register services
		services = new ArrayList<>();
		services.add(new DummyService(config, this));
		services.add(new RconService(config, this));

		// init services
		Iterator<Service> iterator = services.iterator();
		while (iterator.hasNext()) {
			try {
				Service service = iterator.next();
				LOGGER.info("Initializing service: {}", service.getName());
				service.init();
			} catch (ServiceException e) {
				LOGGER.warn("Exception initializing service", e);
				iterator.remove();
			}
		}

		// register listener
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(this);
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		try {
			IMessage message = event.getMessage();
			IChannel channel = message.getChannel();
			// notify only working channel messages with correct prefix
			if (getWorkingChannel().getLongID() == channel.getLongID() &&
					message.getContent().startsWith(config.getPrefix())) {
				services.forEach(service -> service.notify(new ServiceCommand(message)));
			}
		} catch (Exception e) {
			LOGGER.error("Exception handling event: " + event, e);
		}
	}

	@Override
	public void serviceEvent(String details) throws Exception {
		send(getWorkingChannel(), details);
	}

	/**
	 * Send message to channel, if content size is bigger than Discord's limit - split it into several messages
	 */
	private void send(IChannel target, String content) throws Exception {
		Arrays.stream(content.split("(?<=\\G.{" + MESSAGE_LIMIT + "})")).forEachOrdered(s ->
				new MessageBuilder(client).withChannel(target).withContent(content).send()
		);
	}

	private IChannel getWorkingChannel() {
		if (workingChannel == null) {
			workingChannel = client.getChannels().stream().filter(c -> {
				return config.getChannel() != null && config.getChannel().equals(c.getName());
			}).findFirst().orElseThrow(() -> new IllegalArgumentException("Cannot find working channel: " + config.getChannel()));
		}
		return workingChannel;
	}
}
