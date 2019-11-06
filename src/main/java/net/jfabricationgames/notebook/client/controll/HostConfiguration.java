package net.jfabricationgames.notebook.client.controll;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HostConfiguration {
	
	private static final Logger LOGGER = LogManager.getLogger(HostConfiguration.class);
	
	private static HostConfiguration instance;
	
	private String hostUrl;
	private int hostPort;
	
	public static final String RESOURCE_FILE = "";
	public static final String URL_IDENT = "HOST_URL";
	public static final String PORT_IDENT = "HOST_PORT";
	
	private HostConfiguration() {
		try {
			loadConfiguration();
		}
		catch (IOException e) {
			LOGGER.error("Host configuration couldn't be loaded", e);
			LOGGER.warn("Using default host configuration: localhost:8080");
			hostUrl = "localhost";
			hostPort = 8080;
		}
	}
	
	public static synchronized HostConfiguration getInstance() {
		if (instance == null) {
			instance = new HostConfiguration();
		}
		return instance;
	}
	
	private void loadConfiguration() throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties urlProperties = new Properties();
		try (InputStream resourceStream = loader.getResourceAsStream(RESOURCE_FILE)) {
			urlProperties.load(resourceStream);
		}
		hostUrl = urlProperties.getProperty(URL_IDENT);
		String hostPortString = urlProperties.getProperty(PORT_IDENT);
		
		if (hostUrl == null || hostUrl.equals("") || hostPortString == null || hostPortString.equals("")) {
			throw new IOException("No host configuration could be loaded from properties.");
		}
		try {
			hostPort = Integer.parseInt(hostPortString);
		}
		catch (NumberFormatException nfe) {
			throw new IOException("No host port could be loaded (port couldn't be parsed as int)", nfe);
		}
		LOGGER.info("Configuration loaded: host: " + hostUrl + " port: " + hostPort);
	}
	
	public String getHostUrlWithPort() {
		String urlWithPort = hostUrl + ":" + hostPort;
		return urlWithPort;
	}
	
	public String getHostUrl() {
		return hostUrl;
	}
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
	
	public int getHostPort() {
		return hostPort;
	}
	public void setHostPort(int hostPort) {
		this.hostPort = hostPort;
	}
}