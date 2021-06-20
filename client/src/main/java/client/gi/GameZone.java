package client.gi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.CaptureZoneAction;
import actions.GameZoneAction;
import actions.InvalidActionException;
import actions.MouseAction;
import actions.PileZoneAction;
import actions.PlayerZoneAction;
import javafx.scene.layout.Pane;
import util.math.Point;
import util.math.Vector;

public class GameZone extends Pane implements Environnement {

	private static final Logger LOGGER = LoggerFactory.getLogger("GameZone");

	private static final double WIDTH_PERCENT = 98.0 / 100.0;
	private static final double HEIGHT_PERCENT = 4.0 / 6.0;

	public static final double WIDTH_BETWEEN_ZONE = 0.04;
	public static final double HEIGHT_BETWEEN_ZONE = 0.04;

	private Environnement env;

	public double width;
	public double height;

	private PlayerZone playerZone1;
	private PlayerZone playerZone2;
	private PlayerZone playerZone3;
	private PlayerZone playerZone4;

	private PileZone pileZone;

	public GameZone(Environnement env) throws InvalidActionException {

		PlayerZone fakePZ = null;
		Point zoneUpLeft = null;
		Point zoneDownRight = null;

		double playerZoneWidth = 0;
		double playerZoneHeight = 0;
		double pileX = 0;
		double pileY = 0;

		this.env = env;

		this.setWidth(this.env.getWidth()*WIDTH_PERCENT);
		this.setMinWidth(getWidth());
		this.setMaxWidth(getWidth());

		this.setHeight(this.env.getHeight()*HEIGHT_PERCENT);
		this.setMinHeight(getHeight());
		this.setMaxHeight(getHeight());

		this.setLayoutX((this.env.getWidth()-this.getWidth()) / 2);
		this.setLayoutY((this.env.getWidth()-this.getWidth()) / 2);


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(getDisplay());
		}

		fakePZ = new PlayerZone(this, true, true, -1);
		zoneUpLeft = fakePZ.getAbsoluteDownRight();
		zoneDownRight = fakePZ.getAbsoluteUpLeft();

		playerZoneWidth = zoneDownRight.getX() - zoneUpLeft.getX();
		playerZoneHeight = zoneDownRight.getY() - zoneUpLeft.getY();

		this.pileZone = new PileZone(this);
		this.pileZone.setLayoutX(playerZoneWidth);
		this.pileZone.setLayoutY(playerZoneHeight-this.pileZone.getHeight() / 2);

