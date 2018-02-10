package org.bravo2zero.squadroid.services;

import org.bravo2zero.squadroid.Config;
import org.bravo2zero.squadroid.ServiceConfig;
import org.bravo2zero.squadroid.ServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

abstract public class Service {

	protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	Optional<ServiceConfig> serviceConfig;
	ServiceListener listener;

	abstract public String getName();

	abstract public void init() throws ServiceException;

	abstract public String getInfo();

	abstract public void notify(ServiceCommand command);

	public Service(Config config, ServiceListener listener) {
		this.serviceConfig = config.getServices().stream()
				.filter(sc -> getName().equals(sc.getServiceName()))
				.findFirst();
		this.listener = listener;
	}

	protected ServiceConfig getServiceConfig() throws ServiceException {
		return serviceConfig.orElseThrow(() -> new ServiceException("Cannot find config for service [" + getName() + "]"));
	}

	protected String getSetting(String settingName) throws ServiceException {
		return getSetting(settingName, String.class);
	}

	protected <T> T getSetting(String settingName, Class<T> settingType) throws ServiceException {
		if (settingType == String.class) {
			return (T) getServiceConfig().getSettings().get(settingName);
		}
		if (settingType == Integer.class) {
			return (T) Integer.valueOf(getServiceConfig().getSettings().get(settingName));
		}
		throw new ServiceException("Cannot get setting: " + settingName + ". Unsupported setting type: " + settingType);
	}

	protected void send(String message) {
		try {
			listener.serviceEvent(message);
		} catch (Exception e) {
			LOGGER.warn("Exception notifying listener", e);
		}
	}

	protected String getHelp
			() {
		return "\nService: " + getName() + "\n\n" +
				getInfo();
	}

}
