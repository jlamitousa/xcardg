package util.math;

public class Matrix2D {

	private double[][] matrix;
	
	public Matrix2D() {
		matrix = new double[2][];
	}
	
	public void setRow(int i, double[] values) {
		this.matrix[i] = values;
	}
	
	public Point apply(Point p) {
		return new Point(
					this.matrix[0][0]*p.getX()+this.matrix[0][1]*p.getY(),
					this.matrix[1][0]*p.getX()+this.matrix[1][1]*p.getY());
	}
	
	public String toString() {
		return "|"+this.matrix[0][0]+", "+this.matrix[0][1]+"|\n|"+this.matrix[1][0]+", "+this.matrix[1][1]+"|";
	}
}
