package org.bravo2zero.squadroid.watcher;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class LogWatcherServiceImplTest {

	public static final Logger LOGGER = LoggerFactory.getLogger(LogWatcherServiceImplTest.class);

	public static final String BASE_DIR = "target/test-files";
	public static final String FILENAME = "test.log";
	public static final String PATH = BASE_DIR + "/" + FILENAME;

	private LogWatcherService service;


	@Before
	public void setUp() throws Exception {
		Path baseDir = Paths.get(BASE_DIR);
		if (Files.exists(baseDir)) {
			Files.list(baseDir).forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					LOGGER.error("Exception cleaning base test dir", e);
				}
			});
		} else {
			Files.createDirectories(baseDir);
		}
		Files.createFile(Paths.get(PATH));
	}


	@Test
	public void testWatcher() throws Exception {

		Set<String> result = new HashSet<>();

		Path logFile = Paths.get(PATH);

		writeToFile(logFile, "test line 0");

		service = new LogWatcherServiceImpl();
		service.setWatcherCheckInterval(1000);
		service.addLog(PATH, new LogEventHandler() {
			@Override
			public void contentUpdatedEvent(String content) {
				result.add(content);
			}
		});
		service.start();

		writeToFile(logFile, "test line 1", "test line 2");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// do nothing
		}
		service.stop();

		assertEquals(new HashSet<>(Arrays.asList("test line 1", "test line 2")),
				result);
	}


	private void writeToFile(Path file, String... content) throws IOException {
		Files.write(file, Arrays.asList(content), StandardOpenOption.APPEND);
	}

}