package org.bravo2zero.squadroid;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Application {

	public static final Logger LOGGER = LoggerFactory.getLogger(Application.class);
	public static final String CONFIG_FILE = "squadroid-config.json";

	private Squadroid droid;

	public Application(String configPath) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Config config = mapper.readValue(new File(configPath), Config.class);
		droid = new Squadroid(config);
	}

	public static void main(String[] args) {
		try {
			Application app = new Application(CONFIG_FILE);
			while (true) {
				// TODO add proper shutdown when asked from Discord?
				Thread.sleep(1000 * 60);
			}
		} catch (Exception e) {
			LOGGER.error("Exception initializing application", e);
		}
	}

}
