package client.menu;

public enum CardInfo {
	
	ATK("Attaque"),
	DEF("Defense");
	
	String realName;
	
	private CardInfo(String realName) {
		this.realName = realName;
	}
	
	public String getRealName() {
		return this.realName;
	}
}
