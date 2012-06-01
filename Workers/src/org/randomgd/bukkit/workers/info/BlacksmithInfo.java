package org.randomgd.bukkit.workers.info;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.randomgd.bukkit.workers.WorkerHandler;
import org.randomgd.bukkit.workers.util.ChestHandler;
import org.randomgd.bukkit.workers.util.Configuration;

/**
 * Information about blacksmith activity.
 */
public class BlacksmithInfo implements WorkerInfo {

	/**
	 * Unique Identifier.
	 */
	private static final long serialVersionUID = -1090800876410748785L;

	/**
	 * General info displayed to the player.
	 */
	private static final String GENERAL_INFO = ChatColor.GRAY
			+ "I'm a blacksmith.";

	// Seriously : I should make a 'ScanningEntityInfo' to mutualize this.
	private transient int horizontalScan;

	private transient int verticalBelow;

	private transient int verticalAbove;

	private transient Set<Furnace> furnaces;

	private transient Set<Chest> chest;

	public BlacksmithInfo() {
		// Nothing yet.
	}

	@Override
	public void printInfoToPlayer(Player player) {
		player.sendMessage(GENERAL_INFO);
	}

	@Override
	public boolean give(Material material, Player player) {
		if (Material.STICK.equals(material)) {
			printInfoToPlayer(player);
		}
		return false;
	}

	@Override
	public void perform(Entity entity, int x, int y, int z, World world) {
		if ((y > 252) || (y < verticalBelow)) { // Magic number
			// A farmer which is too high to operate.
			return;
		}
		// Let's find a furnace AND a chest.
		// The blacksmith (for a change) does not store a thing.
		// It will transfer stuff from chest to furnace and from furnace to
		// chest.
		furnaces.clear();
		chest.clear();

		for (int xOffset = -horizontalScan; xOffset <= horizontalScan; ++xOffset) {
			int xA = x + xOffset;
			for (int zOffset = -horizontalScan; zOffset <= horizontalScan; ++zOffset) {
				int zA = z + zOffset;
				for (int yOffset = -verticalBelow; yOffset <= verticalAbove; ++yOffset) {
					int yA = y + yOffset;
					Block block = world.getBlockAt(xA, yA, zA);
					Material material = block.getType();
					switch (material) {
					case FURNACE: {
						Furnace furnace = (Furnace) block.getState();
						furnaces.add(furnace);
						break;
					}
					case CHEST: {
						Chest ch = (Chest) block.getState();
						chest.add(ch);
						break;
					}
					default:
						break;
					}
				}
			}
		}

		if (furnaces.isEmpty() || chest.isEmpty()) {
			return;
		}

		// First, clear furnaces.
		for (Furnace i : furnaces) {
			FurnaceInventory fInv = i.getInventory();
			ItemStack result = fInv.getResult();
			if ((result != null) && (result.getAmount() != 0)) {
				// There's something to store.
				Material material = result.getType();
				int toStore = result.getAmount();
				for (Chest j : chest) {
					toStore = ChestHandler.deposit(material, toStore, j);
					if (toStore == 0) {
						break;
					}
				}
				result.setAmount(toStore);
				fInv.setResult(result);
			}
		}

		// Then, look for furnace that need fuel or awaiting jobs.
		for (Furnace i : furnaces) {
			FurnaceInventory fInv = i.getInventory();
			ItemStack job = fInv.getSmelting();
			if ((job != null) && (job.getAmount() > 0)) {
				// If we're here, then there's obviously no fuel left.

				// First, refill with the same material.
				Material material = job.getType();
				if (Material.LOG.equals(material)) {
					// TODO Log management. For now, just skip this. There's
					// metadata to take into account.
					continue;
				}
				int amount = job.getAmount();
				int toRetrieve = job.getMaxStackSize() - amount;
				if (toRetrieve > 0) {
					amount = storeJob(fInv, material, toRetrieve, amount);
				}

				storeNewFuel(fInv, amount);
			} else {
				// The furnace is ready for a new job.
				Material nextJob = null;
				int maxToGet = 0;
				for (Chest j : chest) {
					Inventory cInv = j.getInventory();
					int size = cInv.getSize();
					for (int k = 0; k < size; ++k) {
						ItemStack stack = cInv.getItem(k);
						if (stack != null) {
							int amount = stack.getAmount();
							if (amount > 0) {
								Material material = stack.getType();
								if (WorkerHandler.SMELTABLE.contains(material)) {
									// Here we go. Stack this.
									nextJob = material;
									maxToGet = stack.getMaxStackSize();
									break;
								}
							}
						}
					}
					if (nextJob != null) {
						break;
					}
				}
				if (nextJob != null) {
					// We've got a new job.
					int amount = storeJob(fInv, nextJob, maxToGet, 0);
					// Got some fuel ?
					ItemStack fuel = fInv.getFuel();
					if ((fuel == null) || (fuel.getAmount() < 1)) {
						storeNewFuel(fInv, amount);
					} // No fuel addition.
				}
			}
		}

	}

	private int storeJob(FurnaceInventory fInv, Material material,
			int toRetrieve, int amount) {
		int result = amount;
		int retrieved = ChestHandler.get(material, toRetrieve, chest);
		result += retrieved;
		fInv.setSmelting(new ItemStack(material, result));
		return result;
	}

	private void storeNewFuel(FurnaceInventory fInv, int stored) {
		int toRetrieve = stored / 8;
		int retrieved = ChestHandler.get(Material.COAL, toRetrieve, chest);
		if (retrieved != 0) {
			// ## TODO: check about fuel type (coal OR charcoal).
			ItemStack fuel = new ItemStack(Material.COAL, retrieved);
			fInv.setFuel(fuel);
		}
	}

	@Override
	public void setConfiguration(Configuration cnf) {
		// Nothing special.
		// Room based stuff will occur later.
		horizontalScan = cnf.getHorizontalRange();
		verticalAbove = cnf.getVerticalAbove();
		verticalBelow = cnf.getVerticalBelow();
		furnaces = new HashSet<Furnace>();
		chest = new HashSet<Chest>();
	}

}
