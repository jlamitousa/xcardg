package main;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.gi.GraphicInterface;
import client.menu.Menu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger("Main");

	private static final String JAR_NAME = "jumanji-magic-tournament.jar";
	
	private static final String EVENT_SERVER_IP = "51.68.80.240";
	private static final int EVENT_SERVER_PORT = 4546;

	private static final double GLOBAL_WIDTH = 1250;
	private static final double GLOBAL_HEIGHT = 600;
	private static final double MENU_WIDTH = 240;
	private static final double GI_WIDTH = (GLOBAL_WIDTH-MENU_WIDTH) - 10;
	private static final double MENU_START_X = GI_WIDTH + (GLOBAL_WIDTH - GI_WIDTH - MENU_WIDTH);

	public static void main(String[] args) {

		if(LOGGER.isInfoEnabled()) {
			LOGGER.info("Starting jumanji-magic-tournament client");
		}

		launch(args);
	}

	public static String getResourceDirURL() {

		File file = new File(JAR_NAME);
		String finalUrl = null;
		
		try {
			finalUrl = new String(file.toURI().toURL().toString()+"!/content").replace("file:", "jar:file:");
		} catch(Exception e) {
			LOGGER.error("", e);
		}
		
		return finalUrl;
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
