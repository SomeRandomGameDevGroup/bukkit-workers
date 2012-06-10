package org.randomgd.bukkit.workers.util;

import java.util.Collection;

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
	 * Make a deposit of the specified amount of material.
	 * 
	 * @param material
	 *            Type of material to deposit.
	 * @param amount
	 *            Amount.
	 * @param chest
	 *            List of chest.
	 * @return Remaining item count.
	 */
	public static int deposit(Material material, int amount,
			Collection<Chest> chest) {
		int result = amount;

		for (Chest i : chest) {
			result = deposit(material, amount, i);
			if (result == 0) {
				break;
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
					int room = s.getMaxStackSize() - stackSize;
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

	/**
	 * Get item from chest.
	 * 
	 * @param material
	 *            Type of item to retrieve.
	 * @param amount
	 *            Maximum amount of material to retrieve.
	 * @param chest
	 *            Chest.
	 * @return Amount of retrieved item.
	 */
	public static int get(Material material, int amount, Chest chest) {
		int result = 0;
		int toGet = amount;
		Inventory inventory = chest.getInventory();
		int size = inventory.getSize();
		for (int i = 0; i < size; ++i) {
			ItemStack stack = inventory.getItem(i);
			if (stack != null) {
				Material m = stack.getType();
				int available = stack.getAmount();
				if ((available > 0) && material.equals(m)) {
					if (available > toGet) {
						available = toGet;
					}
					result += available;
					toGet -= available;
					stack.setAmount(stack.getAmount() - available);
					inventory.setItem(i, stack);
				}
			}
		}
		return result;
	}

	/**
	 * Get items from a set of chests.
	 * 
	 * @param material
	 *            Material to get.
	 * @param toRetrieve
	 *            Amount of stuff to retrieve.
	 * @param chest
	 *            List of chests.
	 * @return Amount of retrieved stuff.
	 */
	public static int get(Material material, int toRetrieve,
			Collection<Chest> chest) {
		int result = 0;
		for (Chest j : chest) {
			int retrieved = ChestHandler.get(material, toRetrieve, j);
			result += retrieved;
			toRetrieve -= retrieved;
			if (toRetrieve == 0) {
				break;
			}
		}
		return result;
	}

	/**
	 * Look for a free slot in the supplied collection of chests.
	 * 
	 * @param chest
	 *            Collection of chests.
	 * @return true if there's a least one free slot.
	 */
	public static boolean hasFreeSlot(Collection<Chest> chest) {
		boolean result = false;

		for (Chest i : chest) {
			Inventory inventory = i.getInventory();
			if (inventory.firstEmpty() >= 0) {
				result = true;
				break;
			}
		}
		return result;
	}
}
