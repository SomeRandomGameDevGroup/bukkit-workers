package org.randomgd.bukkit.workers.info;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.randomgd.bukkit.workers.ToolUsage;
import org.randomgd.bukkit.workers.WorkerHandler;
import org.randomgd.bukkit.workers.util.ChestHandler;

/**
 * Information about a farmer activity and inventory.
 */
public class FarmerInfo extends ScannerInfo {

	/**
	 * Farmer info unique identifier.
	 */
	private static final long serialVersionUID = -9109147287021672895L;

	/**
	 * Message displayed to the player if the villager can't accept more items.
	 */
	private static final String FULL_STACK = ChatColor.GRAY
			+ "I'm full of this. Please keep it.";

	/**
	 * Max amount of transported items.
	 */
	private static int MAX_AMOUNT = 64;

	/**
	 * Amount of stored wheat.
	 */
	private int wheat;

	/**
	 * Amount of stored wheat seed.
	 */
	private int wheatSeed;

	/**
	 * Amount of melon slice.
	 */
	private int melon;

	/**
	 * Amount of melon seed.
	 */
	private int melonSeed;

	/**
	 * Amount of pumpkin.
	 */
	private int pumpkin;

	/**
	 * Amount of pumpkin seed.
	 */
	private int pumpkinSeed;

	/**
	 * Wood log.
	 */
	private int wood;

	/**
	 * Sapling.
	 */
	private int sapling;

	/**
	 * If >0, can create soil.
	 */
	private int hoe;

	/**
	 * If >0, can cut wood.
	 */
	private int axe;

	/**
	 * Amount of sugar cane.
	 */
	private int sugarCane;

	/**
	 * Override marker. Allows specializing farmer into a seeder or a
	 * lumberjack.
	 */
	private Material overrideMarker;

	/**
	 * Last column marker.
	 */
	private transient Material lastMarker;

	/**
	 * Constructor.
	 */
	public FarmerInfo() {
		wheat = 0;
		wheatSeed = 0;
		melon = 0;
		melonSeed = 0;
		pumpkin = 0;
		pumpkinSeed = 0;
		wood = 0;
		sapling = 0;
		hoe = 0;
		axe = 0;
		overrideMarker = null;
	}

	private void makeDeposit(Chest chest) {
		if (wheat > 0) {
			wheat = ChestHandler.deposit(Material.WHEAT, wheat, chest);
		}
		if (melon > 0) {
			melon = ChestHandler.deposit(Material.MELON, melon, chest);
		}
		if (pumpkin > 0) {
			pumpkin = ChestHandler.deposit(Material.PUMPKIN, pumpkin, chest);
		}
		if (sugarCane > 0) {
			sugarCane = ChestHandler.deposit(Material.SUGAR_CANE, sugarCane,
					chest);
		}
		if (wood > 0) {
			wood = ChestHandler.deposit(Material.LOG, wood, chest, (byte) 2);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printInfoToPlayer(Player player) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ChatColor.GRAY);
		if (overrideMarker == null) {
			buffer.append("I'm a farmer.");
		} else {
			if (overrideMarker.equals(Material.GRAVEL)) {
				buffer.append("I'm a seeder.");
			} else {
				buffer.append("I'm a lumberjack.");
			}
		}
		if (hoe > 0) {
			buffer.append(" I can make soil.");
		}
		if (axe > 0) {
			buffer.append(" I can cut trees.");
		}
		if (wheat > 0 || melon > 0 || pumpkin > 0 || sugarCane > 0 || wood > 0) {
			buffer.append(" I have stuff to deposit.");
		}
		player.sendMessage(buffer.toString());
	}