		this.getChildren().add(this.pileZone);
	}

	@Override
	public String getName() {
		return "GameZone";
	}

	/**
	 * Nous faisons en sorte que le jouer connecté voit toujours
	 * son plateau de jeu dans la partie bas-droite. Cela explique
	 * le traitement suivant pour retrouver la position définitive 
	 * de la zone de jeu à traiter.
	 */
	public PlayerZone findPlayerZone(int id) {

		PlayerZone pz = null;
		int userId = this.playerZone1.isMe() ? this.playerZone1.getUserId() : -1;

		if(userId == 1) {

			if(id==1) {
				pz = this.playerZone1;
			} else if(id==2) {
				pz = this.playerZone2;
			}  else if(id==3) {
				pz = this.playerZone3;
			}  else if(id==4) {
				pz = this.playerZone4;
			}

		} else if(userId == 2) {

			if(id==1) {
				pz = this.playerZone2;
			} else if(id==2) {
				pz = this.playerZone1;
			}  else if(id==3) {
				pz = this.playerZone4;
			}  else if(id==4) {
				pz = this.playerZone3;
			}
			
		} else if(userId == 3) {

			if(id==1) {
				pz = this.playerZone3;
			} else if(id==2) {
				pz = this.playerZone4;
			}  else if(id==3) {
				pz = this.playerZone1;
			}  else if(id==4) {
				pz = this.playerZone2;
			}
			
		} else if(userId == 4) {

			if(id==1) {
				pz = this.playerZone4;
			} else if(id==2) {
				pz = this.playerZone3;
			}  else if(id==3) {
				pz = this.playerZone2;
			}  else if(id==4) {
				pz = this.playerZone1;
			}
			
		} else {

			//Nous somme simplement spectateur.

			if(id==1) {
				pz = this.playerZone1;
			} else if(id==2) {
				pz = this.playerZone2;
			}  else if(id==3) {
				pz = this.playerZone3;
			}  else if(id==4) {
				pz = this.playerZone4;
			}

		}

		return pz;
	}

	@Override
	public boolean  handleMouseClicked(MouseAction ma, boolean remote) { 
		
		boolean sendRemote = true;
		
		sendRemote = this.playerZone1.handleMouseClicked(ma, remote) && sendRemote;
		sendRemote = this.playerZone2.handleMouseClicked(ma, remote) && sendRemote;
		sendRemote = this.playerZone3.handleMouseClicked(ma, remote) && sendRemote;
		sendRemote = this.playerZone4.handleMouseClicked(ma, remote) && sendRemote;
		sendRemote = this.pileZone.handleMouseClicked(ma, remote) && sendRemote;
		
		return !remote && sendRemote;
		
	}

	@Override
	public boolean handleMousePressed(MouseAction ma, boolean remote) { 
		
		boolean sendRemote = true;
		
		sendRemote = this.playerZone1.handleMousePressed(ma, remote) && sendRemote;
		sendRemote = this.playerZone2.handleMousePressed(ma, remote) && sendRemote;
		sendRemote = this.playerZone3.handleMousePressed(ma, remote) && sendRemote;
		sendRemote = this.playerZone4.handleMousePressed(ma, remote) && sendRemote;
		sendRemote = this.pileZone.handleMousePressed(ma, remote) && sendRemote;
		
		return !remote && sendRemote;
		
	}

	@Override
	public boolean handleMouseDragged(MouseAction ma, boolean remote) { 
		
		boolean sendRemote = true;
		
		sendRemote = this.playerZone1.handleMouseDragged(ma, remote) && sendRemote;
		sendRemote = this.playerZone2.handleMouseDragged(ma, remote) && sendRemote;
		sendRemote = this.playerZone3.handleMouseDragged(ma, remote) && sendRemote;
		sendRemote = this.playerZone4.handleMouseDragged(ma, remote) && sendRemote;
		sendRemote = this.pileZone.handleMouseDragged(ma, remote) && sendRemote;
		
		return !remote && sendRemote;
		
	}

	@Override
	public boolean handleMouseReleased(MouseAction ma, boolean remote) { 
		
		boolean sendRemote = true;
		
		sendRemote = this.playerZone1.handleMouseReleased(ma, remote) && sendRemote;
		sendRemote = this.playerZone2.handleMouseReleased(ma, remote) && sendRemote;
		sendRemote = this.playerZone3.handleMouseReleased(ma, remote) && sendRemote;
		sendRemote = this.playerZone4.handleMouseReleased(ma, remote) && sendRemote;
		sendRemote = this.pileZone.handleMouseReleased(ma, remote) && sendRemote;
		
		return !remote && sendRemote;
		
	}

	public void addPlayer() throws InvalidActionException {
		if(playerZone1==null) {

			playerZone1 = new PlayerZone(this, false, false, -1);

			playerZone1.setLayoutX(getWidth()-playerZone1.getWidth());
			playerZone1.setLayoutY(getHeight()-playerZone1.getHeight());

			this.getChildren().add(this.playerZone1);

			getGI().initPlayerState(1);

			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(playerZone1.getDisplay());
			}

			return;
		}
		if(playerZone2==null) {

			playerZone2 = new PlayerZone(this, true, true, -1);

			playerZone2.setLayoutX(0);
			playerZone2.setLayoutY(0);

			this.getChildren().add(this.playerZone2);

			getGI().initPlayerState(2);

			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(playerZone2.getDisplay());
			}

			return;
		}
		if(playerZone3==null) {

			playerZone3 = new PlayerZone(this, true, false, -1);

			playerZone3.setLayoutX(getWidth()-playerZone3.getWidth());
			playerZone3.setLayoutY(0);

			this.getChildren().add(this.playerZone3);

			getGI().initPlayerState(3);

			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(playerZone3.getDisplay());
			}

			return;
		}
		if(playerZone4==null) {
			playerZone4 = new PlayerZone(this, false, true, -1);

			playerZone4.setLayoutX(0);
			playerZone4.setLayoutY(getHeight()-playerZone4.getHeight());

			this.getChildren().add(this.playerZone4);

			getGI().initPlayerState(4);

			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(playerZone4.getDisplay());
			}

			return;
		}
	}

	public PlayerZone getPlayerZone1() {
		return playerZone1;
	}

	public PlayerZone getPlayerZone2() {
		return playerZone2;
	}

	public PlayerZone getPlayerZone3() {
		return playerZone3;
	}

	public PlayerZone getPlayerZone4() {
		return playerZone4;
	}

	public PileZone getPileZone() {
		return pileZone;
	}

	@Override
	public double getAbsoluteX(double x) {
		return (env!=null) ? env.getAbsoluteX(0)+getLayoutX()+x : 0;
	}

	@Override
	public double getAbsoluteY(double y) {
		return (env!=null) ? env.getAbsoluteY(0)+getLayoutY()+y : 0;
	}

	public boolean isRectangleInZone(Point upLeft, Point downRight, Vector preTranslation) {

		Point finalUpLeft = upLeft;
		Point finalDownRight = downRight;
		boolean inZone = false;
		
		if(preTranslation != null) {
			finalUpLeft = new Point(upLeft.getX()-preTranslation.getX(), upLeft.getY()-preTranslation.getY());
			finalDownRight = new Point(downRight.getX()-preTranslation.getX(), downRight.getY()-preTranslation.getY());
		}

		inZone = isInZone(finalUpLeft) && isInZone(finalDownRight);
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(getDisplay());
			LOGGER.debug("finalUpLeft: "+finalUpLeft+", finalDownRight: "+finalDownRight+", inZone: "+inZone);
		}
		
		return inZone;
	}
	
	@Override
	public boolean authorizeAction(CaptureZone cz, CaptureZoneAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {

		if(cz instanceof PileZone) {
			return authorizeAction((PileZone)cz, PileZoneAction.translate(action), remote, forceDontPutInFront);
		}

		throw new InvalidActionException("Not implemented");
	}

	public boolean authorizeAction(PileZone pilezone, PileZoneAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {

		boolean authorize = false;
		boolean dontPutInFront = false;
		CaptureZone currentlyCardCapturing = getCurrentCapturingZone();
		PlayerZone currentlyZoomed = getCurrentlyZoomedPZ();

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("PlayerZone:"+pilezone.getName()+" is trying to do action '"+action.name()+"'");
		}

		switch(action) {
		case CARD_SELECT: {

			authorize = env.authorizeAction(this, GameZoneAction.CARD_SELECT, remote, forceDontPutInFront);
			break;
		}
		case USER_MOVE: {

			authorize = (currentlyZoomed == null) && env.authorizeAction(this, GameZoneAction.PZ_USER_MOVE, remote, forceDontPutInFront);
			break;
		}
		case ZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.ZONE_ZOOM, remote, forceDontPutInFront);
			break;
		}
		case UNZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.ZONE_UNZOOM, remote, forceDontPutInFront);
			break;
		}
		case CARD_ZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.CARD_ZOOM, remote, forceDontPutInFront);
			break;
		}
		case CARD_UNZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.CARD_UNZOOM, remote, forceDontPutInFront);
			break;
		}
		case CARD_PRECAPTURE: {

			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("currentlyCardCapturing: "+(currentlyCardCapturing!=null ? currentlyCardCapturing.getName() : "<node>"));
			}

			if(currentlyCardCapturing==null && env.authorizeAction(this, GameZoneAction.CARD_PRECAPTURE, remote, forceDontPutInFront)) {
				env.setCurrentCapturingZone(pilezone);
				authorize = true;
				dontPutInFront = true;
			}
			break;

		}
		case CARD_CAPTURE: {
			if(currentlyCardCapturing==pilezone && env.authorizeAction(this, GameZoneAction.PZ_CARD_CAPTURE, remote, forceDontPutInFront)) {
				authorize = true;
				dontPutInFront = true;
			}
			break;
		}
		case CARD_CAPTURE_ABORD: {
			authorize=env.authorizeAction(this, GameZoneAction.CARD_CAPTURE_ABORD, remote, forceDontPutInFront);
			dontPutInFront = true;
			break;
		}}


		if(authorize && !dontPutInFront && !forceDontPutInFront && !remote) {
			putInFront(pilezone);
		}


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Success: "+authorize+", put in front: "+(!dontPutInFront && !forceDontPutInFront && !remote));
		}

		return authorize;
	}

	@Override
	public boolean authorizeAction(PlayerZone pz, PlayerZoneAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {

		boolean authorize = false;
		boolean dontPutInFront = false;
		CaptureZone currentlyCardCapturing = getCurrentCapturingZone();
		CaptureZone currentlyCardCapturingZoomed = getCurrentCapturingZoneZoomed();
		PlayerZone currentlyZoomed = getCurrentlyZoomedPZ();

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("PlayerZone:"+pz.getName()+" is trying to do action '"+action.name()+"'");
		}

		switch(action) {
		case CARD_SELECT: {

			authorize = env.authorizeAction(this, GameZoneAction.CARD_SELECT, remote, forceDontPutInFront);
			break;
		}
		case USER_MOVE: {

			authorize = (currentlyZoomed == null || currentlyZoomed==pz) && env.authorizeAction(this, GameZoneAction.PZ_USER_MOVE, remote, forceDontPutInFront);
			break;
		}
		case CARD_ENGAGE: {

			authorize = env.authorizeAction(this, GameZoneAction.PZ_CARD_ENGAGE, remote, forceDontPutInFront);
			break;
		}
		case CARD_DEGAGE: {

			authorize = env.authorizeAction(this, GameZoneAction.PZ_CARD_DEGAGE, remote, forceDontPutInFront);
			break;
		}
		case CARD_RETURN: {

			authorize = env.authorizeAction(this, GameZoneAction.PZ_CARD_RETURN, remote, forceDontPutInFront);
			break;
		}
		case ZONE_ZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.ZONE_ZOOM, remote, forceDontPutInFront);
			break;
		}
		case ZONE_UNZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.ZONE_UNZOOM, remote, forceDontPutInFront);
			break;
		}
		case ZOOM: {

			authorize = (currentlyZoomed == null) && env.authorizeAction(this, GameZoneAction.PZ_ZOOM, remote, forceDontPutInFront);
			break;
		}
		case CAPTURE_ZOOM: {

			authorize = (currentlyZoomed == pz) && env.authorizeAction(this, GameZoneAction.PZ_CAPTURE_ZOOM, remote, forceDontPutInFront);
			dontPutInFront = true;
			break;
		}
		case UNZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.PZ_UNZOOM, remote, forceDontPutInFront);
			dontPutInFront = true;
			break;
		}
		case CAPTURE_UNZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.PZ_CAPTURE_UNZOOM, remote, forceDontPutInFront);
			dontPutInFront = true;
			break;
		}
		case CARD_ZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.CARD_ZOOM, remote, forceDontPutInFront);
			break;
		}
		case CARD_UNZOOM: {

			authorize = env.authorizeAction(this, GameZoneAction.CARD_UNZOOM, remote, forceDontPutInFront);
			break;
		}
		case CARD_PRECAPTURE: {
			if(currentlyCardCapturing==null && env.authorizeAction(this, GameZoneAction.CARD_PRECAPTURE, remote, forceDontPutInFront)) {
				authorize = true;
				dontPutInFront = true;
			}
			break;
		}
		case CARD_CAPTURE_ABORD: {
			authorize=env.authorizeAction(this, GameZoneAction.CARD_CAPTURE_ABORD, remote, forceDontPutInFront);
			dontPutInFront = true;
			break;
		}
		case CARD_CAPTURE: {

			authorize = env.authorizeAction(this, GameZoneAction.CARD_CAPTURE, remote, forceDontPutInFront);
			break;

		}}


		if(authorize && !dontPutInFront && !forceDontPutInFront && !remote) {
			putInFront(pz);
		}


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Success: "+authorize+", put in front: "+(!dontPutInFront && !forceDontPutInFront && !remote));
		}

		return authorize;
	}

	@Override
	public void notifyActionFinish(CaptureZone cz, CaptureZoneAction action) throws InvalidActionException {

		if(cz instanceof PileZone) {
			notifyActionFinish((PileZone)cz, PileZoneAction.translate(action));
			return; 
		}

		throw new InvalidActionException("Not implemented");
	}

	@Override
	public void notifyActionFinish(PileZone pileZone, PileZoneAction action) throws InvalidActionException {
		env.notifyActionFinish(pileZone, action);
	}

	@Override
	public void notifyActionFinish(PlayerZone pz, PlayerZoneAction action) throws InvalidActionException {

		switch(action) {
		case ZOOM: {

			setCurrentlyZoomedPZ(pz);
			break;

		}
		case CARD_CAPTURE: {

			setCurrentCapturingZone(null);
			break;

		}
		case UNZOOM: {

			setCurrentlyZoomedPZ(null);
			break;

		}}
	}

	@Override
	public Point getInterfaceAbsMiddle() {
		return env.getInterfaceAbsMiddle();
	}

	@Override
	public Point getGameZoneAbsMiddle() {
		return getAbsoluteMiddle();
	}

	@Override
	public Environnement getParentEnv() {
		return this.env;
	}

	@Override
	public CaptureZone getCurrentCapturingZone() {
		return env.getCurrentCapturingZone();
	}

	@Override
	public void setCurrentCapturingZone(CaptureZone cz) throws InvalidActionException {
		env.setCurrentCapturingZone(cz);
	}

	@Override
	public PlayerZone getCurrentlyZoomedPZ() {
		return env.getCurrentlyZoomedPZ();
	}

	@Override
	public void setCurrentlyZoomedPZ(PlayerZone pz) throws InvalidActionException {
		env.setCurrentlyZoomedPZ(pz);
	}

	@Override
	public PlayerZone getRelatedPZ() {
		return null;
	}

	@Override
	public GraphicInterface getGI() {
		return env.getGI();
	}
	
	@Override
	public GameZone getGZ() {
		return this;
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
	public void reset() throws InvalidActionException {
		playerZone1.reset();
		playerZone2.reset();
		playerZone3.reset();
		playerZone4.reset();
		this.pileZone.reset();
	}
	
	@Override
	public String getEnvId() {
		return "GameZone";
	}
}
