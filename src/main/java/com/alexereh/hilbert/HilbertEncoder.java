package com.alexereh.hilbert;

public final class HilbertEncoder {
	private final int level;
	private final double minx;
	private final double miny;
	private final double strideX;
	private final double strideY;

	public HilbertEncoder(int level, MBR extent) {
		this.level = level;
		int hSide = (int) Math.pow(2, level) - 1;

		minx = extent.getMinX();
		strideX = extent.getWidth() / hSide;

		miny = extent.getMinY();
		strideY = extent.getHeight() / hSide;
	}

	public int encode(MBR env) {
		double midX = env.getWidth() / 2 + env.getMinX();
		int x = (int) ((midX - minx) / strideX);

		double midY = env.getHeight() / 2 + env.getMinY();
		int y = (int) ((midY - miny) / strideY);

		return HilbertCode.encode(level, x, y);
	}

}
