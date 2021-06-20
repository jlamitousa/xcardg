package actions;

public enum HandZoneAction {

	CARD_CAPTURE,
	CARD_CAPTURE_ABORD,
	CARD_PRECAPTURE,
	USER_MOVE,
	CARD_ZOOM,
	CARD_UNZOOM,
	CARD_SELECT,
	CARD_ENGAGE,
	CARD_DEGAGE,
	CARD_RETURN;

	public static HandZoneAction translate(CaptureZoneAction cza) {

		HandZoneAction hza = null;

		switch (cza) {
		case USER_MOVE:
			hza = USER_MOVE;
			break;
		case CARD_ZOOM:
			hza = CARD_ZOOM;
			break;
		case CARD_UNZOOM:
			hza = CARD_UNZOOM;
			break;
		case CARD_SELECT:
			hza = CARD_SELECT;
			break;
		case CARD_ENGAGE:
			hza = CARD_ENGAGE;
			break;
		case CARD_DEGAGE:
			hza = CARD_DEGAGE;
			break;
		case CARD_CAPTURE:
			hza = CARD_CAPTURE;
			break;
		case CARD_CAPTURE_ABORD:
			hza = CARD_CAPTURE_ABORD;
			break;
		case CARD_PRECAPTURE:
			hza = CARD_PRECAPTURE;
			break;
		default:
			break;
		}

		return hza;
	}
}
