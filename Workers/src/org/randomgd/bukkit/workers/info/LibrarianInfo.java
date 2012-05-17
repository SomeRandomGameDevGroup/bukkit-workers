package org.randomgd.bukkit.workers.info;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.randomgd.bukkit.workers.ToolUsage;
import org.randomgd.bukkit.workers.util.ChestHandler;
import org.randomgd.bukkit.workers.util.Configuration;

/**
 * Information about a librarian activity and inventory.
 */
public class LibrarianInfo implements WorkerInfo {

	/**
	 * Unique Class Identifier.
	 */
	private static final long serialVersionUID = -3102616957479388299L;

	private int tool;

	private int cane;

	private int book;

	private int wood;

	/**
	 * Constructor.
	 */
	public LibrarianInfo() {
		tool = 0;
		cane = 0;
		book = 0;
		wood = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printInfoToPlayer(Player player) {
		// ## It should be better to pre-build the information string, store it
		// and use a string mutualizer to avoid memory consumption ...
		StringBuffer buffer = new StringBuffer();
		buffer.append(ChatColor.GRAY);
		buffer.append("I'm a librarian.");
		if (tool > 0) {
			buffer.append(" I can cut some wood.");
		}
		if (cane > 0) {
			buffer.append(" I carry some sugar cane.");
		}
		if (book > 0) {
			buffer.append(" I have some book to deposit.");
		}
		player.sendMessage(buffer.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean give(Material material, Player player) {
		boolean result = true;
		switch (material) {
		case SUGAR_CANE:
			++cane;
			break;
		case WOOD_AXE:
			tool += ToolUsage.WOOD.getUsage();
			break;
		case STONE_AXE:
			tool += ToolUsage.STONE.getUsage();
			break;
		case IRON_AXE:
			tool += ToolUsage.IRON.getUsage();
			break;
		case GOLD_AXE:
			tool += ToolUsage.GOLD.getUsage();
			break;
		case DIAMOND_AXE:
			tool += ToolUsage.DIAMOND.getUsage();
			break;
		case LOG:
			++wood;
			break;
		case GOLD_NUGGET: {
			if (book > 0) {
				int amount = book;
				if (amount > 64) {
					amount = 64;
				}
				ItemStack drop = new ItemStack(Material.BOOK, amount);
				book -= amount;
				Inventory inventory = player.getInventory();
				int slot = inventory.firstEmpty();
				if (slot >= 0) {
					inventory.setItem(slot, drop);
				} else {
					result = false;
				}
			} else {
				result = false;
			}
			break;
		}
		default:
			result = false;
			break;
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(Entity entity, int x, int y, int z, World world) {
		if ((y > 252) || (y < 5)) {
			return;
		}
		for (int xOffset = -2; xOffset < 3; ++xOffset) {
			int xA = x + xOffset;
			for (int zOffset = -2; zOffset < 3; ++zOffset) {
				int zA = z + zOffset;
				boolean gotLog = false;
				boolean checkEmpty = false;
				boolean wasEmpty = false;
				for (int yOffset = 3; yOffset > -2; --yOffset) {
					int yA = y + yOffset;
					Block block = world.getBlockAt(xA, yA, zA);
					Material material = block.getType();
					switch (material) {
					case SUGAR_CANE_BLOCK: {
						// Base rule : Only get a sugar cane block if
						// there's one sugar cane block below and air above.
						if (!checkEmpty) {
							Block above = world.getBlockAt(xA, yA + 1, zA);
							checkEmpty = true;
							wasEmpty = above.isEmpty();
						}
						if (wasEmpty) {
							Block below = world.getBlockAt(xA, yA - 1, zA);
							if (below.getType().equals(
									Material.SUGAR_CANE_BLOCK)) {
								// It's ok, we can take it.
								++cane;
								block.setType(Material.AIR);
							}
						}
						break; // ## Ooooh man, that's not nice. It looks like a
								// bug nest to me !
					}
					case LOG: {
						// Ok but, which kind of log ?
						gotLog = block.getData() == 2; // It's birch !
						break;
					}
					case GRASS:
					case DIRT: {
						if (gotLog) {
							// Check the block below.
							// It's a signal to the librarian that this tree
							// must be chopped.
							Block below = world.getBlockAt(xA, yA - 1, zA);
							if (below.getType().equals(Material.BRICK)) {
								// It's the signal ! Chop it out !!
								boolean plant = true;
								for (int i = yA + 1; i < 255; ++i) {
									Block toChop = world.getBlockAt(xA, i, zA);
									if ((tool > 0)
											&& (toChop.getType()
													.equals(Material.LOG))
											&& (toChop.getData() == (byte) 2)) {
										--tool;
										++wood;
										toChop.setType(plant ? Material.SAPLING
												: Material.AIR);
										if (plant) {
											toChop.setData((byte) 2);
										}
										plant &= false;
									} else {
										break;
									}
								}
							}
						}
						break;
					}
					case WORKBENCH: {
						int generated = cane / 3;
						book += generated;
						cane %= 3;
						break;
					}
					case CHEST: {
						// Time to chest fun !
						Chest chest = (Chest) block.getState();
						book = ChestHandler.deposit(Material.BOOK, book, chest);
						wood = ChestHandler.deposit(Material.LOG, wood, chest,
								(byte) 2);
						if (tool == 0) {
							getTool(chest);
						}
					}
					default:
						break;
					}
					if (!checkEmpty) {
						checkEmpty = true;
						wasEmpty = block.isEmpty();
					}
				}
			}
		}
	}

	public void getTool(Chest chest) {
		Inventory inventory = chest.getInventory();
		ItemStack[] stacks = inventory.getContents();
		int size = inventory.getSize();
		for (int sI = 0; sI < size; ++sI) {
			ItemStack stack = stacks[sI];
			if (stack != null) {
				Material material = stack.getType();
				int amount = 0;
				switch (material) {
				case WOOD_AXE:
					amount = ToolUsage.WOOD.getUsage();
					break;
				case STONE_AXE:
					amount = ToolUsage.STONE.getUsage();
					break;
				case IRON_AXE:
					amount = ToolUsage.IRON.getUsage();
					break;
				case GOLD_AXE:
					amount = ToolUsage.GOLD.getUsage();
					break;
				case DIAMOND_AXE:
					amount = ToolUsage.DIAMOND.getUsage();
					break;
				default:
					break;
				}
				if (amount > 0) {
					int sAmount = stack.getAmount();
					stack.setAmount(sAmount - 1);
					inventory.setItem(sI, stack);
					tool += amount;
					break;
				}
			}
		}
	}

	@Override
	public void setConfiguration(Configuration cnf) {
		// TODO ...
	}
}
