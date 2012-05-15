package org.randomgd.bukkit.workers;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Information about a farmer activity and inventory.
 */
public class FarmerInfo implements WorkerInfo {

	/**
	 * Farmer info unique identifier.
	 */
	private static final long serialVersionUID = -9109147287021672895L;

	// Amount of stored wheat.
	private int wheat;

	// Amount of stored wheat seed.
	private int seed;

	// Amount of melon slice.
	private int melon;

	// Amount of pumkin.
	private int pumpkin;

	// Tool usage. At 0, no tool ! Can work !
	private int tool;

	/**
	 * Constructor.
	 */
	public FarmerInfo() {
		wheat = 0;
		seed = 0;
		melon = 0;
		pumpkin = 0;
		tool = 0;
	}

	public void useTool() {
		--tool;
		if (tool < 0) {
			tool = 0;
		}
	}

	public boolean plantSeed() {
		boolean result = (tool > 0) && (seed > 0);
		if (result) {
			--tool;
			--seed;
		}
		return result;
	}

	/**
	 * When harvesting a wheat, it got a chance to get some extra seeds.
	 */
	public void harvestWheat() {
		++wheat;
		seed += (Math.random() * 2);
	}

	public int getWheat() {
		return wheat;
	}

	public void makeDeposit(Chest chest) {
		if (wheat > 0) {
			wheat = ChestHandler.deposit(Material.WHEAT, wheat, chest);
		}
		if (melon > 0) {
			melon = ChestHandler.deposit(Material.MELON, melon, chest);
		}
		if (pumpkin > 0) {
			pumpkin = ChestHandler.deposit(Material.PUMPKIN, pumpkin, chest);
		}
	}

	public void harvestMelon() {
		melon += 1 + (Math.random() * 7);
	}

	public int getMelon() {
		return melon;
	}

	public void harvestPumpkin() {
		++pumpkin;
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
				case WOOD_HOE:
					amount = ToolUsage.WOOD.getUsage();
					break;
				case STONE_HOE:
					amount = ToolUsage.STONE.getUsage();
					break;
				case IRON_HOE:
					amount = ToolUsage.IRON.getUsage();
					break;
				case GOLD_HOE:
					amount = ToolUsage.GOLD.getUsage();
					break;
				case DIAMOND_HOE:
					amount = ToolUsage.DIAMOND.getUsage();
					break;
				case SEEDS: {
					if (seed == 0) {
						seed += stack.getAmount();
						stack.setAmount(0);
						inventory.setItem(sI, stack);
					}
					break;
				}
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printInfoToPlayer(Player player) {
		player.sendMessage(ChatColor.GRAY + "I'm a farmer."
				+ ((tool > 0) ? " I've got a tool." : " But I can't work !")
				+ " (" + tool + ")");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canWork() {
		return (tool > 0);
	}

	@Override
	public boolean give(Material material) {
		boolean result = true;
		switch (material) {
		case WOOD_HOE:
			tool += ToolUsage.WOOD.getUsage();
			break;
		case STONE_HOE:
			tool += ToolUsage.STONE.getUsage();
			break;
		case IRON_HOE:
			tool += ToolUsage.IRON.getUsage();
			break;
		case GOLD_HOE:
			tool += ToolUsage.GOLD.getUsage();
			break;
		case DIAMOND_HOE:
			tool += ToolUsage.DIAMOND.getUsage();
			break;
		case SEEDS:
			seed += 1;
			break;
		default:
			result = false;
			break;
		}
		return result;
	}

	@Override
	public void perform(Entity entity, int x, int y, int z, World world) {
		// So ! Check the blocks !
		// A farmer can operate on blocks around him, at its level up to two
		// level up.
		if (y > 252) { // Magic number
			// A farmer which is too high can operate.
			return;
		}
		makeFarmWork(x, y, z, world);
		if (tool == 0) {
			gatherFarmTool(x, y, z, world);
		}
	}

	private void gatherFarmTool(int x, int y, int z, World world) {
		for (int i = -1; i < 2; ++i) {
			for (int j = -1; j < 2; ++j) {
				for (int k = -1; k < 2; ++k) {
					Block block = world.getBlockAt(x + i, y + k, z + j);
					Material material = block.getType();

					if (material.equals(Material.CHEST)) {
						// Look for tool !
						Chest chest = (Chest) block.getState();
						getTool(chest);
						if (tool > 0) {
							return;
						}
					}
				}
			}
		}
	}

	private boolean checkBlock(World world, int x, int y, int z,
			Material material) {
		boolean result = false;
		Block block = world.getBlockAt(x, y, z);
		result = block.getType().equals(material);
		return result;
	}

	private boolean checkPlaceholder(World world, int x, int y, int z,
			Material material) {
		boolean result;
		result = checkBlock(world, x + 1, y, z, material)
				|| checkBlock(world, x - 1, y, z, material)
				|| checkBlock(world, x, y, z + 1, material)
				|| checkBlock(world, x, y, z - 1, material);
		return result;
	}

	private void makeFarmWork(int x, int y, int z, World world) {
		for (int i = -1; i < 2; ++i) {
			for (int j = -1; j < 2; ++j) {
				for (int k = -1; k < 2; ++k) {
					Block block = world.getBlockAt(x + i, y + k, z + j);
					Material material = block.getType();
					// System.out.println("Interact with " + material);
					switch (material) {
					case CROPS: {
						// Get the crop info.
						byte data = block.getData();
						if (data == 7) {
							// Ready to harvest.
							// But first, check the item stack.
							harvestWheat();
							block.setData((byte) 0); // Re-seed.
						}
						break;
					}
					case MELON_BLOCK: {
						block.setType(Material.AIR);
						harvestMelon();
						break;
					}
					case PUMPKIN: {
						block.setType(Material.AIR);
						harvestPumpkin();
						break;
					}
					case SOIL:
					case GRASS:
					case DIRT: {
						// System.out.println("Try to work earth");
						int xA = x + i;
						int yA = y + k + 1;
						int zA = z + j;
						Block above = world.getBlockAt(xA, yA, zA);
						Material aboveType = above.getType();
						switch (aboveType) {
						case AIR: {
							if (tool > 0) {
								// Ok, it's air. BUT !
								// It might be a placeholder for something else.
								boolean placehold = checkPlaceholder(world, xA,
										yA, zA, Material.MELON_STEM)
										| checkPlaceholder(world, xA, yA, zA,
												Material.PUMPKIN_STEM);
								if ((!placehold) && plantSeed()) {
									block.setType(Material.SOIL);
									above.setType(Material.CROPS);
									above.setData((byte) 0);
								}
							}
							break;
						}
						case SNOW:
						case LONG_GRASS: {
							above.breakNaturally();
							break;
						}
						default:
							break;
						}
						break;
					}
					case CHEST: {
						Chest chest = (Chest) block.getState();
						makeDeposit(chest);
						break;
					}
					default:
						break;
					}
				}
			}
		}
	}

}
