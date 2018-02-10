package com.bravo2zero.squadroid;

import org.bravo2zero.squadroid.Application;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Ignore
public class ApplicationTest {
	public static final Logger LOGGER = LoggerFactory.getLogger(ApplicationTest.class);

	@Test
	public void testApp() throws Exception {
		try {
			// will require valid bot token and existing channel configured
			Application app = new Application("./src/test/resources/squadroid-config.json");
			Thread.sleep(1000 * 60 * 5);
		} catch (Exception e) {
			LOGGER.info("got exception", e);
		}
	}
}
