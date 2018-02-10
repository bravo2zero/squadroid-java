package org.bravo2zero.squadroid;

import java.util.HashMap;
import java.util.Map;

public class ServiceConfig {

	private String serviceName;
	private Map<String,String> settings;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Map<String, String> getSettings() {
		if(settings==null){
			settings = new HashMap<>();
		}
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}
}
