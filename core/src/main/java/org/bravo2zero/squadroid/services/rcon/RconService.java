package org.bravo2zero.squadroid.services.rcon;

import net.sourceforge.rconed.Rcon;
import org.bravo2zero.squadroid.Config;
import org.bravo2zero.squadroid.ServiceListener;
import org.bravo2zero.squadroid.services.Service;
import org.bravo2zero.squadroid.services.ServiceCommand;
import org.bravo2zero.squadroid.services.ServiceException;

public class RconService extends Service {

	public static final String SERVER_IP = "ip";
	public static final String PORT = "port";
	public static final String LOCAL_PORT = "localPort";
	public static final String PASSWORD = "password";

	public RconService(Config config, ServiceListener listener) {
		super(config, listener);
	}

	@Override
	public String getName() {
		return "rcon";
	}

	@Override
	public void init() throws ServiceException {
		// do nothing
	}

	@Override
	public String getInfo() {
		return "Service providing full RCON administration access.\n\n" +
				"Usage:\n\n"+
				"\t!rcon AdminBroadcast <Message>\n" +
				"\t!rcon ListPlayers\n" +
				"\t!rcon AdminKickById <PlayerId> <KickReason>\n" +
				"\t!rcon AdminBanById <PlayerId> \"<BanLength>\" <BanReason> (BanLength: 0 = Perm, 1d = 1 Day, 1M = 1 Month)\n" +
				"\n\nSquad Admin guide: https://squad.gamepedia.com/Server_Administration";
	}

	@Override
	public void notify(ServiceCommand command) {
		command.getType().ifPresent(type -> {
			if ("rcon".equalsIgnoreCase(type)) {
				command.getBody().ifPresent(body -> {
					try {
						String response = executeRconCommand(body);
						send(response);
					} catch (Exception e) {
						LOGGER.warn("Exception executing command: " + body, e);
						send("Cannot execute command: " + e.getMessage());
					}
				});
			}
			if ("help".equalsIgnoreCase(type)) {
				send(getHelp());
			}
		});
	}

	private String executeRconCommand(String command) throws Exception {
		return Rcon.send(
				getSetting(LOCAL_PORT, Integer.class),
				getSetting(SERVER_IP),
				getSetting(PORT, Integer.class),
				getSetting(PASSWORD),
				command
		);
	}
}
