package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.gi.GraphicInterface;
import client.menu.Menu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger("Main");

	private static String JAR_NAME = null;

	private static String EVENT_SERVER_IP = null;
	private static int EVENT_SERVER_PORT = -1;

	private static double GLOBAL_WIDTH = -1;
	private static double GLOBAL_HEIGHT = -1;
	private static double MENU_WIDTH = -1;
	private static double GI_WIDTH = -1;
	private static double MENU_START_X = -1;

	public static void main(String[] args) throws IOException {

		if(LOGGER.isInfoEnabled()) {
			LOGGER.info("Starting jumanji-magic-tournament client");
		}

		loadConfiguration();

		launch(args);

	}

	public static void loadConfiguration() throws IOException {

		Properties configuration = new Properties();

		configuration.load(Main.class.getClassLoader().getResourceAsStream("configuration.properties"));

		JAR_NAME = configuration.getProperty("JAR_NAME");
		EVENT_SERVER_IP = configuration.getProperty("EVENT_SERVER_IP");
		EVENT_SERVER_PORT = Integer.parseInt(configuration.getProperty("EVENT_SERVER_PORT"));
		GLOBAL_WIDTH = Double.parseDouble(configuration.getProperty("GLOBAL_WIDTH"));
		GLOBAL_HEIGHT = Double.parseDouble(configuration.getProperty("GLOBAL_HEIGHT"));
		MENU_WIDTH = Double.parseDouble(configuration.getProperty("MENU_WIDTH"));
		GI_WIDTH = Double.parseDouble(configuration.getProperty("GI_WIDTH"));
		MENU_START_X = Double.parseDouble(configuration.getProperty("MENU_START_X"));
		
		LOGGER.info("Configuration: ");
		LOGGER.info("=> JAR_NAME: "+JAR_NAME);
		LOGGER.info("=> EVENT_SERVER_IP: "+EVENT_SERVER_IP);
		LOGGER.info("=> EVENT_SERVER_PORT: "+EVENT_SERVER_PORT);
		LOGGER.info("=> GLOBAL_WIDTH: "+GLOBAL_WIDTH);
		LOGGER.info("=> GLOBAL_HEIGHT: "+GLOBAL_HEIGHT);
		LOGGER.info("=> MENU_WIDTH: "+MENU_WIDTH);
		LOGGER.info("=> MENU_WIDTH: "+MENU_WIDTH);
		LOGGER.info("=> MENU_START_X: "+MENU_START_X);
		
	}

	private static String getResourceDirURL() {

		File file = new File(JAR_NAME);
		String finalUrl = null;

		try {
			finalUrl = new String(file.toURI().toURL().toString()+"!/content").replace("file:", "jar:file:");
		} catch(Exception e) {
			LOGGER.error("", e);
		}

		return finalUrl;
	}

	public static String getResourceUrl(String relativePath) {

		String imgUrl = getResourceDirURL() + relativePath;

		try {
			URI.create(imgUrl).toURL().openStream();
		} catch (Exception ex) {
			LOGGER.error("Unable to load resource", ex);
		}

		return imgUrl;
		
	}

	public static boolean existsInJar(String filepath) {
		return Main.class.getResource(filepath) != null;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		GraphicInterface gi = new GraphicInterface(GI_WIDTH, GLOBAL_HEIGHT);
		Menu menu = new Menu(MENU_START_X, 0, MENU_WIDTH, GLOBAL_HEIGHT);
		WholeScene wScene = new WholeScene(GLOBAL_WIDTH, GLOBAL_HEIGHT, gi, menu);
		Scene scene = new Scene(wScene, GLOBAL_WIDTH, GLOBAL_HEIGHT);

		menu.setGi(gi);
		gi.setMenu(menu);

		gi.getGmZone().addPlayer();
		gi.getGmZone().addPlayer();
		gi.getGmZone().addPlayer();
		gi.getGmZone().addPlayer();

		menu.buildView();

		primaryStage.setTitle("Jumanji Magic Tournament");
		primaryStage.setScene(scene);
		primaryStage.show();

		gi.startClient(EVENT_SERVER_IP, EVENT_SERVER_PORT);
	}
}
