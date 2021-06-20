package client.gi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.CaptureZoneAction;
import actions.CardAction;
import actions.InvalidActionException;
import actions.MouseAction;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import tranforms.DetailReversedTranslation;
import tranforms.DetailScale;
import tranforms.DetailTranslation;
import tranforms.EDRotation;
import tranforms.InitReversedRotation;
import tranforms.InitScale;
import util.helper.EventHandling;
import util.math.MathOps;
import util.math.Point;
import util.math.Vector;

public abstract class CaptureZone extends Pane implements Environnement, CornerClickableZone {

	protected Logger LOGGER;

	private static final double REVERSE_ANGLE = 180; 
	private double cornerPercent;
	private double cardWidhtPercent;
	private double cardHeightPercent;

	private Environnement env;
	private String name;

	//When changing card size it can be, at the end, be outsize zone. 
	//This variable ensure it stay in the zone
	private Vector preCaptureTranslation;


	public CaptureZone(
			Environnement env, 
			String name, 
			double widthPercent, 
			double heightPercent,
			double cornerPercent,
			double cardWidhtPercent,
			double cardHeightPercent) {

		LOGGER = LoggerFactory.getLogger("CaptureZone");

		BackgroundImage bckImg = null;

		this.env = env;
		this.name = name;
		this.cornerPercent = cornerPercent;
		this.cardWidhtPercent = cardWidhtPercent;
		this.cardHeightPercent = cardHeightPercent;

		this.setWidth(this.env.getWidth()*widthPercent);
		this.setMinWidth(getWidth());
		this.setMaxWidth(getWidth());

		this.setHeight(this.env.getHeight()*heightPercent);
		this.setMinHeight(getHeight());
		this.setMaxHeight(getHeight());

		this.preCaptureTranslation = null;

	}

	@Override
	public String getName() {
		return env.getName()+":"+this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isReversed() {
		return env.isReversed();
	}

	public Vector getPreCaptureTranslation() {
		return preCaptureTranslation;
	}

	public void setPreCaptureTranslation(Vector preCaptureTranslation) {
		this.preCaptureTranslation = preCaptureTranslation;
	}

	@Override
	public boolean handleMouseClicked(MouseAction ma, boolean remote) {

		boolean sendRemote = true;	
		Environnement target = getTargetChildren(ma);

		if(target != null) {
			sendRemote = target.handleMouseClicked(ma, remote) && sendRemote;
		}

		return !remote && sendRemote;
		
	}

	@Override
	public boolean handleMouseDragged(MouseAction ma, boolean remote) {

		boolean sendRemote = true;	

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("handleMouseDragged:");
			LOGGER.debug(getDisplay());
		}

		try {

			Environnement target = getTargetChildren(ma);
			Card c = EventHandling.getTargetAs(ma, Card.class);

			if(c != null && !c.isZoomed()) {

				boolean isOtherEnv = c != null && c.getParentEnv() != this;
				boolean iAmAlreadyCapturing = getCurrentCapturingZone() == this;
				boolean isFullyInMyCaptureZone = c != null && isRectangleMiddleInZone(c.getAbsoluteUpLeft(), c.getAbsoluteDownRight(), null);
				boolean isInMyCaptureZone = false;
				boolean localCaptureInFake = !remote && isFakeZone();
				boolean remoteInHand = remote && this == getGI().getHandZone();
				boolean remoteInNotOwnedFakeZone = remote && isFakeZone() && (getRelatedPZ().getUserId() != ma.getSourceUserId());
				boolean validCapture = !localCaptureInFake && !remoteInHand && !remoteInNotOwnedFakeZone;


				if(isFullyInMyCaptureZone) {
					this.preCaptureTranslation = null;
				}

				isInMyCaptureZone = c != null && isRectangleMiddleInZone(c.getAbsoluteUpLeft(), c.getAbsoluteDownRight(), this.preCaptureTranslation);

				if(LOGGER.isDebugEnabled()) {

					LOGGER.debug("Env: "+getDisplay());

					if(c!=null) {
						LOGGER.debug("Card: "+c.getDisplay());
						LOGGER.debug("preCaptureTranslation: "+this.preCaptureTranslation);
						LOGGER.debug("Card env: "+c.getParentEnv().getDisplay());
						LOGGER.debug(""+c.getTransforms());
					} else {
						LOGGER.debug("Card: <no_card>");
					}

					LOGGER.debug("remote="+remote+", isOtherEnv="+isOtherEnv+" , iAmAlreadyCapturing="+iAmAlreadyCapturing+" , isInMyCaptureZone="+isInMyCaptureZone+", localCaptureInFake="+localCaptureInFake+", remoteInHand="+remoteInHand);

				}


				if(isOtherEnv && isInMyCaptureZone && !iAmAlreadyCapturing && validCapture && env.authorizeAction(this, CaptureZoneAction.CARD_PRECAPTURE, remote)) {

					preCaptureCard(c, ma, true, remote);

				} else if (c != null && iAmAlreadyCapturing && !isInMyCaptureZone && env.authorizeAction(this, CaptureZoneAction.CARD_CAPTURE_ABORD, remote)) {

					abordCaptureCard(c);

				}

			}

			if(target != null) {

				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("Transmit dragging event: ");
					LOGGER.debug("From: "+this.getDisplay());
					LOGGER.debug("To: "+target.getDisplay());
				}

				sendRemote = target.handleMouseDragged(ma, remote) && sendRemote;
			}

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action: ", iae);
		}

