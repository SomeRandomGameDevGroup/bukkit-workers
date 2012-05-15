package org.randomgd.bukkit.workers;

import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Generic interface for worker information.
 */
public interface WorkerInfo extends Serializable {

	/**
	 * Display worker info to player.
	 * 
	 * @param player
	 *            Player receiving the message.
	 */
	void printInfoToPlayer(Player player);

	/**
	 * @return <code>true</code> if the worker can work.
	 */
	boolean canWork();

	/**
	 * Give something to the worker.
	 * 
	 * @param material
	 *            item to give.
	 * @return <code>true</code> if it accepts it.
	 */
	boolean give(Material material);

	/**
	 * Performs worker task.
	 * 
	 * @param entity
	 *            Worker.
	 * @param x
	 *            X-coordinate.
	 * @param y
	 *            Y-coordinate.
	 * @param z
	 *            Z-coordinate.
	 * @param world
	 *            Hosting world.
	 */
	void perform(Entity entity, int x, int y, int z, World world);

}
