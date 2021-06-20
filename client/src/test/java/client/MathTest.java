package client;

import util.math.MathOps;
import util.math.Point;

public class MathTest {

	public static void main(String[] args) {
		System.out.println(MathOps.rotate(90, new Point(0, 0)));
		System.out.println(
				MathOps.rotate(
						90, 
						new Point(1, 0),
						new Point(0, 0)));
		System.out.println(
				MathOps.rotate(
						90, 
						new Point(4*Math.cos(Math.toRadians(45)), 4*Math.sin(Math.toRadians(45))),
						new Point(4*Math.cos(Math.toRadians(45)), 0)));
		
		System.out.println(MathOps.scale(new Point(1, 1), new Point(1, 0), 2, 2.5));
	}
}
