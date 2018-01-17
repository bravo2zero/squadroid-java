package org.bravo2zero.squadroid.watcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogWatcherServiceImpl implements LogWatcherService {

	public static final Logger LOGGER = LoggerFactory.getLogger(LogWatcherServiceImpl.class);

	Watcher watcher = new Watcher(30000);

	@Override
	public void addLog(String filePath, LogEventHandler eventHandler) throws IOException {
		if (watcher.getLogFiles().stream().noneMatch(log -> log.getFilePath().equals(filePath))) {
			watcher.getLogFiles().add(new LogFile(filePath, eventHandler));
		}
	}

	@Override
	public void start() {
		watcher.start();
	}

	@Override
	public void stop() {
		watcher.stopWatcher();
	}

	@Override
	public void setWatcherCheckInterval(long milliseconds){
		watcher.setInterval(milliseconds);
	}


	class Watcher extends Thread {

		private long interval;
		private List<LogFile> logFiles;
		private boolean running = true;

		public Watcher(long interval) {
			super("LogWatcher");
			this.interval = interval;
		}

		public void stopWatcher() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				try {
					getLogFiles().forEach(log -> {
						try {
							log.checkForChanges();
						} catch (Exception e) {
							LOGGER.error("Exception checking for changes", e);
						}
					});
					sleep(interval);
				} catch (InterruptedException e) {
					LOGGER.info("Interrupted main watcher loop");
				}
			}
		}


		public List<LogFile> getLogFiles() {
			if (logFiles == null) {
				logFiles = Collections.synchronizedList(new ArrayList<>());
			}
			return logFiles;
		}

		public void setInterval(long interval) {
			this.interval = interval;
		}
	}

	class LogFile {

		private String filePath;
		private LogEventHandler eventHandler;
		private RandomAccessFile file;

		public LogFile(String filePath, LogEventHandler eventHandler) throws IOException {
			this.filePath = filePath;
			this.eventHandler = eventHandler;
			this.file = getFile();
		}

		public void checkForChanges() throws IOException {
			String line;
			// TODO account for log file rotation
			while ((line = getFile().readLine()) != null) {
				eventHandler.contentUpdatedEvent(line);
			}
		}

		public RandomAccessFile getFile() throws IOException {
			if (file == null) {
				file = new RandomAccessFile(filePath, "r");
				file.seek(file.length());
			}
			return file;
		}

		public String getFilePath() {
			return filePath;
		}

		public LogEventHandler getEventHandler() {
			return eventHandler;
		}
	}
}
