package client.gi;

/**
 * A class used to save current information of a card before a special move.
 * Aim to be restored.
 * @author Jean-Luc
 *
 */
public class CardSnapshot {

	
	private double savedX;
	private double savedY;
	private double savedWidth;
	private double savedHeight;
	private int savedChildPos;
	private Environnement savedEnv;
	
	
	public double getSavedX() {
		return savedX;
	}
	public void setSavedX(double savedX) {
		this.savedX = savedX;
	}
	public double getSavedY() {
		return savedY;
	}
	public void setSavedY(double savedY) {
		this.savedY = savedY;
	}
	public double getSavedWidth() {
		return savedWidth;
	}
	public void setSavedWidth(double savedWidth) {
		this.savedWidth = savedWidth;
	}
	public double getSavedHeight() {
		return savedHeight;
	}
	public void setSavedHeight(double savedHeight) {
		this.savedHeight = savedHeight;
	}
	public int getSavedChildPos() {
		return savedChildPos;
	}
	public void setSavedChildPos(int savedChildPos) {
		this.savedChildPos = savedChildPos;
	}
	public Environnement getSavedEnv() {
		return savedEnv;
	}
	public void setSavedEnv(Environnement savedEnv) {
		this.savedEnv = savedEnv;
	}
	
}
