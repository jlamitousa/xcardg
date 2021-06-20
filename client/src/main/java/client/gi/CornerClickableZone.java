package client.gi;

import actions.MouseAction;
import util.math.Point;

public interface CornerClickableZone {

	public double getCornerWidth();
	public Point getCCZReverseRelative(Point absPoint);
	public double getWidth();
	public double getHeight();
	
	public default boolean isDownLeftCorner(MouseAction ma) {
		return isDownLeftCorner(new Point(ma.getX(), ma.getY()));
	}

	public default boolean isDownLeftCorner(Point absPoint) {

		Point rel = getCCZReverseRelative(absPoint);

		double x = rel.getX();
		double y = rel.getY();

		double limitMinX = 0;
		double limitMaxX = getCornerWidth();
		double limitMinY = getHeight()-getCornerWidth();
		double limitMaxY = getHeight();

		return (limitMinX < x && x < limitMaxX) && (limitMinY < y && y < limitMaxY);
	}
	
	public default boolean isUpRightCorner(MouseAction ma) {
		return isUpRightCorner(new Point(ma.getX(), ma.getY()));
	}

	public default boolean isUpRightCorner(Point absPoint) {

		Point rel = getCCZReverseRelative(absPoint);

		double x = rel.getX();
		double y = rel.getY();

		double limitMinX = getWidth()-getCornerWidth();
		double limitMaxX = getWidth();
		double limitMinY = 0;
		double limitMaxY = getCornerWidth();

		return (limitMinX < x && x < limitMaxX) && (limitMinY < y && y < limitMaxY);
	}

	public default boolean isUpLeftCorner(Point absPoint) {
		return isUpLeftCorner(absPoint.getX(), absPoint.getY());
	}

	public default boolean isUpLeftCorner(double absX, double absY) {

		Point rel = getCCZReverseRelative(new Point(absX, absY));

		double x = rel.getX();
		double y = rel.getY();

		double limitMinX = 0;
		double limitMaxX = getCornerWidth();
		double limitMinY = 0;
		double limitMaxY = getCornerWidth();

		return (limitMinX < x && x < limitMaxX) && (limitMinY < y && y < limitMaxY);
	}

	public default boolean isDownRightCorner(Point absPoint) {
		return isDownRightCorner(absPoint.getX(), absPoint.getY());
	}
	
	public default boolean isDownRightCorner(double absX, double absY) {

		Point rel = getCCZReverseRelative(new Point(absX, absY));

		double x = rel.getX();
		double y = rel.getY();

		double limitMinX = getWidth()-getCornerWidth();
		double limitMaxX = getWidth();
		double limitMinY = getHeight()-getCornerWidth();
		double limitMaxY = getHeight();

		return (limitMinX < x && x < limitMaxX) && (limitMinY < y && y < limitMaxY);
	}
	
}