		return !remote && sendRemote;
	}

	@Override
	public boolean handleMousePressed(MouseAction ma, boolean remote) {

		boolean sendRemote = true;
		Environnement target = getTargetChildren(ma);

		if(target != null) {
			sendRemote = target.handleMousePressed(ma, remote) && sendRemote;
		}

		return !remote && sendRemote;
	}

	@Override
	public boolean handleMouseReleased(MouseAction ma, boolean remote) {

		boolean sendRemote = true;
		Environnement target = null;

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("handleMouseReleased:");
			LOGGER.debug(getDisplay());
		}

		try {

			target = getTargetChildren(ma);
			Card c = EventHandling.getTargetAs(ma, Card.class);

			if(c != null && !c.isZoomed()) {

				boolean isOtherEnv = c != null && c.getParentEnv() != this;
				boolean iAmCapturing = getCurrentCapturingZone() == this;
				boolean isInMyCaptureZone = c != null && isRectangleMiddleInZone(c.getAbsoluteUpLeft(), c.getAbsoluteDownRight(), this.preCaptureTranslation);
				boolean localCaptureInFake = !remote && isFakeZone();
				boolean remoteInHand = remote && this == getGI().getHandZone();
				boolean remoteInNotOwnedFakeZone = remote && isFakeZone() && (getRelatedPZ().getUserId() != ma.getSourceUserId());
				boolean validCapture = !localCaptureInFake && !remoteInHand && !remoteInNotOwnedFakeZone;

				if(LOGGER.isDebugEnabled()) {

					LOGGER.debug("Env: "+getDisplay());

					if(c!=null) {
						LOGGER.debug("Card: "+c.getDisplay());
						LOGGER.debug("Card env: "+c.getParentEnv().getDisplay());
						LOGGER.debug(""+c.getTransforms());
					} else {
						LOGGER.debug("Card: <no_card>");
					}

					LOGGER.debug("remote="+remote+", isOtherEnv="+isOtherEnv+" , iAmCapturing="+iAmCapturing+" , isInMyCaptureZone="+isInMyCaptureZone+", localCaptureInFake="+localCaptureInFake+", remoteInHand="+remoteInHand);
				}

				if(isOtherEnv && iAmCapturing && isInMyCaptureZone && validCapture && env.authorizeAction(this, CaptureZoneAction.CARD_CAPTURE, remote)) {

					captureCard(c, true);
					env.notifyActionFinish(this, CaptureZoneAction.CARD_CAPTURE);

				}

			}

			if(target != null) {
				sendRemote = target.handleMouseReleased(ma, remote) && sendRemote;
			}

		} catch (InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		return !remote && sendRemote;
	}

	@Override
	public boolean authorizeAction(Card c, CardAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {

		boolean authorize = false;

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Card:"+c.getName()+" is trying to do action '"+action.name()+"'");
		}

		switch(action){
		case SELECT: {

			authorize = env.authorizeAction(this, CaptureZoneAction.CARD_SELECT, remote, forceDontPutInFront);
			break;

		}
		case USER_MOVE: {

			authorize = env.authorizeAction(this, CaptureZoneAction.USER_MOVE, remote, forceDontPutInFront);
			break;

		}
		case ENGAGE: {

			authorize = env.authorizeAction(this, CaptureZoneAction.CARD_ENGAGE, remote, forceDontPutInFront);
			break;

		}
		case DEGAGE: {

			authorize = env.authorizeAction(this, CaptureZoneAction.CARD_DEGAGE, remote, forceDontPutInFront);
			break;

		}
		case RETURN: {

			authorize = env.authorizeAction(this, CaptureZoneAction.CARD_RETURN, remote, forceDontPutInFront);
			break;

		}
		case USER_ZOOM: {

			Card currentlyZoomedCard = getCurrentlyZoomedCard();
			
			authorize = env.authorizeAction(this, CaptureZoneAction.CARD_ZOOM, remote, forceDontPutInFront);
			
			if(authorize) {
				
				if(currentlyZoomedCard != null) {
					currentlyZoomedCard.unzoom();
				}
				
				setCurrentlyZoomedCard(c);
				
			}
			
			break;

		}
		case USER_UNZOOM: {

			authorize = env.authorizeAction(this, CaptureZoneAction.CARD_UNZOOM, remote, forceDontPutInFront);
			break;

		}
		default:
		}

		if(authorize && !forceDontPutInFront && !remote) {
			putInFront(c);
		}

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Success: "+authorize+", put in front: "+(!forceDontPutInFront && !remote));
		}

		return authorize;
	}

	public void notifyActionFinish(Card c, CardAction action) throws InvalidActionException {
		
		switch(action) {
		case USER_UNZOOM: {
			setCurrentlyZoomedCard(null);
			break;
		}
		default:
		}
		
	}
	
	@Override
	public void setCurrentlyZoomedCard(Card c) throws InvalidActionException {
		getGI().setCurrentlyZoomedCard(c);
	}
	
	public boolean isRectangleMiddleInZone(Point upLeft, Point downRight, Vector preTranslation) {

		Point finalUpLeft = upLeft;
		Point finalDownRight = downRight;

		if(preTranslation != null) {
			finalUpLeft = new Point(upLeft.getX()-preTranslation.getX(), upLeft.getY()-preTranslation.getY());
			finalDownRight = new Point(downRight.getX()-preTranslation.getX(), downRight.getY()-preTranslation.getY());
		}

		return isRectangleMiddleInZone(finalUpLeft, finalDownRight);
	}

	@Override
	public boolean isRectangleInZone(Point upLeft, Point downRight) {		
		return isInZone(upLeft.getX(), upLeft.getY()) && isInZone(downRight.getX(), downRight.getY());
	}

	private List<Transform> getZoomTransforms() {

		List<Transform> result = new ArrayList<>();

		for(Transform t : getTransforms()) {
			if(t instanceof DetailReversedTranslation||
					t instanceof DetailTranslation||
					t instanceof DetailScale) {

				result.add(t);
			}
		}

		return result;
	}

	@Override
	public double getAbsoluteX(double x) {

		double val = 0;

		if(this.isReversed()) {
			val = env.getAbsoluteX(0)-(getLayoutX()+x);
		} else {
			val = env.getAbsoluteX(0)+(getLayoutX()+x);
		}

		return val;
	}

	@Override
	public double getAbsoluteY(double y) {

		double val = 0;

		if(isReversed()) {
			val = env.getAbsoluteY(0)-(getLayoutY()+y);
		} else {
			val = env.getAbsoluteY(0)+(getLayoutY()+y);
		}

		return val;
	}

	public void preCaptureCard(Card c, MouseAction ma, boolean computeTranslation, boolean remote) throws InvalidActionException {

		Point dragAbsPos = new Point(ma.getX(), ma.getY());

		preCaptureCard(c, dragAbsPos, computeTranslation, remote);

	}

	public void preCaptureCard(Card c, Point dragAbsPos, boolean computeTranslation, boolean remote) throws InvalidActionException {



		double scaleFactorX = (getWidth() / env.getWidth())*cardWidhtPercent;
		double scaleFactorY = getHeight() / env.getHeight()*cardHeightPercent;

		double finalWidth = env.getWidth()*scaleFactorX;
		double finalHeight = env.getHeight()*scaleFactorY;

		//After scaling card size, the position of dragged point will
		//not be the same. We need to make a translate.
		Point cardRelPos = c.getReverseRelative(dragAbsPos);
		double finalUsedScaleX = finalWidth / c.getWidth();
		double finalUsedScaleY = finalHeight / c.getHeight();
		Point dragPosAfterScale = c.getAbsolute(MathOps.scale(cardRelPos, new Point(0, 0), finalUsedScaleX, finalUsedScaleY), 0);
		double translateX = dragAbsPos.getX() - dragPosAfterScale.getX();
		double translateY = dragAbsPos.getY() - dragPosAfterScale.getY();
		Vector finalTr = new Vector(translateX, translateY);

		if(c.isEngaged()) {
			//We have to move from new base but calcul are related to old base.
			//This is equivalent to move from old base with calcul a -ENGAGE_MODE_DEGREE base.
			finalTr = MathOps.rotate(-Card.ENGAGED_MODE_DEGREE, finalTr);
		}

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("CaptureZone: "+getName());
			LOGGER.debug("dragPosAfterScale: "+dragPosAfterScale);
			LOGGER.debug("dragAbsPos: "+dragAbsPos);
			LOGGER.debug("Translate computed for dragg/reduce: "+new Point(translateX, translateY));
		}

		if(c.getParentEnv() == this) {

			c.restoreInitSnapshot();
			c.clearSnapshots();

		}

		c.createSnapshot();

		c.setX(c.getX()+finalTr.getX());
		c.setY(c.getY()+finalTr.getY()); 
		c.setWidth(finalWidth);
		c.setHeight(finalHeight);

		if(computeTranslation) {
			this.preCaptureTranslation = new Vector(translateX, translateY);
		}
	}

	public void abordCaptureCard(Card c) throws InvalidActionException {

		c.restoreLastSnapshot();

		this.preCaptureTranslation = null;
	}

	@Override
	public void captureCard(Card c, boolean refresh) throws InvalidActionException {
		captureCard(c, true, refresh);
	}

	public void captureCard(Card c, boolean translationUsed, boolean refresh) throws InvalidActionException {

		Environnement oldEnv = c.getParentEnv();

		Point finalPosition = this.isReversed() ? getReverseRelative(c.getAbsoluteDownRight()) : getReverseRelative(c.getAbsoluteUpLeft());
		Point finalCardMiddle = getReverseRelative(c.getAbsoluteMiddle());

		List<Transform> oldTransforms = new ArrayList<>(c.getTransforms());
		List<Transform> newTransforms = new ArrayList<>();



		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(c.getDisplay());
			LOGGER.debug("oldTransforms: "+c.getTransforms());
		}


		if(oldEnv != null) {
			oldEnv.removeCard(c, refresh);
		}

		c.setEnv(this);

		if(translationUsed) {
			this.preCaptureTranslation = null;
		}

		if(c.isEngaged()) {
			finalPosition = MathOps.rotate(-Card.ENGAGED_MODE_DEGREE, finalCardMiddle, finalPosition);
		}


		for(Transform tr : oldTransforms) {

			if(tr instanceof InitReversedRotation) {

				continue;

			} else if(tr instanceof EDRotation) {

				newTransforms.add(new EDRotation(Card.ENGAGED_MODE_DEGREE, finalCardMiddle.getX(), finalCardMiddle.getY()));

			} else if (tr instanceof Scale) {

				Scale s = (Scale) tr;
				Point absPivot = c.getAbsolute(new Point(s.getPivotX()-c.getX(), s.getPivotY()-c.getY()), 0);

				newTransforms.add(new InitScale(s.getX(), s.getY(), absPivot.getX(), absPivot.getY()));

			}
		}


		if(this.isReversed()) {
			newTransforms.add(new InitReversedRotation(REVERSE_ANGLE, finalCardMiddle.getX(), finalCardMiddle.getY()));			
		}


		c.clearSnapshots();
		c.getTransforms().clear();
		c.getTransforms().addAll(newTransforms);

		c.setX(finalPosition.getX());
		c.setY(finalPosition.getY());

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(c.getDisplay());
			LOGGER.debug("FinalPosition: "+finalPosition);
			LOGGER.debug("newTransforms: "+newTransforms);

		}

		this.getChildren().add(c);

		if(refresh) {
			this.refreshMenuData();
		}

	}

	@Override
	public void removeCard(Card c, boolean refresh) throws InvalidActionException {
		this.getChildren().remove(c);

		if(refresh) {
			this.refreshMenuData();
		}
	}

	@Override
	public Point getInterfaceAbsMiddle() {
		return env.getInterfaceAbsMiddle();
	}

	@Override
	public Point getGameZoneAbsMiddle() {
		return env.getGameZoneAbsMiddle();
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
	public PlayerZone getRelatedPZ() {
		return env.getRelatedPZ();
	}

	@Override
	public Environnement getParentEnv() {
		return this.env;
	}

	@Override
	public double getCornerWidth() {
		return getWidth()*cornerPercent;
	}

	@Override
	public Point getCCZReverseRelative(Point absPoint) {
		return getReverseRelative(absPoint);
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
	public CaptureZone getCurrentCapturingZoneZoomed() {
		return env.getCurrentCapturingZoneZoomed();
	}

	@Override
	public Card getCurrentlyZoomedCard() {
		return env.getCurrentlyZoomedCard();
	}
	
	@Override
	public String getEnvId() {
		return this.name;
	}

	public boolean isFakeZone() {
		return false;
	}
}
