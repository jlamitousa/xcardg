package client.gi;

import org.slf4j.LoggerFactory;

import actions.InvalidActionException;

public class CommandZone extends Pile {

	private static final double WIDTH_PERCENT = 0.11;
	private static final double HEIGHT_PERCENT = 0.3;
	private static final double CORNER_PERCENT = 0;
	private static final double CARD_WIDTH_PERCENT = 48.2 / 100.0;
	private static final double CARD_HEIGHT_PERCENT = 66.0 / 100.0;

	public CommandZone(Environnement env) {
		super(env, "CommandZone", WIDTH_PERCENT, HEIGHT_PERCENT, CORNER_PERCENT, CARD_WIDTH_PERCENT, CARD_HEIGHT_PERCENT);
		LOGGER = LoggerFactory.getLogger("CommandZone");
	}
	
	@Override
	public void reset() throws InvalidActionException {
		getChildren().clear();
	}
}
