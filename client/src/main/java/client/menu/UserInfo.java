package client.menu;

public enum UserInfo {

	VIE("Vie"),
	NB_CARD_HAND("Main"),
	NB_CARD_PICK("Biblio"),
	DEGAT_COMMANDER2("Dégats CMD (2)"),
	DEGAT_COMMANDER3("Dégats CMD (3)"),
	DEGAT_COMMANDER4("Dégats CMD (4)"),
	DEGAT_POISON2("Dégats POI (2)"),
	DEGAT_POISON3("Dégats POI (3)"),
	DEGAT_POISON4("Dégats POI (4)");
	
	String realName;
	
	private UserInfo(String realName) {
		this.realName = realName;
	}
	
	public String getRealName() {
		return this.realName;
	}
}
