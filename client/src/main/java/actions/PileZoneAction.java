package actions;

public enum PileZoneAction {

	CARD_SELECT,
	USER_MOVE,
	ZOOM,
	UNZOOM,
	CARD_PRECAPTURE,
	CARD_CAPTURE,
	CARD_ZOOM,
	CARD_UNZOOM,
	CARD_CAPTURE_ABORD;
	
	public static PileZoneAction translate(CaptureZoneAction cza) {

		PileZoneAction pza = null;

		switch (cza) {
		case USER_MOVE:
			pza = USER_MOVE;
			break;
		case CARD_SELECT:
			pza = CARD_SELECT;
			break;
		case CARD_CAPTURE:
			pza = CARD_CAPTURE;
			break;
		case CARD_CAPTURE_ABORD:
			pza = CARD_CAPTURE_ABORD;
			break;
		case CARD_PRECAPTURE:
			pza = CARD_PRECAPTURE;
			break;
		case CARD_ZOOM:
			pza = CARD_ZOOM;
			break;
		case CARD_UNZOOM:
			pza = CARD_UNZOOM;
			break;
		case ZOOM:
			pza = PileZoneAction.ZOOM;
			break;
		case UNZOOM:
			pza = UNZOOM;
			break;
		default:
			break;
		}

		return pza;
	}
}
