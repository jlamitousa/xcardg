package client.gi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import actions.CaptureZoneAction;
import actions.CardAction;
import actions.GameZoneAction;
import actions.HandZoneAction;
import actions.InvalidActionException;
import actions.MouseAction;
import actions.PileZoneAction;
import actions.PlayerZoneAction;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import tranforms.InitReversedRotation;
import tranforms.ReversedTranslation;
import util.helper.ListUtil;
import util.math.MathOps;
import util.math.Point;
import util.math.Vector;

public interface Environnement {

	public static final Logger LOGGER = LoggerFactory.getLogger("Environnement");
	public static final Logger LOGGER_V = LoggerFactory.getLogger("EnvironnementVerbose");

	public static final double REVERSE_ANGLE = 180;
	public static double ZONE_MARGIN_PERCENT = 2.0 / 100.0;

	public String getName();

	public String getEnvId();
	
	public default boolean handleMouseClicked(MouseAction ma, boolean remote) { return false; }
	public default boolean handleMousePressed(MouseAction ma, boolean remote) { return false; }
	public default boolean handleMouseDragged(MouseAction ma, boolean remote) { return false; }
	public default boolean handleMouseReleased(MouseAction ma, boolean remote) { return false; }

	public default void refreshMenuData() {
		getGI().getMenu().refreshUsersInfosTable();
	}
	
	public default void displayCardState(CardState state) {
		getGI().getMenu().setCardState(state);
	}
	
	public default void clearCardState(CardState state) {
		getGI().getMenu().setCardState(null);
	}
	
	public default <T> boolean canPerform(T action) { return false; }

	public double getWidth();
	public double getHeight();

	public default Point getAbsolutePos() {
		return getAbsolute(new Point(0, 0), 0);
	}

	public Environnement getParentEnv();
	
	public default int getChildrenPosition(Object o) {
		return ListUtil.getNodePos(getChildren(), o);
	}

	public void reset() throws InvalidActionException;
	
	public default boolean isChildren(Environnement e) {
		
		boolean haveChildren = false;
		
		for(Node n : getChildren()) {

			if(n==e) {
				haveChildren = true;
				break;
			}
			
		}
		
		return haveChildren;
	}
	
	public default void displayPile(Pile pile)  throws InvalidActionException {
		getGI().displayPile(pile);
	}
	
	public default void displayPile(Pile pile, int nb)  throws InvalidActionException {
		getGI().displayPile(pile, nb);
	}
	
	public default void displayGame()  throws InvalidActionException  {
		getGI().displayGame();
	}

	public default void displayImg(String img, int secDuration) throws InvalidActionException {
		getGI().displayImg(img, secDuration);
	}
	
	public default Environnement getDirectTargetChildren(MouseAction ma) {
		
		Environnement match = null;
		List<Environnement> recEnvs = new ArrayList<>();
		int i = 0;

		for(Node n : getChildren()) {

			if(n instanceof Environnement) {

				Environnement env = (Environnement) n;
				String envId = env.getEnvId();
				
				if(ma.getTarget()==env || (envId!=null && envId.equals(ma.getTargetId()))) {

					match = env;
					break;
					
				}

				recEnvs.add(env);
				
			}
			
		}

		while((match==null) && (i < recEnvs.size())) {

			match = recEnvs.get(i).getDirectTargetChildren(ma);

			i++;
		}

		return match;

		
	}
	
	public default Environnement getTargetChildren(MouseAction ma) {

		Environnement match = null;
		List<Environnement> recEnvs = new ArrayList<>();
		int i = 0;

		for(Node n : getChildren()) {

			if(n instanceof Environnement) {

				Environnement env = (Environnement) n;
				String envId = env.getEnvId();
				
				if(ma.getTarget()==env || (envId!=null && envId.equals(ma.getTargetId()))) {

					match = env;
					break;
					
				}

				recEnvs.add(env);
			}
			
		}

		while((match==null) && (i < recEnvs.size())) {

			match = recEnvs.get(i).getTargetChildren(ma);

			i++;
			
		}

		return match;

	}
	
