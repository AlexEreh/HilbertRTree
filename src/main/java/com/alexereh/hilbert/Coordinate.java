package com.alexereh.hilbert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class Coordinate implements Comparable<Coordinate>, Cloneable {

	public static final double NULL_ORDINATE = Double.NaN;

	public static final int X = 0;

	public static final int Y = 1;

	private double x = 0.0;

	private double y = 0.0;

	public Coordinate(Coordinate c) {
		this(c.x, c.y);
	}
	public double getOrdinate(int ordinateIndex) {
		return switch (ordinateIndex) {
			case X -> x;
			case Y -> y;
			default -> throw new IllegalArgumentException("Invalid ordinate index: " + ordinateIndex);
		};
	}
	public void setOrdinate(int ordinateIndex, double value) {
		switch (ordinateIndex) {
			case X -> setX(value);
			case Y -> setY(value);
			default -> throw new IllegalArgumentException("Invalid ordinate index: " + ordinateIndex);
		}
	}
	public boolean isValid() {
		if (!Double.isFinite(x)) return false;
		return Double.isFinite(y);
	}

	public boolean equals2D(Coordinate c) {
		if (!NumberUtil.equalsWithTolerance(this.x, c.x)) {
			return false;
		}
		return NumberUtil.equalsWithTolerance(this.y, c.y);
	}

	public boolean equals(Object other) {
		if (!(other instanceof Coordinate)) {
			return false;
		}
		return equals2D((Coordinate) other);
	}

	public int compareTo(Coordinate o) {
		if (x < o.x) return -1;
		if (x > o.x) return 1;
		return Double.compare(y, o.y);
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public Object clone() {
		try {
			return super.clone(); // return the clone
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	public Coordinate copy() {
		return new Coordinate(this);
	}

	public double distanceTo(Coordinate c) {
		double dx = x - c.x;
		double dy = y - c.y;
		return Math.hypot(dx, dy);
	}
	public int hashCode() {
		return Objects.hash(x, y);
	}

}