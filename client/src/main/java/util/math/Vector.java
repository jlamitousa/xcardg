package util.math;

public class Vector extends Point {

	public Vector(double x, double y) {
		super(x, y);
	}
	
	public Vector(Point from, Point to) {
		this(to.getX()-from.getX(), to.getY()-from.getY());
	}
	
	public static Vector sum(Vector v1, Vector v2) {
		return new Vector(v1.getX()+v2.getX(), v1.getY()+v2.getY());
	}
}
