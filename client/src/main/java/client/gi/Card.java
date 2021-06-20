package client.gi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.CardAction;
import actions.InvalidActionException;
import actions.MouseAction;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import main.Main;
import tranforms.DetailRotation;
import tranforms.DetailScale;
import tranforms.DetailTranslation;
import tranforms.EDRotation;
import tranforms.IUserMove;
import tranforms.IUserMoveGlobal;
import tranforms.InitReversedRotation;
import tranforms.ReversedTranslation;
import tranforms.UserMoveCurrTranReverse;
import tranforms.UserMoveCurrentTranslation;
import tranforms.UserMoveGlobal;
import tranforms.UserMoveGlobalReversed;
import util.helper.ListUtil;
import util.math.MathOps;
import util.math.Point;
import util.math.Vector;

public class Card extends Rectangle implements Environnement, CornerClickableZone {

	private static final String CARD_BCK = "/cards/card-back.png";

	private static final Logger LOGGER = LoggerFactory.getLogger("Card");
	private static final Logger LOGGER_V = LoggerFactory.getLogger("CardVerbose");

	public static final double CORNER_PERCENT_ZOOMED = 2.0 / 10.0;
	public static final double CORNER_PERCENT = 4.0 / 10.0;

	public static final double ZOOMED_CARD_SCALE = 1.8;
	public static final double ENGAGED_MODE_DEGREE = 70;
	public static final double REVERSE_ANGLE = 180;

	private String imgPath;
	private String transformImgPath;

	private String cardId;
	private Environnement env;
	private String name;
	private CardState state;


	private boolean isEngaged;
	private boolean isZoomed;

	private boolean canSwitchEngage;
	private boolean isMoving;

	private boolean isVisible;

	private boolean isReturnedMode;
	private boolean isTransformedMode;




	private Point firstPressedPoint;

	/*
	 * BIG SUBJECT: The card's original displayer environnement is the parentEnv.
	 * When we are zooming a card, its displayer environnement need to inflate in order to
	 * stay a rectangle (Seems to be Java FX architecture). This is not visible in the interface 
	 * but cause problem with click target of MouseEvent class. When the card env is the HandZone, zooming 
	 * the card means to moving it above the handzone. When you click then on the commandZone card for 
	 * switching zoom, you got in log the the click target is the HandZone ! That's because it inflate ..
	 * In order to avoid this problem, we gonna change the displayer env. Most of the time, we gonna use 
	 * the pile zone make the inflate just as big as the card is (because the pileZone is little and game-zone-central).
	 */
	Environnement zoomDisplayer;

	private double firstTimeWidth;
	private double firstTimeHeight;
	private List<CardSnapshot> snapshots;


	public Card(String id, String name, String imgPath, String transformImgPath) {

		this.imgPath = imgPath;
		this.transformImgPath = transformImgPath;
		this.cardId = id;
		this.env = null;
		this.name = name;
		this.state = new CardState(name);

		setFill(new ImagePattern(new Image(imgPath)));

		this.firstTimeWidth = -1;
		this.firstTimeHeight = -1;
		this.snapshots = new ArrayList<>();

		this.isVisible = false;		
		this.isMoving = false;
		this.isZoomed = false;
		this.isEngaged = false;
		this.canSwitchEngage = true;
		this.isReturnedMode = false;
		this.isTransformedMode = false;

	}

