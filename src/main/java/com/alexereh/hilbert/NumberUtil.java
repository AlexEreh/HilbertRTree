package com.alexereh.hilbert;

public final class NumberUtil {
	private static final double DEFAULT_TOLERANCE = 1E-10;

	public static boolean equalsWithTolerance(double first, double second) {
		return Math.abs(first - second) <= DEFAULT_TOLERANCE;
	}

}