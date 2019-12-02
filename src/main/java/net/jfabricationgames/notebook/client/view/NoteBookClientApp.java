package net.jfabricationgames.notebook.client.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NoteBookClientApp extends Application {
	
	private static final Logger LOGGER = LogManager.getLogger(NoteBookClientApp.class);
	
	private NoteBookClientController controller;
	
	public static final String APPLICATION_NAME = "NoteBook Client";

	private static final String propertyStyleCss = "style_css";
	
	private static final String propertiesFile = "./notebookclient.view.properties";
	private Properties styleProperties;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		//load the css style
		loadStyleConfiguration();
		try {
			LOGGER.info("------------------------------------------------------- Starting Application -------------------------------------------------------");
			URL fxmlUrl = getClass().getResource("NoteBookClient.fxml");
			FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
			controller = new NoteBookClientController();
			fxmlLoader.setController(controller);
			Parent root = fxmlLoader.load();
			Scene scene = new Scene(root, 1000, 600);
			//add css style
			String styleResourceFile = styleProperties.getProperty(propertyStyleCss, "style_default.css");
			String styleResource = getClass().getClassLoader().getResource(styleResourceFile).toExternalForm();
			scene.getStylesheets().add(styleResource);
			primaryStage.setScene(scene);
			primaryStage.setTitle(APPLICATION_NAME);
			primaryStage.show();
		}
		catch (Exception e) {
			LOGGER.error("Error while running the window ", e);
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void loadStyleConfiguration() {
		LOGGER.info("loading configuration file: {}", propertiesFile);
		styleProperties = new Properties();
		try (InputStream input = new FileInputStream(propertiesFile)) {
			styleProperties.load(input);
		}
		catch (FileNotFoundException fnfe) {
			//set default values and store the file
			LOGGER.info("no configuration file found ({}); creating default", propertiesFile);
			styleProperties.setProperty(propertyStyleCss, "style_default.css");
			try (OutputStream output = new FileOutputStream(propertiesFile)) {
				styleProperties.store(output, null);
			}
			catch (IOException ioe) {
				LOGGER.error("Unexpected IOException while trying to store default properties");
			}
		}
		catch (IOException ioe) {
			LOGGER.error("Unexpected IOException while loading properties", ioe);
		}
	}
}