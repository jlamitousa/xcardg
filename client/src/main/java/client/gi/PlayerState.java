package client.gi;

public class PlayerState {

	private int id;
	private PickZone pickZone;
	private HandZone handZone;
	private String name;
	private int vie;
	private int degatCommanderP2;
	private int degatCommanderP3;
	private int degatCommanderP4;
	private int degatPoisonP2;
	private int degatPoisonP3;
	private int degatPoisonP4;
	
	public PlayerState(PickZone pickZone, HandZone handZone) {
		this.pickZone = pickZone;
		this.handZone = handZone;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getNbInPickZone() {
		return pickZone.getPileSize();
	}
	public void setNbInPickZone(int nbInPickZone) { /* Dynamic */ }
	
	public int getNbInHand() {
		return handZone.getNbCards();
	}
	
	public void setNbInHand(int nbInHand) { /* Dynamic */ }
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVie() {
		return vie;
	}
	public void setVie(int vie) {
		this.vie = vie;
	}
	public int getDegatCommanderP2() {
		return degatCommanderP2;
	}
	public void setDegatCommanderP2(int degatCommanderP2) {
		this.degatCommanderP2 = degatCommanderP2;
	}
	public int getDegatCommanderP3() {
		return degatCommanderP3;
	}
	public void setDegatCommanderP3(int degatCommanderP3) {
		this.degatCommanderP3 = degatCommanderP3;
	}
	public int getDegatCommanderP4() {
		return degatCommanderP4;
	}
	public void setDegatCommanderP4(int degatCommanderP4) {
		this.degatCommanderP4 = degatCommanderP4;
	}
	public int getDegatPoisonP2() {
		return degatPoisonP2;
	}
	public void setDegatPoisonP2(int degatPoisonP2) {
		this.degatPoisonP2 = degatPoisonP2;
	}
	public int getDegatPoisonP3() {
		return degatPoisonP3;
	}
	public void setDegatPoisonP3(int degatPoisonP3) {
		this.degatPoisonP3 = degatPoisonP3;
	}
	public int getDegatPoisonP4() {
		return degatPoisonP4;
	}
	public void setDegatPoisonP4(int degatPoisonP4) {
		this.degatPoisonP4 = degatPoisonP4;
	}
}
