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
	private String hostResourcePath;

	public static final String RESOURCE_FILE = "hosts.properties";
	public static final String URL_IDENT = "HOST_URL";
	public static final String PORT_IDENT = "HOST_PORT";
	public static final String RESOURCE_PATH_IDENT = "HOST_RESOURCE_PATH";

	private HostConfiguration() {
		try {
			loadConfiguration();
		} catch (IOException e) {
			LOGGER.error("Host configuration couldn't be loaded", e);
			LOGGER.warn("Using default host configuration: localhost:8080/NoteBookService/notebook/notebook/");
			hostUrl = "localhost";
			hostPort = 8080;
			hostResourcePath = "NoteBookService/notebook/notebook/";
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
		String hostPortString = null;
		try (InputStream resourceStream = loader.getResourceAsStream(RESOURCE_FILE)) {
			if (resourceStream != null) {
				urlProperties.load(resourceStream);

				hostUrl = urlProperties.getProperty(URL_IDENT);
				hostPortString = urlProperties.getProperty(PORT_IDENT);
				hostResourcePath = urlProperties.getProperty(RESOURCE_PATH_IDENT);
			}
		}

		if (hostUrl == null || hostUrl.equals("") || hostPortString == null || hostPortString.equals("")
				|| hostResourcePath == null || hostResourcePath.equals("")) {
			throw new IOException("No host configuration could be loaded from properties.");
		}
		try {
			hostPort = Integer.parseInt(hostPortString);
		} catch (NumberFormatException nfe) {
			throw new IOException("No host port could be loaded (port couldn't be parsed as int)", nfe);
		}
		LOGGER.info("Configuration loaded: host: " + hostUrl + " port: " + hostPort + " resource path: "
				+ hostResourcePath);
	}

	public String getHostUrlWithPort() {
		String urlWithPort = "http://" + hostUrl + ":" + hostPort;
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

	public String getHostResourcePath() {
		return hostResourcePath;
	}

	public void setHostResourcePath(String hostResourcePath) {
		this.hostResourcePath = hostResourcePath;
	}
}