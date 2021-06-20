package client.gi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.CaptureZoneAction;
import actions.InvalidActionException;
import actions.MouseAction;
import actions.PlayerZoneAction;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import main.Main;
import tranforms.DetailReversedTranslation;
import tranforms.DetailScale;
import tranforms.DetailTranslation;
import tranforms.InitReversedRotation;
import util.math.Point;

public class PlayerZone extends Pane implements Environnement, CornerClickableZone {

	private static final Logger LOGGER = LoggerFactory.getLogger("PlayerZone");

	private static final String PLAYER_ZONE_BCK = "/game_zone1.jpg";
	private static final String PLAYER_ZONE_BCK_MIRROR = "/game_zone1_mirror.png";
	private static final double WIDTH_PERCENT = 0.46;
	private static final double HEIGHT_PERCENT = 0.48;
	private static final double CARD_WIDTH_PERCENT = 8.0 / 98.0;
	private static final double CARD_HEIGHT_PERCENT = 1.0 / 5.0;
	private static final double CORNER_PERCENT = 5.0 / 100.0;
	private static final double BATTLEFIELD_START_X_PERCENT = CORNER_PERCENT;
	private static final double COMMAND_ZONE_START_Y_PERCENT = 3.0 / 100.0;
	private static final double PICK_ZONE_START_Y_PERCENT = CORNER_PERCENT + (3.0 / 10.0);
	public static final double SPACE_BETWEEN_ZONES_X_PERCENT = 2.0 / 100.0;
	public static final double SPACE_BETWEEN_ZONES_Y_PERCENT = 2.0 / 100.0;
	private static final double REVERSE_ANGLE = 180;

	private int id;
	private boolean isMe;
	private String name;
	private String userName;
	private int tokenCount;

	private Environnement env;
	private boolean isZoomed;
	private boolean isFixedZoomed;
	private boolean isReversed;
	private boolean isLeftSide;
	private boolean isMirrored;

	private BattlefieldZone battlefieldZone;
	private CommandZone commandZone;
	private GraveyardZone graveyardZone;
	private ExilZone exilZone;
	private PickZone pickZone;

	private List<Card> wholeDeck;
	private HandZone oppenentHandZone;


