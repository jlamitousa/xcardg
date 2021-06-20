package client.gi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import actions.CaptureZoneAction;
import actions.CardAction;
import actions.HandZoneAction;
import actions.InvalidActionException;
import actions.MouseAction;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import main.Main;
import tranforms.EDRotation;
import tranforms.UserMoveGlobal;
import util.helper.EventHandling;
import util.helper.ListUtil;
import util.math.Point;

public class HandZone extends CaptureZone {

	private static final String LEFT_ARROW_BCK = "/left-arrow.png";
	private static final String RIGHT_ARROW_BCK = "/right-arrow.png";

	private static final double WIDTH_PERCENT = 6.0 / 10.0;
	private static final double HEIGHT_PERCENT = 17.0 / 60.0;

	private static final double CARD_WIDTH_PERCENT = 1.0 / 6.0;
	private static final double CARD_HEIGHT_PERCENT = 15.0 /17.0;

	private static int NB_VISIBLES_CARDS = 5;
	private static double SPACE_BEFORE_FIRST_PERCENT = 0.05;
	private static double ARROW_WIDTH_PERCENT = SPACE_BEFORE_FIRST_PERCENT;
	private static double SPACE_BETWEEN_CARDS_PERCENT = 1.0 / 60.0; 
	private static double SPACE_ON_TOP_PERCENT = 1.0 / 17.0;

	private boolean fakeZone;
	private boolean reverseDisplayOrder;

	private List<Card> cards;
	private HandZoneArrow leftArrow;
	private HandZoneArrow rightArrow;

	private int currentFirstCard;
	private boolean canMove;

	private PlayerZone relatedPZ;
	
