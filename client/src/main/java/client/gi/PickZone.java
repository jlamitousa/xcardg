package client.gi;

import org.slf4j.LoggerFactory;

public class PickZone extends Pile {

	private static final double WIDTH_PERCENT = 0.12;
	private static final double HEIGHT_PERCENT = 0.6;
	private static final double CORNER_PERCENT = 0.2;
	private static final double CARD_WIDTH_PERCENT = 44.2 / 100.0;
	private static final double CARD_HEIGHT_PERCENT = 0.32;

	public PickZone(Environnement env) {
		
		super(env, "PickZone", WIDTH_PERCENT, HEIGHT_PERCENT, CORNER_PERCENT, CARD_WIDTH_PERCENT, CARD_HEIGHT_PERCENT);
		LOGGER = LoggerFactory.getLogger("PickZone");
		setShowMode(false);

	}

	@Override
	public CaptureZone getCurrentCapturingZone() {
		return getParentEnv().getCurrentCapturingZone();
	}

	@Override
	public PlayerZone getCurrentlyZoomedPZ() {
		return getParentEnv().getCurrentlyZoomedPZ();
	}

	@Override
	public PlayerZone getRelatedPZ() {
		return getParentEnv().getRelatedPZ();
	}
}