	public PlayerZone(Environnement env, boolean isReversed, boolean isLeftSide, int id) throws InvalidActionException {

		BackgroundImage bckImg = null;

		this.id = id;
		this.isMe = false;
		this.name = "none(P"+id+")";
		this.userName = "none";
		this.env = env;
		this.isReversed = isReversed;
		this.isLeftSide = isLeftSide;
		this.isMirrored = 
				!isReversed && isLeftSide ||
				isReversed  && !isLeftSide ;
		this.tokenCount = 0;

		this.setWidth(this.env.getWidth()*WIDTH_PERCENT);
		this.setMinWidth(getWidth());
		this.setMaxWidth(getWidth());

		this.setHeight(this.env.getHeight()*HEIGHT_PERCENT);
		this.setMinHeight(getHeight());
		this.setMaxHeight(getHeight());

		if(this.isMirrored) {

			bckImg = new BackgroundImage(new Image(Main.getResourceUrl(PLAYER_ZONE_BCK_MIRROR), this.getWidth(), this.getHeight(), false, true),
					BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					BackgroundSize.DEFAULT);

		} else {

			bckImg = new BackgroundImage(new Image(Main.getResourceUrl(PLAYER_ZONE_BCK), this.getWidth(), this.getHeight(), false, true),
					BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					BackgroundSize.DEFAULT);

		}


		setBackground(new Background(bckImg));

		this.isZoomed = false;
		this.isFixedZoomed = false;

		if(this.isReversed) {
			this.getTransforms().add(new InitReversedRotation(REVERSE_ANGLE, getWidth()/2, getHeight() / 2));
		}

		this.battlefieldZone = new BattlefieldZone(this);

		if(this.isMirrored) {
			this.battlefieldZone.setLayoutX(getWidth() - (this.battlefieldZone.getWidth() + getWidth()*BATTLEFIELD_START_X_PERCENT));	
		} else {
			this.battlefieldZone.setLayoutX(getWidth()*BATTLEFIELD_START_X_PERCENT);	
		}

		this.getChildren().add(battlefieldZone);

		this.commandZone = new CommandZone(this);

		if(this.isMirrored) {
			this.commandZone.setLayoutX(this.battlefieldZone.getLayoutX()-(this.commandZone.getWidth()+getWidth()*SPACE_BETWEEN_ZONES_X_PERCENT));
			this.commandZone.setLayoutY(getHeight() * COMMAND_ZONE_START_Y_PERCENT);
		} else {
			this.commandZone.setLayoutX(this.battlefieldZone.getLayoutX()+this.battlefieldZone.getWidth()+getWidth()*SPACE_BETWEEN_ZONES_X_PERCENT);
			this.commandZone.setLayoutY(getHeight() * COMMAND_ZONE_START_Y_PERCENT);
		}

		this.getChildren().add(commandZone);

		this.graveyardZone = new GraveyardZone(this);

		if(this.isMirrored) {
			this.graveyardZone.setLayoutX(this.battlefieldZone.getLayoutX()-(this.graveyardZone.getWidth()+getWidth()*SPACE_BETWEEN_ZONES_X_PERCENT));
			this.graveyardZone.setLayoutY(this.commandZone.getLayoutY()+this.commandZone.getHeight()+getHeight()*SPACE_BETWEEN_ZONES_Y_PERCENT);
		} else {
			this.graveyardZone.setLayoutX(this.battlefieldZone.getLayoutX()+this.battlefieldZone.getWidth()+getWidth()*SPACE_BETWEEN_ZONES_X_PERCENT);
			this.graveyardZone.setLayoutY(this.commandZone.getLayoutY()+this.commandZone.getHeight()+getHeight()*SPACE_BETWEEN_ZONES_Y_PERCENT);
		}

		this.getChildren().add(graveyardZone);

		this.exilZone = new ExilZone(this);

		if(this.isMirrored) {
			this.exilZone.setLayoutX(this.battlefieldZone.getLayoutX()-(this.exilZone.getWidth()+getWidth()*SPACE_BETWEEN_ZONES_X_PERCENT));
			this.exilZone.setLayoutY(getWidth() - (this.graveyardZone.getLayoutY()+this.graveyardZone.getHeight()+getHeight()*SPACE_BETWEEN_ZONES_Y_PERCENT));
		} else {
			this.exilZone.setLayoutX(this.battlefieldZone.getLayoutX()+this.battlefieldZone.getWidth()+getWidth()*SPACE_BETWEEN_ZONES_X_PERCENT);
			this.exilZone.setLayoutY(this.graveyardZone.getLayoutY()+this.graveyardZone.getHeight()+getHeight()*SPACE_BETWEEN_ZONES_Y_PERCENT);
		}

		this.getChildren().add(exilZone);

		this.pickZone = new PickZone(this);

		if(this.isMirrored) {
			this.pickZone.setLayoutX(this.commandZone.getLayoutX()-(this.pickZone.getWidth()+getWidth()*SPACE_BETWEEN_ZONES_X_PERCENT));
			this.pickZone.setLayoutY(getHeight()*PICK_ZONE_START_Y_PERCENT);
		} else {
			this.pickZone.setLayoutX(this.commandZone.getLayoutX()+this.commandZone.getWidth()+getWidth()*SPACE_BETWEEN_ZONES_X_PERCENT);
			this.pickZone.setLayoutY(getHeight()*PICK_ZONE_START_Y_PERCENT);
		}

		this.getChildren().add(pickZone);		

		this.wholeDeck = new ArrayList<>();

	}

	public void createOpponentHandZone() throws InvalidActionException {

		boolean imTheLeftNeihbour = (this == getGZ().getPlayerZone4());
		boolean isFrontNeihbour   = (this == getGZ().getPlayerZone3());

		this.oppenentHandZone = new HandZone(getGI(), this, true, !isFrontNeihbour);

		if(!imTheLeftNeihbour) {

			//Putting the handZone outside of the screen, symmetrically.

			Point absGmUpLeft = getGZ().getAbsoluteUpLeft();
			Point absGmDowRig = getGZ().getAbsoluteDownRight();
			Point absLocalHZUpLeft = getHandZone().getAbsoluteUpLeft();

			double ySpaceFromGMtoHz = absLocalHZUpLeft.getY() - absGmDowRig.getY();
			double finalY = -(ySpaceFromGMtoHz-absGmUpLeft.getY()) - this.oppenentHandZone.getHeight();

			this.oppenentHandZone.setLayoutY(finalY);

		}

		getGI().getChildren().add(0, this.oppenentHandZone);

	}