	public HandZone(Environnement env, PlayerZone relatedPZ, boolean fake, boolean reverseDisplayOrder) throws InvalidActionException {

		super(env, "HandZone", WIDTH_PERCENT, HEIGHT_PERCENT, 0, CARD_WIDTH_PERCENT, CARD_HEIGHT_PERCENT);

		LOGGER = LoggerFactory.getLogger("HandZone"+(fake ? "Fake" : ""));

		this.fakeZone = fake;
		this.reverseDisplayOrder = reverseDisplayOrder;
		this.relatedPZ = relatedPZ;
		
		this.setLayoutX((env.getWidth() - this.getWidth()) / 2);
		this.setLayoutY((env.getHeight() - this.getHeight()));

		this.leftArrow = new HandZoneArrow(
				this, 
				"leftArrow", 
				0, 
				getHeight() / 2 - getArrowWidth() / 2,
				getArrowWidth(), 
				getArrowWidth());
		
		if(!fake) {
			this.leftArrow.setFill(new ImagePattern(new Image(Main.getResourceDirURL() +  LEFT_ARROW_BCK)));
		}

		this.rightArrow = new HandZoneArrow(
				this, 
				"rightArrow", 
				getWidth() - getArrowWidth(), 
				getHeight() / 2 - getArrowWidth() / 2,
				getArrowWidth(), 
				getArrowWidth());

		if(!fake) {
			this.rightArrow.setFill(new ImagePattern(new Image(Main.getResourceDirURL() +  RIGHT_ARROW_BCK)));
		}

		this.reset();

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug(getDisplay());
		}
	}

	public int getNbCards() {
		return this.cards.size();
	}

	@Override
	public String getEnvId() {
		
		String id = null;
		
		if(relatedPZ != null) {
			id = relatedPZ.getEnvId()+"_HandZone";
		} else {
			id = getParentEnv().getEnvId()+"_HandZone";
		}
		
		return id;
		
	}
	
	public void setRelatedPZ(PlayerZone relatedPZ) {
		this.relatedPZ = relatedPZ;
	}
	
	@Override
	public PlayerZone getRelatedPZ() {
		return this.relatedPZ;
	}
	
	@Override
	public boolean handleMouseClicked(MouseAction ma, boolean remote) {

		boolean sendRemote = true;
		Environnement target = getTargetChildren(ma);
		
		if(imTheTarget(ma) || isTargetingLeftArrow(ma) || isTargetingRightArrow(ma)) {

			if(this.canMove && isTargetingRightArrow(ma)) {

				moveToRight();
				
			} else if (this.canMove && isTargetingLeftArrow(ma)) {

				moveToLeft();
				
			}

		} 

		if(target != null) {
			sendRemote = target.handleMouseClicked(ma, remote) && sendRemote;
		}

		return !remote && sendRemote;

	}

	private boolean isTargetingLeftArrow(MouseAction ma) {
		return this.leftArrow==ma.getTarget()||this.leftArrow.getEnvId().equals(ma.getTargetId());
	}

	private boolean isTargetingRightArrow(MouseAction ma) {
		return this.rightArrow==ma.getTarget()||this.rightArrow.getEnvId().equals(ma.getTargetId());
	}

	private boolean isTargetingHandCard(MouseAction ma) {

		Card c = EventHandling.getTargetAs(ma, Card.class);

		return (c!=null) && this.cards.contains(c);

	}

	private void moveToRight() {

		if(this.cards.size() > NB_VISIBLES_CARDS) {

			boolean canGoRight =  (this.currentFirstCard+NB_VISIBLES_CARDS) < this.getNbCards();

			if(canGoRight) {
				this.currentFirstCard++;
			}
		}

		setPosition();
	}

	private void moveToLeft() {

		if(this.cards.size() > NB_VISIBLES_CARDS) {

			boolean canGoLeft = (this.currentFirstCard > 0);

			if(canGoLeft) {
				this.currentFirstCard--;
			}
		}

		setPosition();
	}

	private List<Card> getVisiblesCards(int index) {

		List<Card> visibles = new ArrayList<>();

		int firstIndex = Math.max(0, index);
		int lastValidIndex = this.cards.size()-1;
		int lastIndex = Math.min(lastValidIndex, firstIndex+(NB_VISIBLES_CARDS-1));
		int nbCard = lastIndex-firstIndex+1;

		for(int i = 0; i < nbCard; i++) {
			visibles.add(this.cards.get(firstIndex+i));
		}

		return visibles;
	}

	public void setPosition() {

		List<Node> children = this.getChildren();
		List<Card> toDisplay = null;

		for(Card c : this.cards) {

			List<Transform> cleanedTransforms = new ArrayList<>();

			c.setCardVisible(false);

			for(Transform t : c.getTransforms()) {

				if(t instanceof UserMoveGlobal) {
					continue;
				}

				if(t instanceof EDRotation) {
					continue;
				}

				cleanedTransforms.add(t);
			}

			c.getTransforms().clear();
			c.getTransforms().addAll(cleanedTransforms);
			c.setEngaged(false);
		}

		toDisplay = getVisiblesCards(this.currentFirstCard);

		children.clear();

		if(!fakeZone) {
			children.add(leftArrow);
		}

		if(this.reverseDisplayOrder) {

			for(int i = 0; i < toDisplay.size(); i++) {

				Card c = toDisplay.get(i);

				c.setY(getHeight()*SPACE_ON_TOP_PERCENT);
				c.setX(getWidth()-getSpaceBeforeFirst()-getCardWith()*(i+1)-getSpaceBetweenCards()*i);
				c.setCardVisible(!fakeZone);
				children.add(c);

			}

		} else {

			for(int i = 0; i < toDisplay.size(); i++) {

				Card c = toDisplay.get(i);

				c.setY(getHeight()*SPACE_ON_TOP_PERCENT);
				c.setX((getCardWith()+getSpaceBetweenCards())*i+getSpaceBeforeFirst());
				c.setCardVisible(!fakeZone);
				children.add(c);

			}

		}

		if(!fakeZone) {
			children.add(rightArrow);
		}

	}

	private double getCardWith() {
		return this.getWidth()*CARD_WIDTH_PERCENT;
	}

	private double getSpaceBeforeFirst() {
		return this.getWidth()*SPACE_BEFORE_FIRST_PERCENT;
	}

	private double getSpaceBetweenCards() {
		return this.getWidth()*SPACE_BETWEEN_CARDS_PERCENT;
	}

	private double getArrowWidth() {
		return this.getWidth()*ARROW_WIDTH_PERCENT;
	}

	@Override
	public double getAbsoluteX(double x) {
		return getLayoutX()+x;
	}

	@Override
	public double getAbsoluteY(double y) {
		return getLayoutY()+y;
	}

	public void captureCard(Card c, boolean refresh) throws InvalidActionException {
		captureCard(c, 0, refresh);
	}
	
	public void captureCard(Card c, int pos, boolean refresh) throws InvalidActionException {

		Environnement parent = c.getParentEnv();

		if(parent != null) {
			parent.removeCard(c, refresh);
		}

		c.setEnv(this);
		c.getTransforms().clear();
		c.clearSnapshots();
		c.setWidthPercent(CARD_WIDTH_PERCENT);
		c.setHeighPercent(CARD_HEIGHT_PERCENT);
		c.setReturnedMode(false);
		
		this.cards.add(pos, c);
		setPosition();

		if(refresh) {
			this.refreshMenuData();
		}
	}

	@Override
	public void removeCard(Card c, boolean refresh) throws InvalidActionException {
		this.cards.remove(c);
		setPosition();
		if(refresh) {
			this.refreshMenuData();
		}
	}

	@Override
	public int getChildrenPosition(Object o) {
		
		int pos = -1;
		
		for(int i = 0; i < this.cards.size(); i++) {
			if(this.cards.get(i)==o) {
				pos = i;
				break;
			}
		}
		
		return pos;
	}
	
	@Override
	public Point getInterfaceAbsMiddle() {
		return getParentEnv().getInterfaceAbsMiddle();
	}

	@Override
	public Point getGameZoneAbsMiddle() {
		return getParentEnv().getGameZoneAbsMiddle();
	}

	@Override
	public CaptureZone getCurrentCapturingZone() {
		return getParentEnv().getCurrentCapturingZone();
	}

	public String serialize() {

		StringBuilder sb = new StringBuilder();

		if(this.cards.size() > 0) {

			for(int i = 0; i < (this.cards.size()-1); i++) {
				sb.append(this.cards.get(i).getCardId()+"=>");
			}

			sb.append(this.cards.get(this.cards.size()-1).getCardId());
		}

		return sb.toString();
	}

	@Override
	public boolean authorizeAction(Card c, CardAction action, boolean remote, boolean forceDontPutInFront) throws InvalidActionException {

		boolean authorize = false;

		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("Card:"+c.getName()+" is trying to do action '"+action.name()+"'");
		}

		switch(action){
		case SELECT: {

			authorize = getParentEnv().authorizeAction(this, HandZoneAction.CARD_SELECT, remote, forceDontPutInFront);
			break;

		}
		case USER_MOVE: {

			authorize = getParentEnv().authorizeAction(this, HandZoneAction.USER_MOVE, remote, forceDontPutInFront);
			break;

		}
		case ENGAGE: {

			authorize = getParentEnv().authorizeAction(this, HandZoneAction.CARD_ENGAGE, remote, forceDontPutInFront);
			break;

		}
		case DEGAGE: {

			authorize = getParentEnv().authorizeAction(this, HandZoneAction.CARD_DEGAGE, remote, forceDontPutInFront);
			break;

		}
		case RETURN: {

			authorize = getParentEnv().authorizeAction(this, HandZoneAction.CARD_RETURN, remote, forceDontPutInFront);
			break;

		}
		case USER_ZOOM: {

			Card currentlyZoomedCard = getCurrentlyZoomedCard();
			
			authorize = getParentEnv().authorizeAction(this, HandZoneAction.CARD_ZOOM, remote, forceDontPutInFront);
			
			if(authorize) {
				
				if(currentlyZoomedCard != null) {
					currentlyZoomedCard.unzoom();
				}
				
				setCurrentlyZoomedCard(c);
				
			}
			
			break;

		}
		case USER_UNZOOM: {

			authorize = getParentEnv().authorizeAction(this, HandZoneAction.CARD_UNZOOM, remote, forceDontPutInFront);
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

	@Override
	public void setCurrentlyZoomedCard(Card c) throws InvalidActionException {
		getGI().setCurrentlyZoomedCard(c);
	}
	
	@Override
	public HandZone getHandZone() {
		return this;
	}

	public void putHandInPickZone(PlayerZone playerZone) throws InvalidActionException {

		PickZone pickZone = playerZone.getPickZone();
		List<Card> cardsCpy = new ArrayList<>(this.cards);

		for(Card c : cardsCpy) {
			pickZone.captureCard(c, false);
		}

		this.currentFirstCard = 0;

		refreshMenuData();
	}
	
	@Override
	public void reset() throws InvalidActionException {
		this.currentFirstCard = 0;
		this.cards = new ArrayList<>();
		this.getChildren().clear();
		this.leftArrow.reset();
		this.rightArrow.reset();
		if(!fakeZone) {
			this.getChildren().add(this.leftArrow);
			this.getChildren().add(this.rightArrow);
		}
		this.canMove = true;
	}

	public boolean isFakeZone() {
		return this.fakeZone;
	}
}