	public String getCardId() {
		return this.cardId;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public void setEnv(Environnement env) {
		this.env = env;
	}

	public Environnement getParentEnv() {
		return this.env;
	}

	public void setReturnedMode(boolean isReturnedMode) {

		String img = this.isTransformedMode ? transformImgPath : imgPath;

		this.isReturnedMode = isReturnedMode;

		if(this.isReturnedMode) {
			setFill(new ImagePattern(new Image(Main.getResourceUrl(CARD_BCK))));
		} else {
			setFill(new ImagePattern(new Image(imgPath)));
		}

	}

	public boolean isReturnedMode() {
		return isReturnedMode;
	}

	public void setTransformedMode(boolean isTransformedMode) {

		this.isTransformedMode = isTransformedMode;

		setReturnedMode(this.isReturnedMode);
	}

	/**
	 * 
	 * @param isMovingRemote
	 * @param preTranslation when changing card size it can be, at the end, be outsize zone. 
	 *                       This variable ensure it stay in the zone
	 * @return
	 */
	public boolean isOutsideVisibleZone(boolean isRemoteMove, Vector preTranslation) {
		return isRemoteMove && !getGZ().isRectangleInZone(getAbsoluteUpLeft(), getAbsoluteDownRight(), preTranslation);
	}

	public boolean isEngaged() {
		return this.isEngaged;
	}

	public void setEngaged(boolean isEngaged) {
		this.isEngaged = isEngaged;
	}

	public boolean isZoomed() {
		return this.isZoomed;
	}

	public boolean isToken() {
		return this.name.indexOf("TOKEN") > -1;
	}

	@Override
	public boolean isReversed() {
		return haveTransform(InitReversedRotation.class);
	}

	public void setCardVisible(boolean isVisible) {

		this.isVisible = isVisible;

		if(!isVisible) {
			setFill(Color.TRANSPARENT);
		} else {
			setReturnedMode(this.isReturnedMode);
		}
	}

	public void setWidthPercent(double widthPercent) {
		this.setWidth(env.getWidth() * widthPercent);
	}

	public void setHeighPercent(double heighPercent) {
		this.setHeight(env.getHeight() * heighPercent);
	}

	public void createSnapshot() {

		CardSnapshot cs = new CardSnapshot();

		if(this.firstTimeWidth < 0) {
			this.firstTimeWidth = getWidth();
		}

		if(this.firstTimeHeight < 0) {
			this.firstTimeHeight = getHeight();
		}

		cs.setSavedX(getX());
		cs.setSavedY(getY());
		cs.setSavedWidth(getWidth());
		cs.setSavedHeight(getHeight());
		cs.setSavedEnv(this.env);
		cs.setSavedChildPos(this.env.getChildrenPosition(this));

		snapshots.add(cs);

		if(LOGGER_V.isDebugEnabled()) {
			LOGGER_V.debug("Card snapshot created ("+snapshots.size()+"): ");
			LOGGER_V.debug("Saved values: X="+cs.getSavedX()+", Y="+cs.getSavedY()+", width="+cs.getSavedWidth()+", height="+cs.getSavedHeight());
			LOGGER_V.debug("Saved values: FTW="+this.firstTimeWidth+", FTH="+this.firstTimeHeight);
		}

	}

	public CardSnapshot popSnapshot() {

		CardSnapshot cs = null;

		if(!snapshots.isEmpty()) {
			cs = snapshots.get(snapshots.size()-1);
			snapshots.remove(cs);
		}

		return cs;
	}

	public void restore(CardSnapshot cs) {

		if(cs != null) {

			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("Restored values: X="+cs.getSavedX()+", Y="+cs.getSavedY()+", width="+cs.getSavedWidth()+", height="+cs.getSavedHeight());
			}

			setX(cs.getSavedX());
			setY(cs.getSavedY());
			setWidth(cs.getSavedWidth());
			setHeight(cs.getSavedHeight());

		}

	}

	public void restoreInitSnapshot() {

		CardSnapshot cs = null;

		if(!snapshots.isEmpty()) {
			cs = snapshots.get(0);
		}

		restore(cs);

	}

	public void restoreLastSnapshot() {

		CardSnapshot cs = popSnapshot();


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Restoring card snapshot (stock_size="+snapshots.size()+")");
		}


