package org.randomgd.bukkit.workers.info;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.randomgd.bukkit.workers.util.ChestHandler;
import org.randomgd.bukkit.workers.util.Configuration;

/**
 * Information about butchers !
 */
public class ButcherInfo implements WorkerInfo {

	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 1080484770417065531L;

	/**
	 * Shears.
	 */
	private int shears;

	/**
	 * Stored wool.
	 */
	private int[] wool;

	/**
	 * Entity check radius.
	 */
	private transient int radius;

	private transient int horizontalScan;

	private transient int verticalBelow;

	private transient int verticalAbove;

	private transient long lastShear;

	private transient int shearPeriod;

	/**
	 * Constructor.
	 */
	public ButcherInfo() {
		wool = new int[16];
	}

	@Override
	public void printInfoToPlayer(Player player) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ChatColor.GRAY + "I'm a butcher.");
		if (shears > 0) {
			buffer.append(" I can shave sheeps.");
		}
		boolean gotWool = false;
		for (int i = 0; i < 16; ++i) {
			gotWool |= (wool[i] > 0);
		}
		if (gotWool) {
			buffer.append(" I've got some wool to deposit.");
		}
		player.sendMessage(buffer.toString());
	}

	@Override
	public boolean give(Material material, Player player) {
		boolean result = false;
		switch (material) {
		case SHEARS: {
			shears += 238;
			result = true;
			break;
		}
		case STICK:
			printInfoToPlayer(player);
			break;
		// TODO Wheat.
		default:
			break;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(Entity entity, int x, int y, int z, World world) {
		// First, shave the sheeps !
		long now = System.currentTimeMillis();
		long sinceLast = now - lastShear;
		boolean canShear = sinceLast > shearPeriod;

		if (canShear) {
			List<Entity> entities = entity.getNearbyEntities(radius, 2, radius);
			for (Entity i : entities) {
				if (EntityType.SHEEP.equals(i.getType())) {
					Sheep sheep = (Sheep) i;
					if ((shears > 0) && (!sheep.isSheared())) {
						DyeColor color = sheep.getColor();
						byte data = color.getData();
						wool[data] += 1 + ((int) (Math.random() * 2.0));
						sheep.setSheared(true);
						--shears;
					}
				}
			}
			lastShear = now;
		}

		// Scanning.
		for (int xOffset = -horizontalScan; xOffset <= horizontalScan; ++xOffset) {
			int xA = x + xOffset;
			for (int zOffset = -horizontalScan; zOffset <= horizontalScan; ++zOffset) {
				int zA = z + zOffset;
				for (int yOffset = -verticalBelow; yOffset <= verticalAbove; ++yOffset) {
					int yA = y + yOffset;
					Block block = world.getBlockAt(xA, yA, zA);
					Material material = block.getType();
					if (Material.CHEST.equals(material)) {
						Chest chest = (Chest) block.getState();
						// Time to deposit.
						for (int j = 0; j < 16; ++j) {
							wool[j] = ChestHandler.deposit(Material.WOOL,
									wool[j], chest, (byte) j);
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConfiguration(Configuration cnf) {
		radius = cnf.getButcherRadius();
		horizontalScan = cnf.getHorizontalRange();
		verticalAbove = cnf.getVerticalAbove();
		verticalBelow = cnf.getVerticalBelow();
		shearPeriod = cnf.getButcherShearPeriod();
		lastShear = System.currentTimeMillis();
	}

}
