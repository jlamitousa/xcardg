package client.gi;

import org.slf4j.LoggerFactory;

import actions.InvalidActionException;
import javafx.scene.Node;

public class BattlefieldZone extends CaptureZone {

	private static final double WIDTH_PERCENT = 0.65;
	private static final double HEIGHT_PERCENT = 1;
	private static final double CORNER_PERCENT = 10.0 / 100.0;
	private static final double CARD_WIDTH_PERCENT = 8.0 / 98.0;
	private static final double CARD_HEIGHT_PERCENT = 1.0 / 5.0;

	public BattlefieldZone(Environnement env) {
		super(env, "Battlefield", WIDTH_PERCENT, HEIGHT_PERCENT, CORNER_PERCENT, CARD_WIDTH_PERCENT, CARD_HEIGHT_PERCENT);
		LOGGER = LoggerFactory.getLogger("Battlefield");
	}
	
	public void degageAll() {
		for(Node n : getChildren()) {
			if(n instanceof Card) {
				
				Card c = (Card)n;
				
				if(c.isEngaged()) {
					c.degage();
				}
				
			}
		}
	}
	
	@Override
	public void reset() throws InvalidActionException {
		getChildren().clear();
	}
}
