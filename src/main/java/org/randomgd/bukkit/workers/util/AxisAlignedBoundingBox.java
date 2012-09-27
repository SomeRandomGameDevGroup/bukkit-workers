package org.randomgd.bukkit.workers.util;

public class AxisAlignedBoundingBox {
	private int minX;
	private int maxX;

	private int minY;
	private int maxY;

	private int minZ;
	private int maxZ;

	public AxisAlignedBoundingBox(SimplePosition p) {
		minX = maxX = p.getX();
		minY = maxY = p.getY();
		minZ = maxZ = p.getZ();
	}

	public void add(SimplePosition p) {
		int x = p.getX();
		int y = p.getY();
		int z = p.getZ();
		if (minX > x) {
			minX = x;
		} else if (maxX < x) {
			maxX = x;
		}
		if (minY > y) {
			minY = y;
		} else if (maxY < y) {
			maxY = y;
		}
		if (minZ > z) {
			minZ = z;
		} else if (maxZ < z) {
			maxZ = z;
		}
	}

	public boolean contains(int x, int y, int z) {
		boolean result = true;
		result &= (x <= maxX);
		result &= (x >= minX);
		result &= (y <= maxY);
		result &= (y >= minY);
		result &= (z <= maxZ);
		result &= (z >= minZ);
		return result;
	}
}
