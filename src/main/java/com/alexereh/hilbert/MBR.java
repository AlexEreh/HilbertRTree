package com.alexereh.hilbert;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;


@Getter
@Setter
@ToString
@SuppressWarnings("unused")
public final class MBR implements Comparable<MBR> {

	/**
	 * -- GETTER --
	 *
	 */
	private double minX;

	/**
	 * -- GETTER --
	 *
	 */
	private double maxX;

	/**
	 * -- GETTER --
	 */
	private double minY;

	/**
	 * -- GETTER --
	 */
	private double maxY;

	public MBR() {
		init();
	}

	public MBR(double x1, double x2, double y1, double y2) {
		init(x1, x2, y1, y2);
	}

	public MBR(Coordinate p1, Coordinate p2) {
		init(p1.getX(), p2.getX(), p1.getY(), p2.getY());
	}

	public MBR(Coordinate p) {
		init(p.getX(), p.getX(), p.getY(), p.getY());
	}

	public MBR(MBR env) {
		init(env);
	}

	public static boolean intersects(Coordinate p1, Coordinate p2, Coordinate q) {
		double qX = q.getX();
		double qY = q.getY();
		double p1X = p1.getX();
		double p1Y = p1.getY();
		double p2X = p2.getX();
		double p2Y = p2.getY();


		return ((qX >= (Math.min(p1X, p2X))) && (qX <= (Math.max(p1X, p2X))))
				&& ((qY >= (Math.min(p1Y, p2Y))) && (qY <= (Math.max(p1Y, p2Y))));
	}

	public static boolean intersects(Coordinate p1, Coordinate p2, Coordinate q1, Coordinate q2) {
		double q1X = q1.getX();
		double q2X = q2.getX();
		double q1Y = q1.getY();
		double q2Y = q2.getY();
		double p1X = p1.getX();
		double p1Y = p1.getY();
		double p2X = p2.getX();
		double p2Y = p2.getY();

		double minQ = Math.min(q1X, q2X);
		double maxQ = Math.max(q1X, q2X);
		double minP = Math.min(p1X, p2X);
		double maxP = Math.max(p1X, p2X);

		if (minP > maxQ) return false;
		if (maxP < minQ) return false;

		minQ = Math.min(q1Y, q2Y);
		maxQ = Math.max(q1Y, q2Y);
		minP = Math.min(p1Y, p2Y);
		maxP = Math.max(p1Y, p2Y);

		if (minP > maxQ) return false;
		if (maxP < minQ) return false;
		return true;
	}

	public void init() {
		setToNull();
	}

	public void init(double x1, double x2, double y1, double y2) {
		if (x1 < x2) {
			minX = x1;
			maxX = x2;
		} else {
			minX = x2;
			maxX = x1;
		}
		if (y1 < y2) {
			minY = y1;
			maxY = y2;
		} else {
			minY = y2;
			maxY = y1;
		}
	}

	public MBR copy() {
		return new MBR(this);
	}

	public void init(Coordinate p1, Coordinate p2) {
		init(p1.getX(), p2.getX(), p1.getY(), p2.getY());
	}

	public void init(Coordinate p) {
		init(p.getX(), p.getX(), p.getY(), p.getY());
	}

	public void init(MBR env) {
		this.minX = env.minX;
		this.maxX = env.maxX;
		this.minY = env.minY;
		this.maxY = env.maxY;
	}


	public void setToNull() {
		minX = 0;
		maxX = -1;
		minY = 0;
		maxY = -1;
	}

	public boolean isNull() {
		return maxX < minX;
	}

	public double getWidth() {
		if (isNull()) {
			return 0;
		}
		return maxX - minX;
	}

	public double getHeight() {
		if (isNull()) {
			return 0;
		}
		return maxY - minY;
	}

	public double getDiameter() {
		if (isNull()) {
			return 0;
		}
		double w = getWidth();
		double h = getHeight();
		return Math.hypot(w, h);
	}

	public double getArea() {
		return getWidth() * getHeight();
	}

	public void translate(double transX, double transY) {
		if (isNull()) {
			return;
		}
		init(getMinX() + transX, getMaxX() + transX, getMinY() + transY, getMaxY() + transY);
	}

	public Coordinate centre() {
		if (isNull()) return null;
		return new Coordinate((getMinX() + getMaxX()) / 2.0, (getMinY() + getMaxY()) / 2.0);
	}

	public MBR intersection(MBR env) {
		if (isNull() || env.isNull() || !intersects(env)) return new MBR();

		double intMinX = Math.max(minX, env.minX);
		double intMinY = Math.max(minY, env.minY);
		double intMaxX = Math.min(maxX, env.maxX);
		double intMaxY = Math.min(maxY, env.maxY);
		return new MBR(intMinX, intMaxX, intMinY, intMaxY);
	}