	public void setName(String name) {
		this.name = name+"(P"+id+")";
		this.userName = name;
	}

	public int getUserId() {
		return this.id;
	}

	public void setUserId(int id) {
		this.name = this.name+"(P"+id+")";
		this.id = id;
	}

	public boolean isMe() {
		return isMe;
	}

	public void setIsMe(boolean isMe) {
		this.isMe = isMe;
	}

	public HandZone getOppenentHandZone() {
		return oppenentHandZone;
	}

	public Card find(String cardId) {
		for(Card c : this.wholeDeck) {
			if(c.getCardId().equals(cardId)) {
				return c;
			}
		}
		return null;
	}

	public void remove(Card c) {
		this.wholeDeck.remove(c);
	}

	public void remove(String cardId) {

		Card theC = null;

		for(Card c : this.wholeDeck) {
			if(c.getCardId().equals(cardId)) {
				theC = c;
				break;
			}
		}

		if(theC != null) {
			this.wholeDeck.remove(theC);
		}

	}

	public void setWholeDeck(List<Card> wholeDeck) {
		this.wholeDeck = wholeDeck;
	}

	public String getUserName() {
		return this.userName;
	}

	@Override
	public boolean isReversed() {
		return this.isReversed;
	}

	@Override
	public Environnement getParentEnv() {
		return this.env;
	}


	public BattlefieldZone getBattlefieldZone() {
		return battlefieldZone;
	}

	public CommandZone getCommandZone() {
		return commandZone;
	}

	public GraveyardZone getGraveyardZone() {
		return graveyardZone;
	}

	public ExilZone getExilZone() {
		return exilZone;
	}

	public PickZone getPickZone() {
		return pickZone;
	}

	@Override
	public Point getInterfaceAbsMiddle() {
		return this.env.getInterfaceAbsMiddle();
	}

	@Override
	public Point getGameZoneAbsMiddle() {
		return this.env.getGameZoneAbsMiddle();
	}

	@Override
	public double getAbsoluteX(double x) {

		double val = 0;

		if(this.isReversed) {
			val = env.getAbsoluteX(0)+getLayoutX()+getWidth()-x;
		} else {
			val = env.getAbsoluteX(0)+getLayoutX()+x;
		}

		return val;
	}

	@Override
	public double getAbsoluteY(double y) {

		double val = 0;

		if(this.isReversed) {
			val = env.getAbsoluteY(0)+getLayoutY()+getHeight()-y;
		} else {
			val = env.getAbsoluteY(0)+getLayoutY()+y;
		}

		return val;
	}

	public double getCenterX() {
		return getAbsoluteX(getWidth() / 2);
	}

	public double getCenterY() {
		return getAbsoluteY(getHeight() / 2);
	}

	@Override
	public boolean handleMouseClicked(MouseAction ma, boolean remote) {

		boolean sendRemote = true;

		try {

			if(imTheTarget(ma)) {

				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("Target: "+new Point(ma.getX(), ma.getY()));
				}

				boolean doubleClicked = ma.isDoubleClicked();
				boolean isUpLeft = isReversed && isLeftSide && isDownLeftCorner(ma);
				boolean isDownLeft = !isReversed && isLeftSide && isUpRightCorner(ma);
				boolean isUpRight = isReversed && !isLeftSide && isDownLeftCorner(ma);
				boolean isDownRight = !isReversed && !isLeftSide && isUpRightCorner(ma);

				if(!remote && (isUpLeft || isDownLeft || isUpRight || isDownRight)) {

					if(doubleClicked && 
							!this.isFixedZoomed &&
							env.authorizeAction(this, PlayerZoneAction.ZOOM, remote)) {

						blockEvents();
						zoom();
						this.isFixedZoomed = true;
						env.notifyActionFinish(this, PlayerZoneAction.ZOOM);
						sendRemote = false;

					} else if(!doubleClicked && 
							this.isFixedZoomed && 
							env.authorizeAction(this, PlayerZoneAction.UNZOOM, remote)) {

						unzoom();
						this.isFixedZoomed = false;
						env.notifyActionFinish(this, PlayerZoneAction.UNZOOM);
						unblockEvents();
						sendRemote = false;

					}

				} 

			} else {

				Environnement directTarget = getDirectTargetChildren(ma);

				sendRemote = this.battlefieldZone.handleMouseClicked(ma, remote) && sendRemote;
				sendRemote = this.commandZone.handleMouseClicked(ma, remote) && sendRemote;
				sendRemote = this.graveyardZone.handleMouseClicked(ma, remote) && sendRemote;
				sendRemote = this.exilZone.handleMouseClicked(ma, remote) && sendRemote;
				sendRemote = this.pickZone.handleMouseClicked(ma, remote) && sendRemote;

				if(this.oppenentHandZone != null) {
					sendRemote = this.oppenentHandZone.handleMouseClicked(ma, remote) && sendRemote;
				}

				if(directTarget != null && directTarget instanceof Card) {
					sendRemote = directTarget.handleMouseClicked(ma, remote) && sendRemote;
				}

			}

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action: ", iae);
		}

