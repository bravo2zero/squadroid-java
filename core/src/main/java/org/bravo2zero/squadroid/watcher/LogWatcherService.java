package org.bravo2zero.squadroid.watcher;

import java.io.IOException;

public interface LogWatcherService {
	void addLog(String filePath, LogEventHandler dispatcher) throws IOException;

	void start();

	void stop();

	void setWatcherCheckInterval(long milliseconds);
}
