package util.math;

public class MathOps {

	private MathOps() { }
		
	public static Point rotate(double degreesAngle, Point pivot, Point p) {
		
		if(pivot == null || pivot.isOrigin()) {
			
			return rotate(degreesAngle, p);
		
		} else {
		
			Point pTransformed = null;
			Matrix2D rotationPassageMatrix = new Matrix2D();
			Matrix2D rotationPassageMatrixRev = new Matrix2D();
			double rotationPassageRadAngle = -computeAngleRad(pivot);
			
			rotationPassageMatrix.setRow(0, new double[]{ Math.cos(rotationPassageRadAngle), -Math.sin(rotationPassageRadAngle) });
			rotationPassageMatrix.setRow(1, new double[]{ Math.sin(rotationPassageRadAngle),  Math.cos(rotationPassageRadAngle) });
		
			rotationPassageMatrixRev.setRow(0, new double[]{ Math.cos(-rotationPassageRadAngle), -Math.sin(-rotationPassageRadAngle) });
			rotationPassageMatrixRev.setRow(1, new double[]{ Math.sin(-rotationPassageRadAngle),  Math.cos(-rotationPassageRadAngle) });

			pTransformed = rotationPassageMatrix.apply(p);
			pTransformed = translate(pTransformed, new Vector(-norm(pivot), 0));
			pTransformed = rotate(degreesAngle, pTransformed);
			pTransformed = translate(pTransformed, new Vector(norm(pivot), 0));
			pTransformed = rotationPassageMatrixRev.apply(pTransformed);
			
			return pTransformed;
		}
	}
	
	public static Point rotate(double degreesAngle, double pivotX, double pivotY, double x, double y) {
		return rotate(degreesAngle, new Point(pivotX, pivotY), new Point(x, y));
	}
	
	public static Vector rotate(double degreesAngle, Point pivot, Vector v) {
		
		Point res = rotate(degreesAngle, pivot, new Point(v.getX(), v.getY()));
		
		return new Vector(res.getX(), res.getY());
	}
	
	public static Vector rotate(double degreesAngle, Vector v) {
		
		Point res = rotate(degreesAngle, new Point(v.getX(), v.getY()));
		
		return new Vector(res.getX(), res.getY());
	}
	
	public static Point rotate(double degreesAngle, Point p) {
		
		Matrix2D rotationMatrix = new Matrix2D();
		double radAngle = Math.toRadians(degreesAngle);
		
		rotationMatrix.setRow(0, new double[]{ Math.cos(radAngle), -Math.sin(radAngle) });
		rotationMatrix.setRow(1, new double[]{ Math.sin(radAngle),  Math.cos(radAngle) });
	
		return rotationMatrix.apply(p);
	}
	
	public static Vector rotateVector(double degreesAngle, Vector v) {
		
		Point p = rotate(degreesAngle, v);
		
		return new Vector(p.getX(), p.getY());
	}

	public static Point translate(Point p, Vector t) {
		return new Point(p.getX()+t.getX(), p.getY()+t.getY());
	}
	
	public static Point scale(Point p, Point pivot, double factorX, double factorY) {
		
		Matrix2D scaleMatrix = new Matrix2D();
		Vector pivotTranslator = new Vector((1-factorX)*pivot.getX(), (1-factorY)*pivot.getY());
				
		scaleMatrix.setRow(0, new double[]{ factorX,       0 });
		scaleMatrix.setRow(1, new double[]{ 0,       factorY });
	
		return translate(scaleMatrix.apply(p), pivotTranslator);
	}
	
	private static double computeAngleRad(Point p) {
		
		double sign = p.getY() >= 0 ? 1 : -1;
		
		return sign*Math.acos(p.getX() / norm(p));
	}
	
	private static double scalarProduct(Point p1, Point p2) {
		return p1.getX()*p2.getY() + p1.getY()*p2.getX();
	}
	
	private static double norm(Point p) {
		return Math.sqrt(p.getX()*p.getX() + p.getY()*p.getY());
	}
}
