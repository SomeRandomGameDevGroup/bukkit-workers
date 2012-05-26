package org.randomgd.bukkit.workers.util;

public class SimplePosition implements Comparable<SimplePosition> {

	private volatile int hashCode = 0;

	private int x;
	private int y;
	private int z;

	private int l;

	public SimplePosition(int pX, int pY, int pZ, int pl) {
		x = pX;
		y = pY;
		z = pZ;
		l = pl;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public int getL() {
		return l;
	}

	@Override
	public int compareTo(SimplePosition other) {
		boolean equal = (x == other.x) && (y == other.y) && (z == other.z);
		return equal ? 0 : 1;
	}

	@Override
	public boolean equals(Object obj) {

		boolean equal = false;
		if (obj.getClass() == SimplePosition.class) {
			SimplePosition other = (SimplePosition) obj;
			equal = (x == other.x) && (y == other.y) && (z == other.z);
		}
		return equal;
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			int result = 17;
			result = 37 * result + x;
			result = 37 * result + y;
			result = 37 * result + z;
			hashCode = result;
		}

		return hashCode;
	}

	@Override
	public String toString() {
		return String.format("(%d, %d, %d)", x, y, z);
	}
}
