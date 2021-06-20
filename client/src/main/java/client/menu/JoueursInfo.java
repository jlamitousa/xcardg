package client.menu;

import client.gi.GraphicInterface;
import client.gi.PlayerState;

public class JoueursInfo {

	private static final int MAX_NAME_SIZE = 10;
	
	private UserInfo userInfo;
	private GraphicInterface gi;
	private PlayerState ps2;
	private PlayerState ps3; 
	private PlayerState ps4;

	private String infoName;
	private String infoJoueur1;
	private String infoJoueur2;
	private String infoJoueur3;
	private String infoJoueur4;

	public JoueursInfo(UserInfo userInfo, GraphicInterface gi, PlayerState ps2, PlayerState ps3, PlayerState ps4) {

		this.userInfo = userInfo;
		this.gi = gi;
		this.ps2 = ps2;
		this.ps3 = ps3;
		this.ps4 = ps4;

		setInfoName(userInfo.name());
		setInfoJoueur1(getInfoJoueur(1));
		setInfoJoueur2(getInfoJoueur(2));
		setInfoJoueur3(getInfoJoueur(3));
		setInfoJoueur4(getInfoJoueur(4));

	}

	public String getInfoName() {
		return infoName;
	}

	public void setInfoName(String infoName) { 

		String realName = userInfo.getRealName();

		if(ps2 != null && !ps2.getName().equals("none")) {
			String ps2Name = ps2.getName();
			ps2Name = ps2Name.length() > MAX_NAME_SIZE ? ps2.getName().substring(0, MAX_NAME_SIZE)+".." : ps2Name;
			realName = realName.replaceAll("2", ps2Name);
		}
		if(ps3 != null && !ps3.getName().equals("none")) {
			String ps3Name = ps3.getName();
			ps3Name = ps3Name.length() > MAX_NAME_SIZE ? ps3.getName().substring(0, MAX_NAME_SIZE)+".." : ps3Name;
			realName = realName.replaceAll("3", ps3Name);
		}
		if(ps4 != null && !ps4.getName().equals("none")) {
			String ps4Name = ps4.getName();
			ps4Name = ps4Name.length() > MAX_NAME_SIZE ? ps4.getName().substring(0, MAX_NAME_SIZE)+".." : ps4Name;
			realName = realName.replaceAll("4", ps4Name);
		}

		this.infoName = realName;
	}

	private PlayerState getPlayerState(int i) {

		PlayerState ps = null;

		if(i==2) {
			ps = ps2;
		} else if (i==3) {
			ps = ps3;
		} else if (i==4) {
			ps = ps4;
		}

		return ps;
	}

	public String getInfoJoueur(int i) {

		String result = "";
		PlayerState ps = gi.getPlayerState(i);

		if(ps != null) {

			switch (userInfo) {
			case VIE:
				result = ps.getVie()+"";
				break;
			case NB_CARD_HAND:
				result = ps.getNbInHand()+"";
				break;
			case NB_CARD_PICK:
				result = ps.getNbInPickZone()+"";
				break;
			case DEGAT_COMMANDER2:
				result = ps.getDegatCommanderP2()+"";
				break;
			case DEGAT_COMMANDER3:
				result = ps.getDegatCommanderP3()+"";
				break;
			case DEGAT_COMMANDER4:
				result = ps.getDegatCommanderP4()+"";
				break;
			case DEGAT_POISON2:
				result = ps.getDegatPoisonP2()+"";
				break;
			case DEGAT_POISON3:
				result = ps.getDegatPoisonP3()+"";
				break;
			case DEGAT_POISON4:
				result = ps.getDegatPoisonP4()+"";
				break;
			default:
				break;
			}
		}

		return result;

	}

	public void setInfoJoueur(int i, String newValue) {

		PlayerState ps = gi.getPlayerState(i);

		if(ps != null) {
			switch (userInfo) {
			case VIE:
				ps.setVie(Integer.parseInt(newValue));
				break;
			case DEGAT_COMMANDER2:
				ps.setDegatCommanderP2(Integer.parseInt(newValue));
				break;
			case DEGAT_COMMANDER3:
				ps.setDegatCommanderP3(Integer.parseInt(newValue));
				break;
			case DEGAT_COMMANDER4:
				ps.setDegatCommanderP4(Integer.parseInt(newValue));
				break;
			case DEGAT_POISON2:
				ps.setDegatPoisonP2(Integer.parseInt(newValue));
				break;
			case DEGAT_POISON3:
				ps.setDegatPoisonP3(Integer.parseInt(newValue));
				break;
			case DEGAT_POISON4:
				ps.setDegatPoisonP4(Integer.parseInt(newValue));
				break;
			default:
				break;
			}
		}

	}

	public String getInfoJoueur1() {
		return infoJoueur1;
	}

	public void setInfoJoueur1(String newValue) { 
		setInfoJoueur(1, newValue);
		infoJoueur1 = getInfoJoueur(1);
	}

	public String getInfoJoueur2() {
		return infoJoueur2;
	}

	public void setInfoJoueur2(String newValue) { 
		setInfoJoueur(2, newValue);
		infoJoueur2 = getInfoJoueur(2);
	}

	public String getInfoJoueur3() {
		return infoJoueur3;
	}

	public void setInfoJoueur3(String newValue) {
		setInfoJoueur(3, newValue);
		infoJoueur3 = getInfoJoueur(3);
	}

	public String getInfoJoueur4() {
		return infoJoueur4;
	}

	public void setInfoJoueur4(String newValue) {
		setInfoJoueur(4, newValue);
		infoJoueur4 = getInfoJoueur(4);
	}

}