package client.menu;

import client.gi.CardState;

public class CardInfoData {

	private CardInfo cardInfo;
	private CardState cardState;

	private String infoName;
	private String infoValue;

	public CardInfoData(CardInfo cardInfo, CardState cardState) {

		this.cardInfo  = cardInfo;
		this.cardState = cardState;

		setInfoName(cardInfo.realName);
		setInfoValue(getInfoValue());
	}

	public String getInfoName() {
		return this.infoName;
	}
	
	public void setInfoName(String infoName) { 
		this.infoName = infoName;
	}
	
	public String getInfoValue() {

		String value = "";
		
		switch(cardInfo) {
		case ATK:
			value = this.cardState.getAtk()+"";
			break;
		case DEF:
			value = this.cardState.getDef()+"";
			break;
		default:
		}

		return value;
		
	}
	
	public void setInfoValue(String infoValue) {

		switch(cardInfo) {
		case ATK:
			this.cardState.setAtk(Integer.parseInt(infoValue));
			break;
		case DEF:
			this.cardState.setDef(Integer.parseInt(infoValue));
			break;
		default:
		}

	}

}