	public Point getInterfaceAbsMiddle();
	public Point getGameZoneAbsMiddle();
	
	public PlayerZone getRelatedPZ();
	
	public GraphicInterface getGI();
	
	public GameZone getGZ();
	
	public default void blockEvents() {
		
		GraphicInterface gi = getGI();
		
		if(!gi.isEventBlocked()) {
			getGI().blockEvents();
		}
			
	}
	
	public default void unblockEvents() {
		
		GraphicInterface gi = getGI();
		
		if(gi.isEventBlocked()) {
			getGI().unblockEvents();
		}
		
	}
	
	public default HandZone getHandZone() {
		return getGI().getHandZone();
	}
	
	public List<Transform> getTransforms();

	public default boolean haveTransform(Class<? extends Transform> clazz) {

		boolean haveTransform = false;

		for(Transform t : getTransforms()) {
			if(clazz.isInstance(t)) {
				haveTransform = true;
				break;
			}
		}

		return haveTransform;
	}

	public default Transform foundTransform(Class<? extends Transform> clazz) {

		Transform tr = null;

		for(Transform tmp : getTransforms()) {
			if(clazz.isInstance(tmp)) {
				tr = tmp;
				break;
			}
		}

		return tr;
	}
	
	public default void putTrAtEnd(Class<? extends Transform> clazz) {

		List<Transform> theEnd = new ArrayList<>();

		for(Transform t : getTransforms()) {
			if(clazz.isInstance(t)) {
				theEnd.add(t);
			}
		}

		for(Transform t : theEnd) {
			getTransforms().remove(t);
		}

		for(Transform t : theEnd) {
			getTransforms().add(t);
		}
	}
	
	
	public List<Node> getChildren();

	public default boolean isReversed() {
		return false;
	}

	public default boolean isParentReversed() {
		return getParentEnv() != null && getParentEnv().isReversed();
	}
	
	public default boolean isSpecialReversed() {
		return isReversed() && !haveTransform(InitReversedRotation.class);
	}

	public default Point getReverseRelative(Point absPoint) {

		Point parentAbs = absPoint;
		Point rev = null;
		Point upLeft = null;

		Environnement parentEnv = getParentEnv();
		Point parentRev = null;


		if(parentEnv != null) {

			//Due to JavaFX transformation mechanism, parent transformation
			//are applied to child but not referenced in child.getTransforms()
			//To take those parents transforms in our calculus, we have to first
			//to a reverseRelative to parentEnv then apply ours transforms.

			parentRev = parentEnv.getReverseRelative(absPoint);
			parentAbs = new Point(parentEnv.getAbsoluteX(parentRev.getX()), parentEnv.getAbsoluteY(parentRev.getY()));
		}

		rev = applyReverseTransforms(parentAbs, 0);
		
		//No more transformation left than ones related to reverse mode
		//
		//[double-reverse while no-special-reverse] => one 180 rotation has not be done. Special work needed with upleft point.
		//[normal-reverse] => No more reverse. Just compute the real upleft point for final value.
		//[normal] =>  Just compute the real upleft point for final value.
			
		if(isReversed() && isParentReversed() && !isSpecialReversed()) {
			
			upLeft = new Point(getAbsoluteX(0), getAbsoluteY(0));
			rev = new Point(upLeft.getX() - rev.getX(), upLeft.getY() - rev.getY());
			
		} else if (isReversed()) {
			
			upLeft = new Point(getAbsoluteX(getWidth()), getAbsoluteY(getHeight()));
			rev = new Point(rev.getX() - upLeft.getX(), rev.getY() - upLeft.getY());
			
		} else {
			
			upLeft = new Point(getAbsoluteX(0), getAbsoluteY(0));
			rev = new Point(rev.getX() - upLeft.getX(), rev.getY() - upLeft.getY());
			
		}
		
		return rev;
		
	}

