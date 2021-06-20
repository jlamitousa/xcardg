package actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.gi.Card;
import client.gi.Environnement;
import client.gi.GraphicInterface;
import util.helper.EventHandling;

public class MouseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger("MouseAction");
	
	private GraphicInterface gi;
	
	private double x;
	private double y;
	private Object target;
	private String targetId;
	private boolean doubleClicked;
	
	private int sourceUserId;
	
	public MouseAction(GraphicInterface gi, int sourceUserId, double x, double y, String targetId) {
		this(gi, sourceUserId, x, y, targetId, false);
	}
	
	public MouseAction(GraphicInterface gi, int sourceUserId, double x, double y, String targetId, boolean doubleClicked) {
		
		this.gi = gi;
		this.sourceUserId = sourceUserId;
		this.x = x;
		this.y = y;
		this.targetId = targetId;
		this.doubleClicked = doubleClicked;
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("new MouseAction: x="+x+", y="+y+", targetId="+targetId+", doubleClicked="+doubleClicked+" target="+gi.getTargetChildren(this).getEnvId()+" asCard="+EventHandling.getTargetAs(this, Card.class));
		}
		
	}
	
	public MouseAction(GraphicInterface gi, int sourceUserId, double x, double y, Object target) {
		this(gi, sourceUserId, x, y, target, false);
	}
	
	public MouseAction(GraphicInterface gi, int sourceUserId, double x, double y, Object target, boolean doubleClicked) {
		
		this.gi = gi;
		this.sourceUserId = sourceUserId;
		this.x = x;
		this.y = y;
		this.target = target;
	
		if(target instanceof Environnement) {
			this.targetId = ((Environnement)target).getEnvId();
		}
		
		this.doubleClicked = doubleClicked;
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("new MouseAction: x="+x+", y="+y+", targetId="+targetId+", doubleClicked="+doubleClicked+" target="+gi.getTargetChildren(this).getEnvId()+" asCard="+EventHandling.getTargetAs(this, Card.class));
		}
	}

	public GraphicInterface getGi() {
		return gi;
	}

	public int getSourceUserId() {
		return sourceUserId;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public boolean isDoubleClicked() {
		return doubleClicked;
	}

	public Object getTarget() {
		return target;
	}

	public String getTargetId() {
		return targetId;
	}
}
