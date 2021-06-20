package client.gi;

public class CardState {

	private PileCardState cs;
	
	private String cardName;
	private int atk;
	private int def;
	
	public CardState(String cardName) {
		this.cardName = cardName;
		this.atk = 0;
		this.def = 0;
	}

	public String getCardName() {
		return this.cardName;
	}
	
	public int getAtk() {
		return atk;
	}

	public void setAtk(int atk) {
		this.atk = atk;
	}

	public int getDef() {
		return def;
	}

	public void setDef(int def) {
		this.def = def;
	}
	
}