	public default void putInFront(Object o) {

		int pos = -1;
		int size = getChildren().size();

		if(size > 0 && o!=getChildren().get(size-1)) {

			if(LOGGER.isDebugEnabled()) {
				if(o instanceof Environnement) {
					Environnement env = (Environnement) o;
					LOGGER.debug("Putting in front: "+env.getDisplay());
				} else {
					LOGGER.debug("Putting in front: "+o);
				}
			}

			for(int i = 0; i < getChildren().size(); i++) {
				if(getChildren().get(i)==o) {
					pos = i;
					break;
				}
			}

			if(pos > -1) {
				getChildren().remove(pos);
				getChildren().add((Node)o);
			}

		}
	}

	public default boolean authorizeAction(GameZone gz, GameZoneAction action, boolean isRemote) throws InvalidActionException {
		return authorizeAction(gz, action, isRemote, false);
	}
	public default boolean authorizeAction(PlayerZone pz, PlayerZoneAction action, boolean isRemote) throws InvalidActionException {
		return authorizeAction(pz, action, isRemote, false);
	}
	public default boolean authorizeAction(HandZone hz, HandZoneAction action, boolean isRemote) throws InvalidActionException {
		return authorizeAction(hz, action, isRemote, false);
	}
	public default boolean authorizeAction(CaptureZone cz, CaptureZoneAction action, boolean isRemote) throws InvalidActionException {
		return authorizeAction(cz, action, isRemote, false);
	}
	public default boolean authorizeAction(Card c, CardAction action, boolean isRemote) throws InvalidActionException {
		return authorizeAction(c, action, isRemote, false);
	}
	
