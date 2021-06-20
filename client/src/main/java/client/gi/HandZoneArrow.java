package client.gi;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.shape.Rectangle;
import actions.InvalidActionException;
import actions.MouseAction;
import javafx.scene.Node;
import util.math.Point;

public class HandZoneArrow extends Rectangle implements Environnement {

	private Environnement env;
	private String name;
	
	public HandZoneArrow(Environnement env, String name, double x, double y, double width, double height) {
		
		this.env = env;
		this.name = name;
		
		this.setX(x);
		this.setY(y);
		this.setWidth(width);
		this.setHeight(height);
		
	}
	
	@Override
	public boolean handleMouseClicked(MouseAction ma, boolean remote) { return true; }
	
	@Override
	public boolean handleMousePressed(MouseAction ma, boolean remote) { return true; }
	
	@Override
	public boolean handleMouseDragged(MouseAction ma, boolean remote) { return true; }
	
	@Override
	public boolean handleMouseReleased(MouseAction ma, boolean remote) { return true; }
	
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getEnvId() {
		return env.getEnvId()+"_"+this.name;
	}

	@Override
	public Environnement getParentEnv() {
		return this.env;
	}

	@Override
	public void reset() throws InvalidActionException { /* nothing todo */ }

	@Override
	public Point getInterfaceAbsMiddle() {
		return this.env.getInterfaceAbsMiddle();
	}

	@Override
	public Point getGameZoneAbsMiddle() {
		return this.env.getGameZoneAbsMiddle();
	}

	@Override
	public PlayerZone getRelatedPZ() {
		return this.env.getRelatedPZ();
	}

	@Override
	public GraphicInterface getGI() {
		return this.env.getGI();
	}

	@Override
	public GameZone getGZ() {
		return this.env.getGZ();
	}

	@Override
	public CaptureZone getCurrentCapturingZone() {
		return this.env.getCurrentCapturingZone();
	}

	@Override
	public PlayerZone getCurrentlyZoomedPZ() {
		return this.env.getCurrentlyZoomedPZ();
	}

	@Override
	public CaptureZone getCurrentCapturingZoneZoomed() {
		return this.env.getCurrentCapturingZoneZoomed();
	}

	@Override
	public Card getCurrentlyZoomedCard() {
		return env.getCurrentlyZoomedCard();
	}
	
	@Override
	public double getAbsoluteX(double x) {
		return -1;
	}

	@Override
	public double getAbsoluteY(double y) {
		return -1;
	}

	@Override
	public List<Node> getChildren() {
		return new ArrayList<Node>();
	}

}
