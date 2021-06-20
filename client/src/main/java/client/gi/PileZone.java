package client.gi;

import org.slf4j.LoggerFactory;

import actions.InvalidActionException;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import main.Main;

public class PileZone extends Pile {

	private static final String BCK_URL = "/pile-zone-bck.png";
	
	private static final double WIDTH_PERCENT = 2 * GameZone.WIDTH_BETWEEN_ZONE;
	private static final double HEIGHT_PERCENT = 0.25;
	private static final double CORNER_PERCENT = 0.2;
	private static final double CARD_WIDTH_PERCENT = 0.8;
	private static final double CARD_HEIGHT_PERCENT = 0.9;
	
	public PileZone(Environnement env) {
		
		super(env, "PILE", WIDTH_PERCENT, HEIGHT_PERCENT, CORNER_PERCENT, CARD_WIDTH_PERCENT, CARD_HEIGHT_PERCENT);
		
		LOGGER = LoggerFactory.getLogger("PileZone");
		
		String bckUrl = Main.getResourceDirURL() + BCK_URL;
		BackgroundImage bckImg = new BackgroundImage(new Image(bckUrl, env.getWidth()*WIDTH_PERCENT, env.getHeight()*HEIGHT_PERCENT, false, true),
				BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				BackgroundSize.DEFAULT);
		
		setBackground(new Background(bckImg));
		
		setShowMode(true);
	}
	
	@Override
	public void reset() throws InvalidActionException {
		getChildren().clear();
	}
}
