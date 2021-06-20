package client.menu;

public enum UserInfo {

	VIE("Vie"),
	NB_CARD_HAND("Main"),
	NB_CARD_PICK("Biblio"),
	DEGAT_COMMANDER2("D�gats CMD (2)"),
	DEGAT_COMMANDER3("D�gats CMD (3)"),
	DEGAT_COMMANDER4("D�gats CMD (4)"),
	DEGAT_POISON2("D�gats POI (2)"),
	DEGAT_POISON3("D�gats POI (3)"),
	DEGAT_POISON4("D�gats POI (4)");
	
	String realName;
	
	private UserInfo(String realName) {
		this.realName = realName;
	}
	
	public String getRealName() {
		return this.realName;
	}
}