	@Override
	public boolean give(Material material, Player player) {
		boolean result = true;
		// It can't get sapling yet.
		// ## Need an interface change.
		switch (material) {
		case WOOD_HOE:
			hoe += ToolUsage.WOOD.getUsage();
			break;
		case STONE_HOE:
			hoe += ToolUsage.STONE.getUsage();
			break;
		case IRON_HOE:
			hoe += ToolUsage.IRON.getUsage();
			break;
		case GOLD_HOE:
			hoe += ToolUsage.GOLD.getUsage();
			break;
		case DIAMOND_HOE:
			hoe += ToolUsage.DIAMOND.getUsage();
			break;
		case WOOD_AXE:
			axe += ToolUsage.WOOD.getUsage();
			break;
		case STONE_AXE:
			axe += ToolUsage.STONE.getUsage();
			break;
		case IRON_AXE:
			axe += ToolUsage.IRON.getUsage();
			break;
		case GOLD_AXE:
			axe += ToolUsage.GOLD.getUsage();
			break;
		case DIAMOND_AXE:
			axe += ToolUsage.DIAMOND.getUsage();
			break;
		case SEEDS:
			if (wheatSeed < MAX_AMOUNT) {
				++wheatSeed;
			} else {
				player.sendMessage(FULL_STACK);
				result = false;
			}
			break;
		case PUMPKIN_SEEDS:
			if (pumpkinSeed < MAX_AMOUNT) {
				++pumpkinSeed;
			} else {
				player.sendMessage(FULL_STACK);
				result = false;
			}
			break;
		case MELON_SEEDS:
			if (melonSeed < MAX_AMOUNT) {
				++melonSeed;
			} else {
				player.sendMessage(FULL_STACK);
				result = false;
			}
			break;
		case GOLD_NUGGET: {
			// TODO Implement seed dump.
			result = false;
			break;
		}
		case SUGAR_CANE: {
			++sugarCane;
			break;
		}
		case STICK: {
			printInfoToPlayer(player);
			result = false;
			break;
		}
		case WHEAT: {
			if (player.hasPermission("usefulvillagers.jobassign.seeder")) {
				applyOverrideMarker(player, Material.GRAVEL,
						"Let's grow wheat everywhere !");
				result = false;
			} else {
				player.sendMessage(WorkerHandler.NO_TASK_PERMISSION_MESSAGE);
				result = false;
			}
			break;
		}
		case SAPLING: {
			if (player.hasPermission("usefulvillagers.jobassign.lumberjack")) {
				applyOverrideMarker(player, Material.WOOD,
						"Let's cut all birch trees !");
				result = false;
			} else {
				player.sendMessage(WorkerHandler.NO_TASK_PERMISSION_MESSAGE);
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

	private void applyOverrideMarker(Player player, Material targetMarker,
			String message) {
		if (targetMarker.equals(overrideMarker)) {
			overrideMarker = null;
		} else {
			overrideMarker = targetMarker;
		}
		if (overrideMarker != null) {
			player.sendMessage(ChatColor.GRAY + message);
		} else {
			player.sendMessage(ChatColor.GRAY
					+ "Let's act as a regular farmer.");
		}
	}

	private void lookForStuff(Chest chest) {
		Inventory inventory = chest.getInventory();
		int size = inventory.getSize();
		ItemStack[] stacks = inventory.getContents();
		for (int i = 0; i < size; ++i) {
			ItemStack stack = stacks[i];
			if (stack == null) {
				continue;
			}
			int amount = stack.getAmount();
			Material material = stack.getType();
			byte metaData = stack.getData().getData();
			switch (material) {
			// Seeds and sapling.
			case SAPLING: {
				if (metaData == 2) {
					int room = MAX_AMOUNT - sapling;
					if (room > 0) {
						if (room > amount) {
							room = amount;
						}
						sapling += room;
						amount -= room;
						stack.setAmount(amount);
						inventory.setItem(i, stack);
					}
				}
				break;
			}
			case SEEDS: {
				wheatSeed = extractItems(inventory, i, stack, amount, wheatSeed);
				break;
			}
			case MELON_SEEDS: {
				melonSeed = extractItems(inventory, i, stack, amount, melonSeed);
				break;
			}
			case PUMPKIN_SEEDS: {
				pumpkinSeed = extractItems(inventory, i, stack, amount,
						pumpkinSeed);
				break;
			}
			// Tools ! ## Code redundancy !! BAD !!
			case WOOD_HOE: {
				if (extractTools(inventory, i, stack, amount, hoe)) {
					hoe += ToolUsage.WOOD.getUsage();
				}
				break;
			}
			case STONE_HOE: {
				if (extractTools(inventory, i, stack, amount, hoe)) {
					hoe += ToolUsage.STONE.getUsage();
				}
				break;
			}
			case IRON_HOE: {
				if (extractTools(inventory, i, stack, amount, hoe)) {
					hoe += ToolUsage.IRON.getUsage();
				}
				break;
			}
			case GOLD_HOE: {
				if (extractTools(inventory, i, stack, amount, hoe)) {
					hoe += ToolUsage.GOLD.getUsage();
				}
				break;
			}
			case DIAMOND_HOE: {
				if (extractTools(inventory, i, stack, amount, hoe)) {
					hoe += ToolUsage.DIAMOND.getUsage();
				}
				break;
			}

			case WOOD_AXE: {
				if (extractTools(inventory, i, stack, amount, axe)) {
					axe += ToolUsage.WOOD.getUsage();
				}
				break;
			}
			case STONE_AXE: {
				if (extractTools(inventory, i, stack, amount, axe)) {
					axe += ToolUsage.STONE.getUsage();
				}
				break;
			}
			case IRON_AXE: {
				if (extractTools(inventory, i, stack, amount, axe)) {
					axe += ToolUsage.IRON.getUsage();
				}
				break;
			}
			case GOLD_AXE: {
				if (extractTools(inventory, i, stack, amount, axe)) {
					axe += ToolUsage.GOLD.getUsage();
				}
				break;
			}
			case DIAMOND_AXE: {
				if (extractTools(inventory, i, stack, amount, axe)) {
					axe += ToolUsage.DIAMOND.getUsage();
				}
				break;
			}
			default:
				break;
			}
		}
	}

	private boolean extractTools(Inventory inventory, int i, ItemStack stack,
			int amount, int tool) {
		boolean result = false;
		if (tool == 0) {
			stack.setAmount(amount - 1);
			inventory.setItem(i, stack);
			result = true;
		}
		return result;
	}

	private int extractItems(Inventory inventory, int i, ItemStack stack,
			int amount, int curAmount) {
		int result = curAmount;
		int room = MAX_AMOUNT - result;
		if (room > 0) {
			if (room > amount) {
				room = amount;
			}
			result += room;
			amount -= room;
			stack.setAmount(amount);
			inventory.setItem(i, stack);
		}
		return result;
	}

	/**
	 * Perform earth work at the specified location.
	 * 
	 * @param world
	 *            World where the work takes place.
	 * @param xA
	 *            X-coordinate of the block to modify.
	 * @param zA
	 *            Y-coordinate of the block to modify.
	 * @param yA
	 *            Z-coordinate of the block to modify.
	 * @param block
	 *            Block to test.
	 * @param material
	 *            Material of the block to test/modify.
	 * @return true if some work occurs.
	 */
	private boolean doEarthWork(World world, int xA, int zA, int yA,
			Block block, Material material) {
		boolean result = false;
		if ((overrideMarker != null) && (lastMarker == null)) {
			lastMarker = overrideMarker;
		}
		if (lastMarker != null) {
			Block above = world.getBlockAt(xA, yA + 1, zA);
			Material aboveMaterial = above.getType();
			switch (lastMarker) {
			case GRAVEL: // Wheat work.
			{
				if (wheatSeed > 0) {
					// Do we need a hoe ?
					boolean ready = cleanBlock(block, material, above,
							aboveMaterial);

					if (ready) {
						--wheatSeed;
						above.setType(Material.CROPS);
						above.setData((byte) 0);
						result = true;
					}
				}
				break;
			}
			case COBBLESTONE: // Melon work.
			{
				if (melonSeed > 0) {
					boolean ready = cleanBlock(block, material, above,
							aboveMaterial);

					if (ready) {
						--melonSeed;
						above.setType(Material.MELON_STEM);
						above.setData((byte) 0);
						result = true;
					}
				}
				break;
			}
			case SMOOTH_BRICK: // Pumpkin work.
			{
				if (pumpkinSeed > 0) {
					boolean ready = cleanBlock(block, material, above,
							aboveMaterial);

					if (ready) {
						--pumpkinSeed;
						above.setType(Material.PUMPKIN_STEM);
						above.setData((byte) 0);
						result = true;
					}
				}
				break;
			}
			case SANDSTONE: // Sugar cane work.
			{
				result = plantSugarCane(block, material, above, aboveMaterial);
				break;
			}
			case WOOD: // Plant tree.
			{
				if (sapling > 0
						&& cleanBlockForCane(block, material, above,
								aboveMaterial)) {
					--sapling;
					above.setType(Material.SAPLING);
					above.setData((byte) 2);
					result = true;
				}
				break;
			}
			default:
				break;
			}
		}
		return result;
	}

	/**
	 * Try to plan sugar cane.
	 * 
	 * @param block
	 *            Tested block.
	 * @param material
	 *            Material of tested block.
	 * @param above
	 *            Block above the tested one.
	 * @param aboveMaterial
	 *            Material of the block above the tested one.
	 * @return true if the farmer can plant a sugar cane.
	 */
	private boolean plantSugarCane(Block block, Material material, Block above,
			Material aboveMaterial) {
		boolean result = false;
		if (sugarCane > 0) {
			boolean ready = cleanBlockForCane(block, material, above,
					aboveMaterial);

			if (ready) {
				--sugarCane;
				above.setType(Material.SUGAR_CANE_BLOCK);
				above.setData((byte) 0);
				result = true;
			}
		}
		return result;
	}

	/**
	 * Check if the block is clean for sugar cane plant.
	 * 
	 * @param block
	 *            Tested block.
	 * @param material
	 *            Tested block material.
	 * @param above
	 *            Block above the tested block.
	 * @param aboveMaterial
	 *            Material of the block above the tested one.
	 * @return true if the tested block can be used for planting sugar cane.
	 */
	private boolean cleanBlockForCane(Block block, Material material,
			Block above, Material aboveMaterial) {
		boolean ready = false;

		if (!Material.SOIL.equals(material)
				&& (Material.LONG_GRASS.equals(aboveMaterial)
						|| Material.SNOW.equals(aboveMaterial) || Material.AIR
							.equals(aboveMaterial))) {
			above.setType(Material.AIR);
			above.setData((byte) 0);
			ready = true;
		}
		return ready;
	}

	/**
	 * Check if the block is clean for earth work.
	 * 
	 * @param block
	 *            Tested block.
	 * @param material
	 *            Tested block material.
	 * @param above
	 *            Block above the tested block.
	 * @param aboveMaterial
	 *            Material of the block above the tested one.
	 * @return true if the tested block can be used for earth work.
	 */
	private boolean cleanBlock(Block block, Material material, Block above,
			Material aboveMaterial) {
		boolean ready = true;

		if (!Material.SOIL.equals(material)) {
			// It's not soil ! We need to make room.
			if (Material.LONG_GRASS.equals(aboveMaterial)
					|| Material.SNOW.equals(aboveMaterial)
					|| Material.AIR.equals(aboveMaterial)) {
				above.setType(Material.AIR);
				above.setData((byte) 0);
				if (hoe > 0) {
					--hoe;
					block.setType(Material.SOIL);
				} else {
					ready = false;
				}
			} else {
				ready = false;
			}
		} else {
			// It's already soil !
			ready = !(Material.CROPS.equals(aboveMaterial)
					|| Material.MELON_STEM.equals(aboveMaterial) || Material.PUMPKIN_STEM
					.equals(aboveMaterial));
		}
		return ready;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void scan(Block block, World world, int xA, int yA, int zA) {
		Material material = block.getType();
		byte metaData = block.getData();

		// So, What can we sense ?
		/*
		 * Markers are the following : - Gravel enable wheat work. - Sandstone
		 * enable sugar cane work. - Wood enable tree work. - Cobblestone enable
		 * melon work. - Stone brick enable pumpkin work.
		 */

		switch (material) {
		// Markers detection.
		case GRAVEL:
		case SANDSTONE:
		case WOOD:
		case COBBLESTONE:
		case SMOOTH_BRICK:
			lastMarker = material;
			break;

		// Harvest part.
		case CROPS: {
			// Whatever happens, we harvest crops.
			// Markers are only for tool use.
			if (metaData == (byte) 7) { // Ready to harvest.
				++wheat;
				wheatSeed += (int) (Math.random() * 2);
				if (wheatSeed > MAX_AMOUNT) {
					wheatSeed = MAX_AMOUNT;
				}
				// We assume that crop harvesting will procure at
				// least one seed.
				block.setData((byte) 0);
			}
			break;
		}
		case SUGAR_CANE_BLOCK: {
			// We first met sugar cane.
			// All the sugar cane block above will be harvest.
			int topLocation;
			for (topLocation = yA; topLocation < 255; ++topLocation) {
				Block logBlock = world.getBlockAt(xA, topLocation, zA);
				if (!logBlock.getType().equals(Material.SUGAR_CANE_BLOCK)) {
					--topLocation;
					break;
				}
			}
			// And now, chop the wood !
			for (int yB = topLocation; yB > yA; --yB) {
				Block toCut = world.getBlockAt(xA, yB, zA);
				toCut.setType(Material.AIR);
				++sugarCane;
			}
			break;
		}
		case MELON_BLOCK: {
			// The melon harvesting is a bit tweaked.
			// We lower the slice amount, but automatically add
			// a chance to get a melon seed.
			block.setType(Material.AIR);
			melon += 1 + (int) (Math.random() * 6);
			melonSeed += (int) (Math.random() * 1.5);
			if (melonSeed > MAX_AMOUNT) {
				melonSeed = MAX_AMOUNT;
			}
			break;
		}
		case PUMPKIN: {
			block.setType(Material.AIR);
			++pumpkin;
			// For now, pumpkin seeds can only be provided by
			// players.
			break;
		}

		// Work part.
		// This is where we require tools.
		case DIRT:
		case GRASS:
		case SOIL:
			if (doEarthWork(world, xA, zA, yA, block, material)) {
				lastMarker = null;
			}
			break;
		case SAND: {
			if (Material.SANDSTONE.equals(lastMarker)) {
				Block above = world.getBlockAt(xA, yA + 1, zA);
				Material aboveMaterial = above.getType();

				if (plantSugarCane(block, material, above, aboveMaterial)) {
					lastMarker = null;
				}
			}
			break;
		}
		case LOG: {
			if ((Material.WOOD.equals(lastMarker) || Material.WOOD
					.equals(overrideMarker)) && (metaData == 2) && (axe > 0)) {
				// Time to cut wood. But not like a dumbass.
				// First, look for the top.
				int topLocation;
				for (topLocation = yA; topLocation < 255; ++topLocation) {
					Block logBlock = world.getBlockAt(xA, topLocation, zA);
					if ((!logBlock.getType().equals(Material.LOG))
							|| (logBlock.getData() != (byte) 2)) {
						--topLocation;
						break;
					}
				}
				// And now, chop the wood !
				for (int yB = topLocation; (yB >= yA) && (axe > 0); --yB) {
					Block toCut = world.getBlockAt(xA, yB, zA);
					toCut.setType(Material.AIR);
					--axe;
					++wood;
				}
				block.setType(Material.SAPLING);
				block.setData((byte) 2);
			}
			break;
		}
		case CHEST: {
			// Time for deposit.
			Chest chest = (Chest) block.getState();
			makeDeposit(chest);
			// And for retrieval.
			// (Tools, seeds, saplings).
			lookForStuff(chest);
			break;
		}
		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preColumnScan(World world) {
		lastMarker = null;
	}

}