		return !remote && sendRemote;

	}

	@Override
	public boolean handleMousePressed(MouseAction ma, boolean remote) { 

		boolean sendRemote = true;
		Environnement directTarget = getDirectTargetChildren(ma);

		sendRemote = this.battlefieldZone.handleMousePressed(ma ,remote) && sendRemote;
		sendRemote = this.commandZone.handleMousePressed(ma, remote) && sendRemote;
		sendRemote = this.graveyardZone.handleMousePressed(ma, remote) && sendRemote;
		sendRemote = this.exilZone.handleMousePressed(ma, remote) && sendRemote;
		sendRemote = this.pickZone.handleMousePressed(ma, remote) && sendRemote;

		if(this.oppenentHandZone != null) {
			sendRemote = this.oppenentHandZone.handleMousePressed(ma, remote) && sendRemote;
		}
		if(directTarget != null && directTarget instanceof Card) {
			sendRemote = directTarget.handleMousePressed(ma, remote) && sendRemote;
		}

		return !remote && sendRemote;
	}

	@Override
	public boolean handleMouseDragged(MouseAction ma, boolean remote) {

		boolean sendRemote = true;
		Environnement directTarget = getDirectTargetChildren(ma);
		
		sendRemote = this.battlefieldZone.handleMouseDragged(ma ,remote) && sendRemote;
		sendRemote = this.commandZone.handleMouseDragged(ma ,remote) && sendRemote;
		sendRemote = this.graveyardZone.handleMouseDragged(ma ,remote) && sendRemote;
		sendRemote = this.exilZone.handleMouseDragged(ma ,remote) && sendRemote;
		sendRemote = this.pickZone.handleMouseDragged(ma ,remote) && sendRemote;

		if(this.oppenentHandZone != null) {
			sendRemote = this.oppenentHandZone.handleMouseDragged(ma, remote) && sendRemote;
		}
		if(directTarget != null && directTarget instanceof Card) {
			sendRemote = directTarget.handleMouseDragged(ma, remote) && sendRemote;
		}
		
		return !remote && sendRemote;

	}

	@Override
	public boolean handleMouseReleased(MouseAction ma, boolean remote) { 

		boolean sendRemote = true;
		Environnement directTarget = getDirectTargetChildren(ma);
		
		sendRemote = this.battlefieldZone.handleMouseReleased(ma ,remote) && sendRemote;
		sendRemote = this.commandZone.handleMouseReleased(ma ,remote) && sendRemote;
		sendRemote = this.graveyardZone.handleMouseReleased(ma ,remote) && sendRemote;
		sendRemote = this.exilZone.handleMouseReleased(ma ,remote) && sendRemote;
		sendRemote = this.pickZone.handleMouseReleased(ma ,remote) && sendRemote;

		if(this.oppenentHandZone != null) {
			sendRemote = this.oppenentHandZone.handleMouseReleased(ma, remote) && sendRemote;
		}
		if(directTarget != null && directTarget instanceof Card) {
			sendRemote = directTarget.handleMouseReleased(ma, remote) && sendRemote;
		}
		
		return !remote && sendRemote;

	}

	private void zoom() {

		Translate cdt = null;
		double translationX = 0;
		double translationY = 0;

		Scale cds = null;
		double scaleFactorX = env.getWidth() / getWidth();
		double scaleFactorY = env.getHeight() / getHeight();

		if(this.isReversed()) {

			/*	Dans l'état renversé: 
					-les translations positive se font vers le haut. 
					-le haut-gauche se retrouve en bas-droit.

				Il faut donc, pour les translations:
					-se servir du point bas-droit.
					-se servir des translations négatives.
			 */

			Point downLeft = new Point(getAbsoluteX(0), getAbsoluteY(0));
			Point envDownLeft = new Point(env.getAbsoluteX(0)+env.getWidth(), env.getAbsoluteY(0)+env.getHeight());

			translationX = downLeft.getX() - envDownLeft.getX() ;
			translationY = downLeft.getY() - envDownLeft.getY();

			cdt = new DetailReversedTranslation(translationX, translationY);
			cds = new DetailScale(scaleFactorX, scaleFactorY, 0, 0);

		} else {

			translationX = env.getAbsoluteX(0) - getAbsoluteX(0);
			translationY = env.getAbsoluteY(0) - getAbsoluteY(0);

			cdt = new DetailTranslation(translationX, translationY);
			cds = new DetailScale(scaleFactorX, scaleFactorY, 0, 0);
		}

		this.getTransforms().add(cdt); 
		this.getTransforms().add(cds);

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("ZOOM ACTION (END): "+getDisplay());
		}
	}

	private void unzoom() {

		List<Transform> oldTransforms = new ArrayList<Transform>();
		List<Transform> newTransforms = this.getTransforms();

		oldTransforms.addAll(newTransforms);

		newTransforms.clear();

		for(Transform t : oldTransforms) {

			if(t instanceof DetailTranslation || t instanceof DetailReversedTranslation) {
				continue;
			}
			if(t instanceof DetailScale) {
				continue;
			}

			newTransforms.add(t);
		}

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("UNZOOM ACTION (END): "+getDisplay());
		}
	}

	public void captureCardInOpponentHandZone(Card c) throws InvalidActionException {
		this.oppenentHandZone.captureCard(c, true);
	}

	@Override
	public boolean authorizeAction(CaptureZone cz, CaptureZoneAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {


		boolean authorize = false;
		boolean dontPutInFront = false;
		CaptureZone currentlyCardCapturing = env.getCurrentCapturingZone();

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("CaptureZone:"+cz.getName()+" is trying to do action '"+action.name()+"'");
		}

		switch(action) {
		case CARD_SELECT: {

			authorize = env.authorizeAction(this, PlayerZoneAction.CARD_SELECT, remote, forceDontPutInFront);
			break;
		}
		case USER_MOVE: {

			dontPutInFront = currentlyCardCapturing!=null && currentlyCardCapturing!=cz;
			authorize = env.authorizeAction(this, PlayerZoneAction.USER_MOVE, remote, dontPutInFront);

			break;
		}
		case CARD_ENGAGE: {

			authorize = env.authorizeAction(this, PlayerZoneAction.CARD_ENGAGE, remote, forceDontPutInFront);
			break;
		}
		case CARD_DEGAGE: {

			authorize = env.authorizeAction(this, PlayerZoneAction.CARD_DEGAGE, remote, forceDontPutInFront);
			break;
		}
		case CARD_RETURN: {

			authorize = env.authorizeAction(this, PlayerZoneAction.CARD_RETURN, remote, forceDontPutInFront);
			break;
		}
		case CARD_ZOOM: {

			authorize = env.authorizeAction(this, PlayerZoneAction.CARD_ZOOM, remote, forceDontPutInFront);
			break;
		}
		case CARD_UNZOOM: {

			authorize = env.authorizeAction(this, PlayerZoneAction.CARD_UNZOOM, remote, forceDontPutInFront);
			break;
		}
		case ZOOM: {
			authorize = env.authorizeAction(this, PlayerZoneAction.ZONE_ZOOM, remote, forceDontPutInFront);
			break;
		}
		case CARD_PRECAPTURE: {

			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("currentlyCardCapturing: "+(currentlyCardCapturing!=null ? currentlyCardCapturing.getName() : "<node>"));
			}

			if(currentlyCardCapturing==null && env.authorizeAction(this, PlayerZoneAction.CARD_PRECAPTURE, remote, forceDontPutInFront)) {
				env.setCurrentCapturingZone(cz);
				authorize = true;
				dontPutInFront = true;
			}
			break;

		}
		case CARD_CAPTURE: {

			authorize = (currentlyCardCapturing == cz);
			break;
		}
		case CARD_CAPTURE_ABORD: {
			if(currentlyCardCapturing==cz && env.authorizeAction(this, PlayerZoneAction.CARD_CAPTURE_ABORD, remote, forceDontPutInFront)) {
				env.setCurrentCapturingZone(null);
				authorize=true;
				dontPutInFront=true;
			}
			break;
		}};




		if(authorize && !dontPutInFront && !forceDontPutInFront && !remote) {
			putInFront(cz);
		}

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Success: "+authorize+", put in front: "+(!dontPutInFront && !forceDontPutInFront && !remote));
		}

		return authorize;
	}

	public void degageAll() {
		this.battlefieldZone.degageAll();
	}

	@Override
	public void notifyActionFinish(CaptureZone cz, CaptureZoneAction action) throws InvalidActionException {

		switch(action) {
		case CARD_PRECAPTURE: {

			env.notifyActionFinish(this, PlayerZoneAction.CARD_PRECAPTURE);

			break;
		}
		case CARD_CAPTURE: {

			env.notifyActionFinish(this, PlayerZoneAction.CARD_CAPTURE);
			env.setCurrentCapturingZone(null);

			break;
		}}

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("CaptureZone:"+cz.getName()+" has finish action '"+action.name()+"'");
		}
	}

	public double getCornerWidth() {
		return this.getWidth()*CORNER_PERCENT;
	}

	@Override
	public PlayerZone getRelatedPZ() {
		return this;
	}

	public void captureInOppenentHand(String[] cardIds) throws InvalidActionException {

		if(cardIds != null) {

			for(int i = (cardIds.length-1); i >= 0; i--) {

				String cardId = cardIds[i];
				Card c = find(cardId);

				if(c != null) {
					captureCardInOpponentHandZone(c);
				}

			}

		}

	}

	public void createToken(boolean remote) throws InvalidActionException {

		String cardDirURL = "/cards/";
		Point cardRelMiddlePZ = null;

		Card c = new Card(
				"P"+getUserId() + "_TOKEN_" + (this.tokenCount)+"__"+(wholeDeck.size()+1),
				"_TOKEN_" + (this.tokenCount++), 
				Main.getResourceUrl(cardDirURL + "default_token.png"), 
				Main.getResourceUrl(cardDirURL + "default_token.png"));

		getHandZone().captureCard(c, false);
		wholeDeck.add(c);

		cardRelMiddlePZ = c.getReverseRelative(getAbsoluteMiddle());
		c.setX(cardRelMiddlePZ.getX());
		c.setY(cardRelMiddlePZ.getY());

		battlefieldZone.preCaptureCard(c, c.getAbsolutePos(), false, remote);
		battlefieldZone.captureCard(c, false, false);

		refreshMenuData();
	}

	@Override
	public void reset() throws InvalidActionException {
		battlefieldZone.reset();
		commandZone.reset();
		graveyardZone.reset();
		exilZone.reset();
		pickZone.reset();
		wholeDeck.clear();
		if(oppenentHandZone != null) {
			oppenentHandZone.reset();
		}
		getGI().initPlayerState(this.id);
	}

	@Override
	public Point getCCZReverseRelative(Point absPoint) {
		return getReverseRelative(absPoint);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public GraphicInterface getGI() {
		return env.getGI();
	}

	@Override
	public GameZone getGZ() {
		return env.getGZ();
	}

	@Override
	public CaptureZone getCurrentCapturingZone() {
		return env.getCurrentCapturingZone();
	}

	@Override
	public PlayerZone getCurrentlyZoomedPZ() {
		return env.getCurrentlyZoomedPZ();
	}

	@Override
	public CaptureZone getCurrentCapturingZoneZoomed() {
		return env.getCurrentCapturingZoneZoomed();
	}

	@Override
	public Card getCurrentlyZoomedCard() {
		return env.getCurrentlyZoomedCard();
	}

	@Override
	public String getEnvId() {
		return getName();
	}

}
