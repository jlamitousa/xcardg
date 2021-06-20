package client.gi;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.LoggerFactory;

import actions.CaptureZoneAction;
import actions.InvalidActionException;
import actions.MouseAction;
import tranforms.InitReversedRotation;
import util.helper.EventHandling;
import util.math.Point;

public abstract class Pile extends CaptureZone {

	private List<Card> cards;
	public static final double REVERSE_ANGLE = 180;

	private double cardWidhtPercent;
	private double cardHeightPercent;

	private boolean showMode;
	private boolean showFirtOnlyMode;
	private boolean isZoomed;


	public Pile(
			Environnement env, 
			String name, 
			double widthPercent, 
			double heightPercent,
			double cornerPercent,
			double cardWidhtPercent,
			double cardHeightPercent) {

		super(env, name, widthPercent, heightPercent, cornerPercent, cardWidhtPercent, cardHeightPercent);

		LOGGER = LoggerFactory.getLogger("Pile");
		
		this.cardWidhtPercent = cardWidhtPercent;
		this.cardHeightPercent = cardHeightPercent;

		this.cards = new ArrayList<Card>();

		this.showFirtOnlyMode = false;
		this.showMode = true;
		this.isZoomed = false;
	}

	public boolean isShowMode() {
		return showMode;
	}

	public void setShowMode(boolean showMode) {
		this.showMode = showMode;
	}

	public boolean isShowFirtOnlyMode() {
		return showFirtOnlyMode;
	}

	public void setShowFirtOnlyMode(boolean showFirtOnlyMode) {
		this.showFirtOnlyMode = showFirtOnlyMode;
	}

	public int getPileSize() {
		return this.cards.size();
	}

	@Override
	public boolean handleMouseClicked(MouseAction ma, boolean remote) {

		boolean sendRemote = true;
		Environnement target = null; 

		try {

			target = getTargetChildren(ma);

			boolean doubleClicked = ma.isDoubleClicked();
			boolean isUpRight = isReversed() ? isDownLeftCorner(ma) : isUpRightCorner(ma);
			Card targetAsCard = EventHandling.getTargetAs(ma, Card.class);

			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(getDisplay());
				LOGGER.debug("doubleClicked: "+doubleClicked+", isUpRight: "+isUpRight+", targetAsCard :"+(targetAsCard == null ? "<no_card>" : targetAsCard.getDisplay()));
			}

			if(doubleClicked) {

				if(!remote && isUpRight && !this.isZoomed && getParentEnv().authorizeAction(this, CaptureZoneAction.ZOOM, remote)) {

					displayPile(this);
					sendRemote = false;

				} else if (!remote && isUpRight && this.isZoomed && getParentEnv().authorizeAction(this, CaptureZoneAction.UNZOOM, remote)) {

					//TODO Not reachable code
					displayGame();
					sendRemote = false;

				} else if (target != null) {

					if((targetAsCard != null) && (this instanceof PickZone)) {

						if(!remote) {
						
							if(LOGGER.isDebugEnabled()) {
								LOGGER.debug("Capturing '"+targetAsCard.getName()+"' in local hand .. ");
							}
							
							getHandZone().captureCard(targetAsCard, true);
						
						} else {
							
							if(LOGGER.isDebugEnabled()) {
								LOGGER.debug("Capturing '"+targetAsCard.getName()+"' in remote hand .. ");
							}
							
							getGZ().findPlayerZone(ma.getSourceUserId()).captureCardInOpponentHandZone(targetAsCard);
							
						}

					} else { 

						sendRemote = target.handleMouseClicked(ma, remote) && sendRemote;

					}

				}

			} else if(target != null) {

				sendRemote = target.handleMouseClicked(ma, remote) && sendRemote;

			}

		} catch(InvalidActionException iae) {
			LOGGER.error("Invalid action", iae);
		}

		return !remote && sendRemote;

	}

	@Override
	public void captureCard(Card c, boolean refresh) throws InvalidActionException {
		Environnement oldEnv = c.getParentEnv();
		Point finalCardMiddle = null;

		if(oldEnv != null) {
			oldEnv.removeCard(c, refresh);
		}
		
		c.setEnv(this);
		this.setPreCaptureTranslation(null);
		c.getTransforms().clear();
		c.clearSnapshots();

		if(!showMode && !showFirtOnlyMode) {
			c.setReturnedMode(true);
		} else {
			c.setReturnedMode(false);
		}

		c.setWidthPercent(this.cardWidhtPercent);
		c.setHeighPercent(this.cardHeightPercent);
		c.setX((getWidth()-c.getWidth()) / 2);
		c.setY((getHeight()-c.getHeight()) / 2);
		finalCardMiddle = getReverseRelative(c.getAbsoluteMiddle());

		if(this.isReversed()) {
			c.getTransforms().add(new InitReversedRotation(REVERSE_ANGLE, finalCardMiddle.getX(), finalCardMiddle.getY()));			
		}

		this.cards.add(0, c);
		this.getChildren().add(c);

		if(refresh) {
			this.refreshMenuData();
		}

	}

	public Card find(String cardId) {

		Card c = null;

		for(Card tmpC : this.cards) {

			if(tmpC.getCardId().equals(cardId)) {
				c = tmpC;
				break;
			}
		}

		return c;
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

	public void sort(String order) throws InvalidActionException {

		Card[] sorted = new Card[this.cards.size()];
		int index = 0;

		for(String cardId : order.split("=>")) {

			Card c = find(cardId);

			if(c == null) {
				throw new InvalidActionException("unable to realize ordered sorting. '"+cardId+"' is missing");
			}

			sorted[index++] = c;
		}

		this.cards.clear();
		this.getChildren().clear();

		for(int i = (sorted.length-1); i >= 0; i--) {
			captureCard(sorted[i], false);
		}

		refreshMenuData();
	}

	public void sort() throws InvalidActionException {

		Card[] sorted = new Card[this.cards.size()];
		List<Integer> availablePositions = new ArrayList<>();
		Random r = new Random();
		int index = 0;

		for(int i = 0; i < this.cards.size(); i++) {
			availablePositions.add(i);
		}

		while(availablePositions.size() > 0) {

			int pos = r.nextInt(availablePositions.size());
			int avPos = availablePositions.get(pos);

			availablePositions.remove(pos);
			sorted[avPos] = this.cards.get(index++);
		}

		this.cards.clear();
		this.getChildren().clear();

		for(int i = (sorted.length-1); i >= 0; i--) {
			captureCard(sorted[i], false);
		}

		refreshMenuData();
	}

	public List<Card> getAll() {
		return get(Integer.MAX_VALUE);
	}

	public List<Card> get(int nb) {

		List<Card> copy = new ArrayList<>();

		for(int i = 0; i < Math.min(this.cards.size(), nb); i++) {
			copy.add(this.cards.get(i));
		}

		return copy;
	}

	public Card getFirst() {

		Card first = null;

		if(this.cards.size() > 0) {
			first = this.cards.get(0);
		}

		return first;
	}

	@Override
	public void removeCard(Card c, boolean refresh) throws InvalidActionException {
		this.cards.remove(c);
		this.getChildren().remove(c);

		if(refresh) {
			refreshMenuData();
		}
	}

	@Override
	public void reset() throws InvalidActionException {
		this.getChildren().clear();
		this.cards.clear();
	}
}
