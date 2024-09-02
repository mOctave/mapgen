package moctave.esmapper;

import java.awt.Dimension;
import java.awt.Point;

public class RectCoordinate {
	public RectCoordinate() {
		x = 0;
		y = 0;
	}
	public RectCoordinate(
		double x,
		double y
	) {
		this.x = x;
		this.y = y;
	}

	public RectCoordinate(Dimension d) {
		x = d.getWidth();
		y = d.getHeight();
	}

	public RectCoordinate(Point p) {
		x = p.getX();
		y = p.getY();
	}

	private double x;
	private double y;

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Truncates the coordinate pair, keeping only the integer part.
	 * Does not convert the coordinate pair to using integers.
	 */
	public void trunc() {
		x = (int) x;
		y = (int) y;
	}

	public String toString() {
		return String.format("(%f, %f)", x, y);
	}

	// Convert dimension to a Dimension or Point.
	public Dimension toDimension() {
		return new Dimension((int) x, (int) y);
	}

	public Point toPoint() {
		return new Point((int) x, (int) y);
	}

	// Math helper methods
	public static RectCoordinate add(RectCoordinate c1, RectCoordinate c2) {
		return new RectCoordinate(
			c1.getX() + c2.getX(),
			c1.getY() + c2.getY()
		);
	}

	public static RectCoordinate subtract(RectCoordinate c1, RectCoordinate c2) {
		return new RectCoordinate(
			c1.getX() - c2.getX(),
			c1.getY() - c2.getY()
		);
	}

	public static RectCoordinate multiply(RectCoordinate c1, RectCoordinate c2) {
		return new RectCoordinate(
			c1.getX() * c2.getX(),
			c1.getY() * c2.getY()
		);
	}

	public static RectCoordinate divide(RectCoordinate c1, RectCoordinate c2) {
		return new RectCoordinate(
			c1.getX() / c2.getX(),
			c1.getY() / c2.getY()
		);
	}
}
