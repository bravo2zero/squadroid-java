package org.bravo2zero.squadroid.services.dummy;

import org.bravo2zero.squadroid.Config;
import org.bravo2zero.squadroid.ServiceListener;
import org.bravo2zero.squadroid.services.Service;
import org.bravo2zero.squadroid.services.ServiceCommand;
import org.bravo2zero.squadroid.services.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyService extends Service {

	public static final Logger LOGGER = LoggerFactory.getLogger(DummyService.class);

	public DummyService(Config config, ServiceListener listener) {
		super(config, listener);
	}

	@Override
	public String getName() {
		return "dummy";
	}

	@Override
	public String getInfo() {
		return "This is a simple dummy service, it will repeat commands.\n\n" +
				"Usage:\n\n" +
				"\t!dummy <arg1> <arg2>..<argN>";
	}

	@Override
	public void init() throws ServiceException {
		// do nothing
	}


	@Override
	public void notify(ServiceCommand command) {
		command.getType().ifPresent(s -> {
			if ("dummy".equalsIgnoreCase(s)) {

				send(String.format("Hi %s! You said: %s.",
						command.getMessage().getAuthor().getName(),
						command.getMessage().getContent()));

			}
			if ("help".equalsIgnoreCase(s)) {
				send(getHelp());
			}
		});
	}

}
