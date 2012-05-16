package org.randomgd.bukkit.workers;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * A nice utility class for chest handling.
 */
public final class ChestHandler {

	/**
	 * Make a deposit of a specified amount of material.
	 * 
	 * @param material
	 *            Material to deposit.
	 * @param amount
	 *            Amount of material.
	 * @param chest
	 *            Targeted chest.
	 * @return Remaining item count.
	 */
	public static int deposit(Material material, int amount, Chest chest) {
		int result = amount;
		Inventory inventory = chest.getInventory();
		ItemStack[] stacks = inventory.getContents();
		int size = inventory.getSize();
		int maxSize = inventory.getMaxStackSize();
		for (int sI = 0; (sI < size) && (result > 0); ++sI) {
			int curAmount = result;
			if (curAmount > maxSize) {
				curAmount = maxSize;
			}
			ItemStack s = stacks[sI];
			if (s != null) {
				if (s.getType().equals(material)) {
					int stackSize = s.getAmount();
					int room = maxSize - stackSize;
					if (room > curAmount) {
						room = curAmount;
					}
					s.setAmount(stackSize + room);
					result -= room;
				}
			} else {
				ItemStack newone = new ItemStack(material, curAmount);
				result -= curAmount;
				inventory.setItem(sI, newone);
			}
		}
		return result;
	}

	/**
	 * Make a deposit of a specified amount of material.
	 * 
	 * @param material
	 *            Material to deposit.
	 * @param amount
	 *            Amount of material.
	 * @param chest
	 *            Targeted chest.
	 * @param data
	 *            Meta-data.
	 * @return Remaining item count.
	 */
	public static int deposit(Material material, int amount, Chest chest,
			byte data) {
		int result = amount;
		Inventory inventory = chest.getInventory();
		ItemStack[] stacks = inventory.getContents();
		int size = inventory.getSize();
		for (int sI = 0; (sI < size) && (result > 0); ++sI) {
			int curAmount = result;
			if (curAmount > 64) {
				curAmount = 64;
			}
			ItemStack s = stacks[sI];
			if (s != null) {
				if (s.getType().equals(material)
						&& (s.getData().getData() == data)) {
					int stackSize = s.getAmount();
					int room = 64 - stackSize;
					if (room > curAmount) {
						room = curAmount;
					}
					s.setAmount(stackSize + room);
					result -= room;
				}
			} else {
				MaterialData materialData = new MaterialData(material, data);
				ItemStack newone = materialData.toItemStack(curAmount);
				result -= curAmount;
				inventory.setItem(sI, newone);
			}
		}
		return result;
	}

}
