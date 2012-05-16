package org.randomgd.bukkit.workers.info;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Information for a golem.
 */
public class GolemInfo implements WorkerInfo {

	/**
	 * Unique identifier of the class.
	 */
	private static final long serialVersionUID = -5247875151175550610L;

	/**
	 * The only thing the golem could say.
	 */
	private static final String GOLEM_CATCH_PHRASE = ChatColor.ITALIC + ""
			+ ChatColor.DARK_PURPLE + "(Some cracking sound)";

	/**
	 * Torch count.
	 */
	private int torch;

	/**
	 * Constructor.
	 */
	public GolemInfo() {
		torch = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printInfoToPlayer(Player player) {
		player.sendMessage(GOLEM_CATCH_PHRASE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean give(Material material, Player player) {
		boolean result = true;

		switch (material) {
		case TORCH:
			++torch;
			break;
		default:
			result = false;
			break;
		}

		return result;
	}

	@Override
	public void perform(Entity entity, int x, int y, int z, World world) {
		if (torch > 0) {
			// Light up the place with torches.
			Block block = world.getBlockAt(x, y, z);
			Block beyond = world.getBlockAt(x, y - 1, z);
			byte lightLevel = block.getLightFromBlocks();
			if (lightLevel < 8 && block.isEmpty()
					&& (!(beyond.isEmpty() || beyond.isLiquid()))) {
				block.setType(Material.TORCH);
				--torch;
			}
		}
	}

}