	public default boolean authorizeAction(GameZone gz, GameZoneAction action, boolean isRemote, boolean forceDontPutInFront) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default boolean authorizeAction(PileZone pileZone, PileZoneAction action, boolean isRemote, boolean forceDontPutInFront) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default boolean authorizeAction(PlayerZone pz, PlayerZoneAction action, boolean isRemote, boolean forceDontPutInFront) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default boolean authorizeAction(HandZone hz, HandZoneAction action, boolean isRemote, boolean forceDontPutInFront) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default boolean authorizeAction(CaptureZone c, CaptureZoneAction action, boolean isRemote, boolean forceDontPutInFront) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default boolean authorizeAction(Card c, CardAction action, boolean isRemote, boolean forceDontPutInFront) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}

	public default void notifyActionFinish(GameZone gz, GameZoneAction action) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default void notifyActionFinish(PileZone pileZone, PileZoneAction action) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default void notifyActionFinish(PlayerZone pz, PlayerZoneAction action) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default void notifyActionFinish(CaptureZone c, CaptureZoneAction action) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default void notifyActionFinish(HandZone hz, HandZoneAction action) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	public default void notifyActionFinish(Card c, CardAction action) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}

	public default void captureCard(Card c, boolean refresh) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}

	public CaptureZone getCurrentCapturingZone();
	
	public default void setCurrentCapturingZone(CaptureZone cz) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}

	public PlayerZone getCurrentlyZoomedPZ();
		
	public default void setCurrentlyZoomedPZ(PlayerZone pz) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	
	public Card getCurrentlyZoomedCard();
	
	public default void setCurrentlyZoomedCard(Card c) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	
	public CaptureZone getCurrentCapturingZoneZoomed();
	
	public default void setCurrentCapturingZoneZoomed(CaptureZone cz) throws InvalidActionException {
		throw new InvalidActionException("Not implemented");
	}
	
	public default void removeCard(Card c, boolean refresh) throws InvalidActionException {
		throw new InvalidActionException();
	}

	public default boolean imTheTarget(MouseAction ma) {
		
		String envId = getEnvId();
		
		return this==ma.getTarget() || (envId != null && envId.equals(ma.getTargetId()));
	
	}

	public default boolean isInZone(Point p) {
		return isInZone(p.getX(), p.getY());
	}

	public default boolean isRectangleMiddleInZone(Point upLeft, Point downRight) {

		double middleX = upLeft.getX() + (downRight.getX()-upLeft.getX()) / 2;
		double middleY = upLeft.getY() + (downRight.getY()-upLeft.getY()) / 2;

		return isInZone(middleX, middleY);
	}

	public default boolean isInZone(double x, double y) {

		Point upLeft = isReversed() ?  this.getAbsoluteDownRight() : this.getAbsoluteUpLeft();
		Point downRight = isReversed() ? this.getAbsoluteUpLeft() : this.getAbsoluteDownRight();

		double limitMinX = upLeft.getX();
		double limitMaxX = downRight.getX();
		double limitMinY = upLeft.getY();
		double limitMaxY = downRight.getY();

		return (limitMinX < x && x < limitMaxX) && (limitMinY < y && y < limitMaxY);
	}

	public default boolean isRectangleInZone(Point absUpLeft, Point absDownRight) {
		return isInZone(absUpLeft.getX(), absUpLeft.getY()) && isInZone(absDownRight.getX(), absDownRight.getY());
	}


	public double getAbsoluteX(double x);
	public double getAbsoluteY(double y);

	public default Point getAbsoluteMiddle() {
		Point relative = new Point(getWidth() / 2, getHeight() / 2);
		Point result = applyAbsoluteTransformsRec(new Point(getAbsoluteX(relative.getX()), getAbsoluteY(relative.getY())), getTransforms(), 0);
		Environnement parentEnv = getParentEnv();

		if(parentEnv != null) {
			result = parentEnv.getAbsoluteContinuation(result, 0);
		}
		
		return result;
		
	}
	
	public default Point getAbsoluteUpLeft() {
		
		Point relative = new Point(0, 0);
		Point result = applyAbsoluteTransformsRec(new Point(getAbsoluteX(relative.getX()), getAbsoluteY(relative.getY())), getTransforms(), 0);
		Environnement parentEnv = getParentEnv();

		if(parentEnv != null) {
			result = parentEnv.getAbsoluteContinuation(result, 0);
		}
		
		return result;
		
	}

	public default Point getAbsoluteDownRight() {
		
		Point relative = new Point(getWidth(), getHeight());
		Point result = applyAbsoluteTransformsRec(new Point(getAbsoluteX(relative.getX()), getAbsoluteY(relative.getY())), getTransforms(), 0);
		Environnement parentEnv = getParentEnv();

		if(parentEnv != null) {
			result = parentEnv.getAbsoluteContinuation(result, 0);
		}
		
		return result;
		
	}

	public default Point getAbsolute(Point relPoint, int recursionLVL) {
		
		Point result = applyAbsoluteTransformsRec(new Point(getAbsoluteX(relPoint.getX()), getAbsoluteY(relPoint.getY())), getTransforms(), recursionLVL);
		Environnement parentEnv = getParentEnv();

		if(parentEnv != null) {
			result = parentEnv.getAbsoluteContinuation(result, 0);
		}
		
		return result;
		
	}
	
	public default Point getAbsoluteContinuation(Point childAbs, int recursionLVL) {
		
		Point result = applyAbsoluteTransformsRec(childAbs, getTransforms(), recursionLVL);
		Environnement parentEnv = getParentEnv();

		if(parentEnv != null) {
			result = parentEnv.getAbsoluteContinuation(result, 0);
		}
		
		return result;
		
	}
	
	public default Point getAbsolute(Point relPoint, List<Transform> tranforms, int recursionLVL) {
		return applyAbsoluteTransformsRec(new Point(getAbsoluteX(relPoint.getX()), getAbsoluteY(relPoint.getY())), tranforms, recursionLVL);
	}

	public default Point applyAbsoluteTransforms(Point absPoint, int recursionLVL) {
		return applyAbsoluteTransformsRec(absPoint, getTransforms(), recursionLVL);
	}

	public default Point applyReverseTransforms(Point absPoint, int recursionLVL) {
		
		List<Transform> transforms = ListUtil.copy(getTransforms());
		
		Point result = applyReverseTransformsRec(absPoint, transforms, recursionLVL);
		Point middle = null;
		
		if(isSpecialReversed()) {
			middle = applyReverseTransformsRec(getAbsoluteMiddle(), transforms, recursionLVL);
			result = MathOps.rotate(REVERSE_ANGLE, middle, result);
		}
		
		return result;
	}

	public default Point applyAbsoluteTransformsRec(Point absPoint, List<Transform> transforms, int recursionLVL) {

		if(LOGGER_V.isDebugEnabled()) {
			LOGGER_V.debug(getSpacing(recursionLVL)+"applyAbsoluteTransforms (start): "+absPoint);
		}

		Point finalP = absPoint;
		List<Transform> applied = new ArrayList<Transform>();

		for(Transform tr : transforms) {

			if(tr instanceof InitReversedRotation) {

				continue;

			} else if(tr instanceof Rotate) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Rotation .. ");
				}

				Rotate r = (Rotate) tr;
				Point pivot = getAbsolute(new Point(r.getPivotX(), r.getPivotY()), applied, recursionLVL+1);

				finalP = MathOps.rotate(r.getAngle(), pivot, finalP);

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP: "+finalP);
				}

				applied.add(r);

			} else if (tr instanceof ReversedTranslation) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"ReversedTranslation .. ");
				}

				Translate t = (Translate) tr;

				finalP = MathOps.translate(finalP, new Vector(-t.getX(), -t.getY()));

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP: "+finalP);
				}

				applied.add(t);

			} else if (tr instanceof Translate) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Translate .. ");
				}

				Translate t = (Translate) tr;

				finalP = MathOps.translate(finalP, new Vector(t.getX(), t.getY()));

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP: "+finalP);
				}

				applied.add(t);

			} else if(tr instanceof Scale) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Scale .. ");
				}

				Scale s = (Scale) tr;
				Point pivot = getAbsolute(new Point(s.getPivotX(), s.getPivotY()), applied, recursionLVL+1);

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

	public default Point applyReverseTransformsRec(Point absPoint, List<Transform> transforms, int recursionLVL) {

		if(LOGGER_V.isDebugEnabled()) {
			LOGGER_V.debug(getSpacing(recursionLVL)+"(start) applyReverseTransforms to "+absPoint);
		}

		Point finalP = absPoint;
		List<Transform> reversedOrderTransforms = ListUtil.reverse(transforms);
		List<Transform> applied = new ArrayList<>();

		for(Transform tr : reversedOrderTransforms) {

			if(tr instanceof Rotate) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Rotation ..");
				}

				Rotate r = (Rotate) tr;
				Point pivot = applyReverseTransformsRec(getAbsolute(new Point(r.getPivotX(), r.getPivotY()), recursionLVL+1), ListUtil.reverse(applied), recursionLVL+1);

				finalP = MathOps.rotate(-r.getAngle(), pivot, finalP);

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP .."+finalP);
				}

				applied.add(r);

			} else if (tr instanceof ReversedTranslation) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"ReversedTranslation ..");
				}

				Translate t = (Translate) tr;

				finalP = MathOps.translate(finalP, new Vector(t.getX(), t.getY()));

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP .."+finalP);
				}

				applied.add(t);

			} else if (tr instanceof Translate) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Translate ..");
				}

				Translate t = (Translate) tr;

				finalP = MathOps.translate(finalP, new Vector(-t.getX(), -t.getY()));

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Current finalP .."+finalP);
				}

				applied.add(t);

			} else if(tr instanceof Scale) {

				if(LOGGER_V.isDebugEnabled()) {
					LOGGER_V.debug(getSpacing(recursionLVL)+"Scale ..");
				}

				Scale s = (Scale) tr;
				Point pivot = applyReverseTransformsRec(getAbsolute(new Point(s.getPivotX(), s.getPivotY()), recursionLVL+1), ListUtil.reverse(applied), recursionLVL+1);

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

	public default String getDisplay() {

		String display = "";

		display += "Name: "+getName()+",  ";
		display += "upLeft: "+getAbsoluteUpLeft()+", ";
		display += "downRight: "+getAbsoluteDownRight()+", ";
		display += "width: "+getWidth()+", ";
		display += "heigth: "+getHeight();
		
		if(getParentEnv() != null) {
			display += ", env: "+getParentEnv().getName();
		}

		return display;
	}

	public default String getSpacing(int recursionLVL) {

		StringBuilder spacingB = new StringBuilder();

		for(int i = 0; i < recursionLVL; i++) {
			spacingB.append("\t");
		}

		return spacingB.toString();
	}
}
