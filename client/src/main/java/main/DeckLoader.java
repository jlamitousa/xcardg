package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.gi.Card;
import client.gi.CommandZone;
import client.gi.HandZone;
import client.gi.PickZone;
import client.gi.PlayerZone;

public class DeckLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger("DeckLoader");

	public static void loadDeck(PlayerZone playerZone, HandZone handZone, String deckFilePath, String deckName) throws Exception {

		String resourceDir = Main.getResourceDirURL();
		String deckFile = resourceDir + "/decks/"+deckFilePath;
		String cardDir = "/content/cards/";
		String cardDirURL = resourceDir + "/cards/";

		PickZone pickZone = playerZone.getPickZone();
		CommandZone commandZone = playerZone.getCommandZone();

		BufferedReader br = new BufferedReader(new InputStreamReader(new URL(deckFile).openStream()));
		String line = null;
		int nbCard = 0;
		List<Card> wholeDeck = new ArrayList<>();
		
		while((line = br.readLine()) != null) {

			Map<String, String> cardProps = new HashMap<>();
			Card c = null;
			
			for(String parts : line.split(";")) {
				String[] nameAndVal = parts.split(":");
				cardProps.put(nameAndVal[0], nameAndVal[1]);
			}

			if(!Main.existsInJar(cardDir + cardProps.get("name") + ".png")) {
				throw new Exception("Invalid card name: '"+cardProps.get("name")+"'");
			}

			if(cardProps.get("transform_name") != null && 
					!Main.existsInJar(cardDir + cardProps.get("transform_name") + ".png")) {
				throw new Exception("Invalid card transform_name: '"+cardProps.get("transform_name")+"'");
			}

			c = new Card(
					"P"+playerZone.getUserId() + "_" + cardProps.get("name") + "_" + (nbCard+1),
					cardProps.get("name"), 
					cardDirURL + cardProps.get("name") + ".png", 
					cardDirURL + cardProps.get("transform_name") + ".png");

			wholeDeck.add(c);
			handZone.captureCard(c, false);
			c.createSnapshot();
			pickZone.captureCard(c, false);
			nbCard++;

			if("1".equals(cardProps.get("commander"))) {
				commandZone.captureCard(c, false);
			}

			playerZone.setName(deckName);
			playerZone.getGI().initPlayerState(playerZone.getUserId());
			
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("card number "+nbCard+": "+c.getDisplay());
			}

		}

		playerZone.setWholeDeck(wholeDeck);
		playerZone.refreshMenuData();
		pickZone.sort();
	}
}