		restore(cs);

	}

	public void clearSnapshots() {
		this.snapshots.clear();
	}

	public Point computeFinalActionPosition(MouseAction ma, boolean remote) {
		return computeFinalActionPosition(new Point(ma.getX(), ma.getY()), remote, ma.getSourceUserId());
	}

	public Point computeFinalActionPosition(Point pos, boolean remote, int sourceId) {

		Point finalPos = pos;

		if(remote) {

			//The opponent made an action on me meaning i'm on the game zone (=> PZ1 is not null).

			PlayerZone pz = getRelatedPZ();
			int userId = getGI().getGmZone().getPlayerZone1().getUserId();
			Point pivot = null;

			if(userId==1 && sourceId==2 || userId==2 && sourceId==1 || userId==3 && sourceId==4 || userId==4 && sourceId==3) {
				pivot = getAbsoluteMiddle();
			} else if (userId==1 && sourceId==3 || userId==2 && sourceId==4 || userId==3 && sourceId==1 || userId==4 && sourceId==2) {
				pivot = new Point(pos.getX(), getAbsoluteMiddle().getY());
			} else if (userId==1 && sourceId==4  || userId==2 && sourceId==3 || userId==3 && sourceId==2  || userId==4 && sourceId==1) {
				pivot = new Point(getAbsoluteMiddle().getX(), pos.getY());
			}

			if(pivot != null) {
				finalPos = MathOps.rotate(REVERSE_ANGLE, pivot, pos);
			} else {
				finalPos = pos;
			}

		}

		return finalPos;

	}

	@Override
	public boolean handleMouseClicked(MouseAction ma, boolean remote) {

		boolean sendRemote = true;

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("handleMouseClicked");
		}

		try {

			if(!this.isZoomed) {

				if (imTheTarget(ma)) {

					boolean doubleClicked = ma.isDoubleClicked();
					Point finalAbsPos = computeFinalActionPosition(ma, remote);
					boolean isUpLeftCorner = isUpLeftCorner(finalAbsPos);
					boolean isUpRightCorner = isUpRightCorner(finalAbsPos);
					boolean isDownLeftCorner = isDownLeftCorner(finalAbsPos);
					boolean zoomFromNormal = 
							!this.isZoomed && 
							doubleClicked && 
							!isDownLeftCorner && 
							!isUpLeftCorner && 
							!isUpRightCorner && 
							!remote;
					boolean zoomFromZoom = 
							!doubleClicked && 
							getCurrentlyZoomedCard() != null &&
							getCurrentlyZoomedCard() != this &&
							!remote;

					if(!remote) {
						if(!this.isReturnedMode) {
							displayCardState(this.state);
						} else {
							displayCardState(null);
						}
					}

					if (zoomFromNormal || zoomFromZoom) {

						if(env.authorizeAction(this, CardAction.USER_ZOOM, remote)){

							blockEvents();
							zoom();
							sendRemote = false;

							env.notifyActionFinish(this, CardAction.USER_ZOOM);

						} 

					} else if(!doubleClicked) {

						if (this.canSwitchEngage && this.isEngaged && isUpLeftCorner && env.authorizeAction(this, CardAction.DEGAGE, remote)) {

							degage();

						} else if (this.canSwitchEngage && !this.isEngaged && isUpRightCorner && env.authorizeAction(this, CardAction.ENGAGE, remote)) {

							engage();

						} else if (isDownLeftCorner && env.authorizeAction(this, CardAction.RETURN, remote)) {

							setReturnedMode(!this.isReturnedMode);

						}

					}

				}

			}

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		return !remote && sendRemote;

	}

	@Override
	public boolean handleMousePressed(MouseAction ma, boolean remote) {

		boolean sendRemote = true;

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("handleMousePressed: "+getDisplay());
		}

		try {

			if (!this.isZoomed) {

				if(imTheTarget(ma) && env.authorizeAction(this, CardAction.SELECT, remote)) {

					Point finalAbsPos = computeFinalActionPosition(ma, remote);
					Point clickAbsPos = new Point(ma.getX(), ma.getY()); //We still need it for mvt relativity.

					if (!isUpLeftCorner(finalAbsPos) && !isUpRightCorner(finalAbsPos) && env.authorizeAction(this, CardAction.USER_MOVE, remote)) {
						this.isMoving = true;
						this.firstPressedPoint = clickAbsPos;
					}

				}

			}

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		return !remote && sendRemote;

	}

	@Override
	public boolean handleMouseDragged(MouseAction ma, boolean remote) {

		boolean sendRemote = true;

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("handleMouseDragged");
		}

		try {

			if (!this.isZoomed) {

				if(imTheTarget(ma) && this.firstPressedPoint != null && env.authorizeAction(this, CardAction.USER_MOVE, remote)) {

					Point clickAbsPos = new Point(ma.getX(), ma.getY());
					CaptureZone cz = getCurrentCapturingZone();
					Vector preCaptureTranslation = cz != null ? cz.getPreCaptureTranslation() : null;

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Dragging"+(remote? "(remote)":"")+": " + clickAbsPos+ ", "+getDisplay());
					}

					move(clickAbsPos);

					if(isOutsideVisibleZone(remote, preCaptureTranslation)) {
						setCardVisible(false);
					} else {
						setCardVisible(true);
					}

				}

			}

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action ", iae);

		}

		return !remote && sendRemote;

	}

	@Override
	public boolean handleMouseReleased(MouseAction ma, boolean remote) {

		boolean sendRemote = true;
		CaptureZone cz = getCurrentCapturingZone();
		Vector preCaptureTranslation = cz != null ? cz.getPreCaptureTranslation() : null;

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("handleMouseReleased");
		}

		try {

			if(imTheTarget(ma)) {

				if(this.isZoomed) {

					if(env.authorizeAction(this, CardAction.USER_UNZOOM, remote)) {
						unzoom();
						unblockEvents();
						sendRemote = false;

						env.notifyActionFinish(this, CardAction.USER_UNZOOM);
					}

				} else { 

					if(this.firstPressedPoint != null && env.authorizeAction(this, CardAction.USER_MOVE, remote)) {

						clearAfterMove();

						this.isMoving = false;
						this.firstPressedPoint = null;
					}

					if(isOutsideVisibleZone(remote, preCaptureTranslation)) {
						setVisible(false);
					} else {
						setVisible(true);
					}

					getGI().setChildrenPosition();

				}

			}

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		return !remote && sendRemote;

	}

	private void zoom() {

		Point targetAbs = getGameZoneAbsMiddle();
		Point targetRel = null;
		Point absPos = isReversed() ? getAbsoluteDownRight() : getAbsoluteUpLeft();
		Point relPos = null;
		Point absMiddle = getAbsoluteMiddle();
		Point relMiddle = null;
		Point zoomDispMiddle = null;
		boolean isInsideZoomedPlayerZone = false;

		PlayerZone pz = getCurrentlyZoomedPZ();
		DetailScale pzScale = pz != null ? (DetailScale) pz.foundTransform(DetailScale.class) : null;
		Point scaleInfo = new Point(this.firstTimeWidth / getWidth(), this.firstTimeHeight / getHeight());
		boolean shouldReverse = false;

		DetailTranslation cdt = null;
		Vector transition = null;
		DetailScale cds = null;


		createSnapshot();

		this.zoomDisplayer = getGZ().getPileZone();
		isInsideZoomedPlayerZone = (pzScale != null);

		if(isInsideZoomedPlayerZone) {

			scaleInfo.setX(scaleInfo.getX()*(1.0 / pzScale.getX()));
			scaleInfo.setY(scaleInfo.getY()*(1.0 / pzScale.getY()));
			this.zoomDisplayer = pz;
			targetAbs = pz.getAbsoluteMiddle();

		}

		shouldReverse = !isReversed() && this.zoomDisplayer.isReversed() || isReversed() && !this.zoomDisplayer.isReversed();
		this.setWidth(this.getWidth()*scaleInfo.getX());
		this.setHeight(this.getHeight()*scaleInfo.getY());

		zoomDispMiddle = getReverseRelative(this.zoomDisplayer.getAbsoluteMiddle());
		getGZ().putInFront(this.zoomDisplayer);

		relPos = this.zoomDisplayer.getReverseRelative(absPos);
		targetRel = this.zoomDisplayer.getReverseRelative(targetAbs);
		relMiddle = this.zoomDisplayer.getReverseRelative(absMiddle);

		if(this.isEngaged) {

			relPos = MathOps.rotate(-ENGAGED_MODE_DEGREE, relMiddle, relPos);
			targetRel = MathOps.rotate(-ENGAGED_MODE_DEGREE, relMiddle, targetRel);

			transition = new Vector(
					targetRel.getX() - (relPos.getX() + getWidth()), 
					targetRel.getY() - (relPos.getY() + getHeight()));

		} else {

			transition = new Vector(
					targetRel.getX() - (relPos.getX() + getWidth()), 
					targetRel.getY() - (relPos.getY() + getHeight()));

		}

		this.setX(relPos.getX());
		this.setY(relPos.getY());

		if(this.env==getHandZone()) {
			try {
				((HandZone)this.env).removeCard(this, false);
			} catch(InvalidActionException iae) {
				LOGGER.error("Invalid action", iae);
			}
		} else {
			this.env.getChildren().remove(this);
		}

		this.zoomDisplayer.getChildren().add(this);

		cdt = new DetailTranslation(transition.getX(), transition.getY());
		cds = new DetailScale(ZOOMED_CARD_SCALE, ZOOMED_CARD_SCALE, getX(), getY());

		this.getTransforms().add(cdt);
		this.getTransforms().add(cds);


		if(isEngaged) {

			EDRotation edr = (EDRotation) foundTransform(EDRotation.class);

			if(edr != null) {
				this.getTransforms().add(0, new DetailRotation(-ENGAGED_MODE_DEGREE, edr.getPivotX(), edr.getPivotY()));
				this.getTransforms().add(new DetailRotation(ENGAGED_MODE_DEGREE, getCenterX(), getCenterY()));
			}

			this.getTransforms().add(new DetailRotation(-ENGAGED_MODE_DEGREE, getCenterX(), getCenterY()));
		}
		if(shouldReverse) {

			InitReversedRotation irr = (InitReversedRotation) foundTransform(InitReversedRotation.class);

			if(irr != null) {
				this.getTransforms().add(0, new DetailRotation(-REVERSE_ANGLE, irr.getPivotX(), irr.getPivotY()));
				this.getTransforms().add(new DetailRotation(REVERSE_ANGLE, getCenterX(), getCenterY()));
			}

			this.getTransforms().add(new DetailRotation(REVERSE_ANGLE, getCenterX(), getCenterY()));
		}

		this.isZoomed = true;
		this.canSwitchEngage = false;

	}

	public void unzoom() {

		List<Transform> oldTransforms = new ArrayList<Transform>();
		List<Transform> newTransforms = this.getTransforms();
		CardSnapshot cs = popSnapshot();

		oldTransforms.addAll(newTransforms);

		newTransforms.clear();

		for (Transform t : oldTransforms) {

			if (t instanceof DetailTranslation || t instanceof DetailScale || t instanceof DetailRotation) {
				continue;
			}

			newTransforms.add(t);

		}

		restore(cs);

		if(cs.getSavedEnv() instanceof HandZone) {
			try {
				((HandZone)cs.getSavedEnv()).captureCard(this, cs.getSavedChildPos(), false);
			} catch(InvalidActionException iae) {
				LOGGER.error("Invalid action", iae);
			}
		} else {
			this.env.getChildren().remove(this);
			this.env.getChildren().add(cs.getSavedChildPos(), this);
		}

		this.isZoomed = false;
		this.canSwitchEngage = true;

	}

	public void engage() {
		this.getTransforms().add(new EDRotation(ENGAGED_MODE_DEGREE, this.getCenterX(), this.getCenterY()));
		this.isEngaged = true;
	}

	public void degage() {

		List<Transform> transforms = new ArrayList<Transform>();
		boolean edRotationSeen = false;

		transforms.addAll(this.getTransforms());
		this.getTransforms().clear();

		for (Transform t : transforms) {
			if (!(t instanceof EDRotation)) {
				this.getTransforms().add(t);
			}
		}

		this.isEngaged = false;
	}

	private void move(Point to) {

		Point fpp = env.getReverseRelative(this.firstPressedPoint);
		Point tmpTo = env.getReverseRelative(to);

		Point relFrom = null;
		Point relTo = null;


		popLastAsTransMove();


		if(this.isEngaged) {
			//We have to move from new base but calcul are related to old base.
			//This is equivalent to move from old base with calcul a -ENGAGE_MODE_DEGREE base.
			fpp = MathOps.rotate(-ENGAGED_MODE_DEGREE, fpp);
			tmpTo = MathOps.rotate(-ENGAGED_MODE_DEGREE, tmpTo);
		}


		if(env.isReversed()) {

			relFrom = new Point(-fpp.getX(), -fpp.getY());
			relTo = new Point(-tmpTo.getX(), -tmpTo.getY());

			this.getTransforms().add(new UserMoveCurrTranReverse(relFrom, relTo));

		} else {

			relFrom = fpp;
			relTo = tmpTo;

			this.getTransforms().add(new UserMoveCurrentTranslation(relFrom, relTo));
		}


		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("All mvts: "+getTransforms());
		}
	}

	private IUserMoveGlobal getUserMove() {

		for (Transform t : this.getTransforms()) {
			if (t instanceof IUserMoveGlobal) {
				return (IUserMoveGlobal) t;
			}
		}

		return new UserMoveGlobal();
	}

	private void clearAfterMove() {

		Point relFirstP = getReverseRelative(this.firstPressedPoint);

		IUserMove firstBeforeRotation = null;
		IUserMove lastBeforeRotation = null;
		IUserMove firstAfterRotation = null;
		IUserMove lastAfterRotation = null;
		Vector globalCurrentMove = null;

		double historicalGlobalMoveX = 0;
		double historicalGlobalMoveY = 0;
		Vector globalHistoricMove = null;

		Vector historicalGlobalMove = new Vector(0, 0);
		Vector globalMoveAfterRotation = new Vector(0, 0);
		Vector globalMove = null;

		boolean isAfterRotation = false;

		List<Transform> transforms = new ArrayList<Transform>();

		transforms.addAll(this.getTransforms());
		this.getTransforms().clear();

		for (Transform t : transforms) {

			if (t instanceof UserMoveGlobal || t instanceof UserMoveGlobalReversed) {

				Translate tr = (Translate) t;

				//When in reversed mode, the end of this function goint to reverse this UserMoveGlobal.
				//As this UserMoveGlobal come from last call, it's already reversed .. we don't need to do it again.
				//In order to cancel this, we reserve one here, meaning twice globally in this function => cancelled.
				if(env.isReversed()) {
					historicalGlobalMoveX -= tr.getX();
					historicalGlobalMoveY -= tr.getY();
				} else {
					historicalGlobalMoveX += tr.getX();
					historicalGlobalMoveY += tr.getY();
				}

			} else if (t instanceof IUserMove) {

				if (!isAfterRotation) {

					if (firstBeforeRotation == null) {

						firstBeforeRotation = (IUserMove) t;
						lastBeforeRotation = (IUserMove) t;

					} else {

						lastBeforeRotation = (IUserMove) t;

					}

				} else if (firstAfterRotation == null) {

					firstAfterRotation = (IUserMove) t;
					lastAfterRotation = (IUserMove) t;

				} else {

					lastAfterRotation = (IUserMove) t;

				}

			} else if (t instanceof EDRotation) {

				isAfterRotation = true;
				this.getTransforms().add(t);

			} else {

				this.getTransforms().add(t);
			}
		}

		// Historical GLobal User Move
		globalHistoricMove = new Vector(historicalGlobalMoveX, historicalGlobalMoveY);

		// Current User Move
		if (firstBeforeRotation != null && lastBeforeRotation != null) {
			historicalGlobalMove = new Vector(firstBeforeRotation.getFrom(), lastBeforeRotation.getTo());
		}
		if (firstAfterRotation != null && lastAfterRotation != null) {
			globalMoveAfterRotation = new Vector(firstAfterRotation.getFrom(), lastAfterRotation.getTo());
			globalMoveAfterRotation = MathOps.rotateVector(ENGAGED_MODE_DEGREE, globalMoveAfterRotation);
		}
		globalCurrentMove = Vector.sum(historicalGlobalMove, globalMoveAfterRotation);

		globalMove = Vector.sum(globalHistoricMove, globalCurrentMove);

		//When reversed, adding at place 0 means do it before reverse ..
		//that's why we have to reverse coordinate.
		if(env.isReversed()) {
			this.getTransforms().add(0, new UserMoveGlobalReversed(-globalMove.getX(), -globalMove.getY()));
		} else {
			this.getTransforms().add(0, new UserMoveGlobal(globalMove.getX(), globalMove.getY()));
		}

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Transforms after clearing moves: "+getTransforms());
		}

	}

	private double getCenterX() {
		return this.getX() + getWidth() / 2;
	}

	private double getCenterY() {
		return this.getY() + getHeight() / 2;
	}

	public double getCornerWidth() {
		return this.getWidth() * (shouldUseCornerZoomed() ? CORNER_PERCENT_ZOOMED : CORNER_PERCENT);
	}

	public boolean shouldUseCornerZoomed() {
		PlayerZone pz = getRelatedPZ();
		return (getParentEnv() instanceof HandZone) ||  (pz != null && pz.foundTransform(DetailScale.class) != null);
	}

	@Override
	public double getAbsoluteX(double x) {

		double val = 0;

		if(env != null) {

			if(env.isReversed()) {
				val = env.getAbsoluteX(0) - (getX() + x);
			} else {
				val = env.getAbsoluteX(0) + getX() + x;
			}

		}

		return val;
	}

	@Override
	public double getAbsoluteY(double y) {

		double val = 0;

		if(env != null) {

			if(env.isReversed()) {
				val = env.getAbsoluteY(0) - (getY() + y);
			} else {
				val = env.getAbsoluteY(0) + getY() + y;
			}

		}

		return val;
	}

	private IUserMove popLastAsTransMove() {

		IUserMove lastAsMove = null;
		List<Transform> transforms = this.getTransforms();
		int size = transforms.size();

		if (size > 0 && (transforms.get(size - 1) instanceof IUserMove)) {

			lastAsMove = (IUserMove) transforms.get(size - 1);

		}

		if(lastAsMove != null) {
			transforms.remove(lastAsMove);
		}

		return lastAsMove;
	}

	@Override
	public Point applyAbsoluteTransformsRec(Point absPoint, List<Transform> transforms, int recursionLVL) {

		boolean initReversedRotDone = false;
		boolean edRotationDone = false;

		if(LOGGER_V.isDebugEnabled()) {
			LOGGER_V.debug(getSpacing(recursionLVL)+"applyAbsoluteTransforms (start): "+absPoint);
		}

		Point finalP = absPoint;
		List<Transform> applied = new ArrayList<Transform>();

		for(Transform tr : transforms) {

			if(tr instanceof Rotate) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Rotation .. ");
				}

				Rotate r = (Rotate) tr;
				Point pivot = getAbsolute(new Point(r.getPivotX()-getX(), r.getPivotY()-getY()), applied, recursionLVL+1);

				finalP = MathOps.rotate(r.getAngle(), pivot, finalP);

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP: "+finalP);
				}

				applied.add(r);

				initReversedRotDone = initReversedRotDone || (r instanceof InitReversedRotation);
				edRotationDone = edRotationDone || (r instanceof EDRotation);

			} else if (tr instanceof ReversedTranslation) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"ReversedTranslation .. ");
				}

				Translate t = (Translate) tr;
				Vector finalTr = new Vector(t.getX(), t.getY());


				if(edRotationDone) {
					finalTr = MathOps.rotate(ENGAGED_MODE_DEGREE, finalTr);
				}
				if(!initReversedRotDone) {
					finalTr = MathOps.rotate(REVERSE_ANGLE, finalTr);
				}


				finalP = MathOps.translate(finalP, finalTr);

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP: "+finalP);
				}

				applied.add(t);

			} else if (tr instanceof Translate) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Translate .. ");
				}

				Translate t = (Translate) tr;
				Vector finalTr = new Vector(t.getX(), t.getY());

				if(edRotationDone) {
					finalTr = MathOps.rotate(ENGAGED_MODE_DEGREE, finalTr);
				}
				if(initReversedRotDone) {
					finalTr = MathOps.rotate(180, finalTr);
				}

				finalP = MathOps.translate(finalP, finalTr);

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP: "+finalP);
				}

				applied.add(t);

			} else if(tr instanceof Scale) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Scale .. ");
				}

				Scale s = (Scale) tr;
				Point pivot = getAbsolute(new Point(s.getPivotX()-getX(), s.getPivotY()-getY()), applied, recursionLVL+1);

				finalP = MathOps.scale(finalP, pivot, s.getX(), s.getY());

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP: "+finalP);
				}

				applied.add(s);
			}
		}

		if(LOGGER_V.isDebugEnabled()) {
			LOGGER_V.debug(getSpacing(recursionLVL)+"applyAbsoluteTransforms (end): "+absPoint+" => "+finalP);
		}

		return finalP;
	}

	@Override
	public Point applyReverseTransformsRec(Point absPoint, List<Transform> transforms, int recursionLVL) {

		boolean initReversedRotDone = false;
		boolean edRotationDone = false;

		if(LOGGER_V.isDebugEnabled()) {
			LOGGER_V.debug(getSpacing(recursionLVL)+"(start) applyReverseTransforms to "+absPoint);
		}

		Point finalP = absPoint;
		List<Transform> reversedOrderTransforms = ListUtil.reverse(transforms);
		List<Transform> applied = new ArrayList<>();

		for(Transform tr : reversedOrderTransforms) {

			if(tr instanceof Rotate) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Rotation ("+tr.getClass().getName()+")..");
				}

				Rotate r = (Rotate) tr;
				Point pivot = applyReverseTransformsRec(getAbsolute(new Point(r.getPivotX()-getX(), r.getPivotY()-getY()), recursionLVL+1), ListUtil.reverse(applied), recursionLVL+1);

				finalP = MathOps.rotate(-r.getAngle(), pivot, finalP);

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP .."+finalP);
				}

				applied.add(r);

				initReversedRotDone = initReversedRotDone || (r instanceof InitReversedRotation);
				edRotationDone = edRotationDone || (r instanceof EDRotation);

			} else if (tr instanceof ReversedTranslation) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"ReversedTranslation ..");
				}

				Translate t = (Translate) tr;
				Vector finalTr = new Vector(-t.getX(), -t.getY());

				if(this.isEngaged && !edRotationDone) {
					finalTr = MathOps.rotate(ENGAGED_MODE_DEGREE, finalTr);
				}
				if(this.isReversed() && initReversedRotDone) {
					finalTr = MathOps.rotate(REVERSE_ANGLE, finalTr);
				}

				finalP = MathOps.translate(finalP, finalTr);

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP .."+finalP);
				}

				applied.add(t);

			} else if (tr instanceof Translate) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Translate ..");
				}

				Translate t = (Translate) tr;
				Vector finalTr = new Vector(-t.getX(), -t.getY());

				if(this.isEngaged && !edRotationDone) {
					finalTr = MathOps.rotate(ENGAGED_MODE_DEGREE, finalTr);
				}
				if(initReversedRotDone) {
					finalTr = MathOps.rotate(REVERSE_ANGLE, finalTr);
				}

				finalP = MathOps.translate(finalP, finalTr);

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP .."+finalP);
				}

				applied.add(t);

			} else if(tr instanceof Scale) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Scale ..");
				}

				Scale s = (Scale) tr;
				Point pivot = applyReverseTransformsRec(getAbsolute(new Point(s.getPivotX()-getX(), s.getPivotY()-getY()), recursionLVL+1), ListUtil.reverse(applied), recursionLVL+1);

				finalP = MathOps.scale(finalP, pivot, 1 / s.getX(), 1 / s.getY());

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP .."+finalP);
				}

				applied.add(s);
			}
		}

		if(LOGGER_V.isDebugEnabled()) {
			LOGGER_V.debug(getSpacing(recursionLVL)+"(end) applyReverseTransforms to "+absPoint+" => "+finalP);
		}

		return finalP;
	}

	@Override
	public String getDisplay() {

		String display = Environnement.super.getDisplay() + ", ";

		display += "(x="+getX()+"; y="+getY()+"), ";
		display += "isEngaged: " + this.isEngaged + ", ";
		display += "isZoomed: " + this.isZoomed + ", ";
		display += "canSwitchEngage: " + this.canSwitchEngage + ", ";
		display += "isMoving: " + this.isMoving + ", ";
		display += "isVisible: " + this.isVisible + ", ";
		display += "isReturnedMode: " + this.isReturnedMode + ", ";
		display += "isTransformedMode: " + this.isTransformedMode;

		return display;
	}

	@Override
	public List<Node> getChildren() {
		return new ArrayList<>();
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
	public Point getCCZReverseRelative(Point absPoint) {
		return getReverseRelative(absPoint);
	}


	/**
	 * 
	 * @return width after all transforms application
	 */
	private double getEffectiveWidth() {

		Point upLeft = getAbsoluteUpLeft();
		Point downRight = getAbsoluteDownRight();

		if(this.isEngaged) {
			upLeft = MathOps.rotate(-ENGAGED_MODE_DEGREE, upLeft);
			downRight = MathOps.rotate(-ENGAGED_MODE_DEGREE, downRight);
		}

		return downRight.getX()-upLeft.getX();
	}

	/**
	 * 
	 * @return height after all transforms application
	 */
	private double getEffectiveHeight() {

		Point upLeft = getAbsoluteUpLeft();
		Point downRight = getAbsoluteDownRight();

		if(this.isEngaged) {
			upLeft = MathOps.rotate(-ENGAGED_MODE_DEGREE, upLeft);
			downRight = MathOps.rotate(-ENGAGED_MODE_DEGREE, downRight);
		}

		return downRight.getY()-upLeft.getY();
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
	public void reset() throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}

	@Override
	public String getEnvId() {
		return getCardId();
	}
}
