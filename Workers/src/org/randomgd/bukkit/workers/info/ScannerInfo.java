/**
 * Public Domain.
 */
package org.randomgd.bukkit.workers.info;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.randomgd.bukkit.workers.util.Configuration;

/**
 * Information about a scanning workers.
 */
public abstract class ScannerInfo implements WorkerInfo {

	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 5069611337324963904L;

	private transient int verticalAbove;

	private transient int verticalBelow;

	private transient int horizontalScan;

	/**
	 * Constructor.
	 */
	public ScannerInfo() {
		super();
	}

	/**
	 * Pre-scanning procedure.
	 */
	protected void preScan(World world) {
		// By default, nothing to do.
	}

	/**
	 * Scanning procedure.
	 * 
	 * @param block
	 *            Scanned block.
	 * @param world
	 *            World of the block.
	 * @param xA
	 *            X-coordinate of the specified block.
	 * @param yA
	 *            Y-coordinate of the specified block.
	 * @param zA
	 *            Z-coordinate of the specified block.
	 */
	protected abstract void scan(Block block, World world, int xA, int yA,
			int zA);

	/**
	 * Post scanning procedure.
	 * @param entity TODO
	 */
	protected void postScan(World world, Entity entity) {
		// By default, nothing to do.
	}

	/**
	 * Pre-column scan.
	 */
	protected void preColumnScan(World world) {
		// Byt default, nothing to do.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void perform(Entity entity, int x, int y, int z, World world) {
		if ((y > (256 - verticalAbove)) || (y < verticalBelow)) {
			return;
		}
		preScan(world);

		for (int xOffset = -horizontalScan; xOffset <= horizontalScan; ++xOffset) {
			int xA = x + xOffset;
			for (int zOffset = -horizontalScan; zOffset <= horizontalScan; ++zOffset) {
				int zA = z + zOffset;
				preColumnScan(world);
				for (int yOffset = -verticalBelow; yOffset <= verticalAbove; ++yOffset) {
					int yA = y + yOffset;
					Block block = world.getBlockAt(xA, yA, zA);
					scan(block, world, xA, yA, zA);
				}
			}
		}

		postScan(world, entity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConfiguration(Configuration cnf) {
		verticalAbove = extractVerticalAbove(cnf);
		verticalBelow = extractVerticalBelow(cnf);
		horizontalScan = extractHorizontalScan(cnf);
	}

	/**
	 * Extract vertical scanning parameter value from specified configuration.
	 * 
	 * @param cnf
	 *            Configuration.
	 * @return Value of the vertical scanning parameter.
	 */
	protected int extractVerticalAbove(Configuration cnf) {
		return cnf.getVerticalAbove();
	}

	/**
	 * Extract vertical scanning parameter value from specified configuration.
	 * 
	 * @param cnf
	 *            Configuration.
	 * @return Value of the vertical scanning parameter.
	 */
	protected int extractVerticalBelow(Configuration cnf) {
		return cnf.getVerticalBelow();
	}

	/**
	 * Extract horizontal scanning parameter value from specified configuration.
	 * 
	 * @param cnf
	 *            Configuration.
	 * @return Value of the horizontal scanning.
	 */
	protected int extractHorizontalScan(Configuration cnf) {
		return cnf.getHorizontalRange();
	}

}
