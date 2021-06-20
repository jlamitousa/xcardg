package tranforms;

import javafx.scene.transform.Translate;
import util.math.Point;

public class UserMoveCurrTranReverse extends Translate implements ReversedTranslation, IUserMove { 
	
	private Point from;
	private Point to;
	
	public UserMoveCurrTranReverse() {
		this(new Point(0, 0), new Point(0, 0));
	}
	
	public UserMoveCurrTranReverse(Point from, Point to) {
		this(from, to, to.getX()-from.getX(), to.getY()-from.getY());
	}
	
	/**
	 * This constructor aims to make the role of each subtraction 
	 * in others constuctor more clear.
	 * @param from
	 * @param to
	 * @param diffX
	 * @param diffY
	 */
	private  UserMoveCurrTranReverse(Point from, Point to, double diffX, double diffY) {
		super(diffX, diffY);
		this.from = from;
		this.to = to;
	}
	
	public Point getFrom() {
		return this.from;
	}
	public Point getTo() {
		return this.to;
	}
	
	@Override
	public String toString() {
		
		double diffX = to.getX()-from.getX();
		double diffY = to.getY()-from.getY();
		
		return "Moving (reversed) from "+this.from+" to "+this.to+" => ("+diffX+", "+diffY+")";
	}
	
}