	public boolean intersects(MBR other) {
		if (isNull() || other.isNull()) {
			return false;
		}
		return !(other.minX > maxX || other.maxX < minX || other.minY > maxY || other.maxY < minY);
	}


	public boolean intersects(Coordinate a, Coordinate b) {
		if (isNull()) {
			return false;
		}

		double mbrMinX = Math.min(a.getX(), b.getX());
		if (mbrMinX > maxX) return false;

		double mbrMaxX = Math.max(a.getX(), b.getX());
		if (mbrMaxX < minX) return false;

		double mbrMinY = Math.min(a.getY(), b.getY());
		if (mbrMinY > maxY) return false;

		double mbrMaxY = Math.max(a.getY(), b.getY());
		if (mbrMaxY < minY) return false;

		return true;
	}

	public boolean disjoint(MBR other) {
		return !intersects(other);
	}

	public boolean intersects(Coordinate p) {
		return intersects(p.getX(), p.getY());
	}

	public boolean intersects(double x, double y) {
		if (isNull()) return false;
		return !(x > maxX || x < minX || y > maxY || y < minY);
	}

	public boolean contains(MBR other) {
		return covers(other);
	}

	public boolean contains(Coordinate p) {
		return covers(p);
	}

	public boolean contains(double x, double y) {
		return covers(x, y);
	}

	public boolean containsProperly(MBR other) {
		if (equals(other)) return false;
		return covers(other);
	}

	public boolean covers(double x, double y) {
		if (isNull()) return false;
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	public boolean covers(Coordinate p) {
		return covers(p.getX(), p.getY());
	}

	public boolean covers(MBR other) {
		if (isNull() || other.isNull()) {
			return false;
		}
		return other.getMinX() >= minX && other.getMaxX() <= maxX && other.getMinY() >= minY && other.getMaxY() <= maxY;
	}

	public double distance(MBR env) {
		if (intersects(env)) return 0;

		double dx = 0.0;
		if (maxX < env.minX) dx = env.minX - maxX;
		else if (minX > env.maxX) dx = minX - env.maxX;

		double dy = 0.0;
		if (maxY < env.minY) dy = env.minY - maxY;
		else if (minY > env.maxY) dy = minY - env.maxY;

		// if either is zero, the MBRs overlap either vertically or horizontally
		if (dx == 0.0) return dy;
		if (dy == 0.0) return dx;
		return Math.hypot(dx, dy);
	}

	public boolean equals(Object other) {
		if (!(other instanceof MBR otherMBR)) {
			return false;
		}
		if (isNull()) {
			return otherMBR.isNull();
		}
		return maxX == otherMBR.getMaxX() && maxY == otherMBR.getMaxY() && minX == otherMBR.getMinX() && minY == otherMBR.getMinY();
	}
	public void expandToInclude(Coordinate p)
	{
		expandToInclude(p.getX(), p.getY());
	}

	public void expandToInclude(double x, double y) {
		if (isNull()) {
			minX = x;
			maxX = x;
			minY = y;
			maxY = y;
		}
		else {
			if (x < minX) {
				minX = x;
			}
			if (x > maxX) {
				maxX = x;
			}
			if (y < minY) {
				minY = y;
			}
			if (y > maxY) {
				maxY = y;
			}
		}
	}

	public void expandToInclude(MBR other) {
		if (other.isNull()) {
			return;
		}
		if (isNull()) {
			minX = other.getMinX();
			maxX = other.getMaxX();
			minY = other.getMinY();
			maxY = other.getMaxY();
		}
		else {
			if (other.minX < minX) {
				minX = other.minX;
			}
			if (other.maxX > maxX) {
				maxX = other.maxX;
			}
			if (other.minY < minY) {
				minY = other.minY;
			}
			if (other.maxY > maxY) {
				maxY = other.maxY;
			}
		}
	}
	public int compareTo(@NotNull MBR env) {

		// compare nulls if present
		if (isNull()) {
			if (env.isNull()) return 0;
			return -1;
		} else {
			if (env.isNull()) return 1;
		}
		// compare based on numerical ordering of ordinates
		if (minX < env.minX) return -1;
		if (minX > env.minX) return 1;
		if (minY < env.minY) return -1;
		if (minY > env.minY) return 1;
		if (maxX < env.maxX) return -1;
		if (maxX > env.maxX) return 1;
		if (maxY < env.maxY) return -1;
		if (maxY > env.maxY) return 1;
		return 0;


	}
}