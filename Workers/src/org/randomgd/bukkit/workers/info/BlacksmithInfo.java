package org.randomgd.bukkit.workers.info;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import org.randomgd.bukkit.workers.ToolUsage;
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

	/**
	 * Information displayed if the villager is a miner.
	 */
	private static final String MINER_INFO = ChatColor.GRAY + "I'm a miner.";

	/**
	 * Information displayed if the villager is a miner and can mine.
	 */
	private static final String BASIC_MINING_INFO = ChatColor.GRAY
			+ "I can mine.";

	/**
	 * Information displayed if the villager is a miner but can't mine.
	 */
	private static final String NO_MINING_INFO = ChatColor.GRAY
			+ "But I can't mine anything !";

	/**
	 * Information displayed in the villager is a miner that can mine hard
	 * blocks.
	 */
	private static final String HARD_STUFF_MINING_INFO = ChatColor.GRAY
			+ "I can break hard stuff.";

	// Seriously : I should make a 'ScanningEntityInfo' to mutualize this.
	private transient int horizontalScan;

	private transient int verticalBelow;

	private transient int verticalAbove;

	private transient Set<Furnace> furnaces;

	private transient Set<Chest> chest;

	private transient List<Location> miningColumn;

	/**
	 * Amount of stone axe usage left.
	 */
	private int stonePickaxe;

	/**
	 * Amount of iron axe usage left.
	 */
	private int ironPickaxe;

	/**
	 * Mining authorization.
	 */
	private boolean canMine;

	/**
	 * Last mining date (for cooldown).
	 */
	private transient long lastMining;

	/**
	 * current cooldown.
	 */
	private transient long currentCooldown;

	/**
	 * Mining cooldown configuration.
	 */
	private transient int miningCooldown;

	/**
	 * Maximum mining depth.
	 */
	private transient int miningDepth;

	/**
	 * Constructor.
	 */
	public BlacksmithInfo() {
		stonePickaxe = 0;
		ironPickaxe = 0;
		canMine = false;
	}

	@Override
	public void printInfoToPlayer(Player player) {
		if (!canMine) {
			player.sendMessage(GENERAL_INFO);
		} else {
			player.sendMessage(MINER_INFO);
			if (stonePickaxe + ironPickaxe == 0) {
				player.sendMessage(NO_MINING_INFO);
			} else {
				if (ironPickaxe > 0) {
					player.sendMessage(HARD_STUFF_MINING_INFO);
				} else {
					player.sendMessage(BASIC_MINING_INFO);
				}
			}
		}
	}

	@Override
	public boolean give(Material material, Player player) {
		boolean result = false;
		switch (material) {
		case STICK: {
			printInfoToPlayer(player);
			break;
		}
		case GOLD_PICKAXE: {
			if (player.hasPermission("usefulvillagers.jobassign.miner")) {
				// Set the blacksmith to miner sub-task (from / to).
				String message;
				canMine = !canMine;

				if (canMine) {
					message = ChatColor.GRAY + "Let's mine !";
				} else {
					message = ChatColor.GRAY + "Ok, I stop mining now.";
				}
				player.sendMessage(message);
			} else {
				// TODO Change NO_*_PERMISSION_MESSAGE location to a neutral
				// class to avoid coupling.
				player.sendMessage(WorkerHandler.NO_TASK_PERMISSION_MESSAGE);
			}
			break;
		}
		case STONE_PICKAXE: {
			if (canMine) {
				stonePickaxe += ToolUsage.STONE.getUsage();
				result = true;
			}
			break;
		}
		case IRON_PICKAXE: {
			if (canMine) {
				ironPickaxe += ToolUsage.IRON.getUsage();
				result = true;
			}
			break;
		}
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
		miningColumn.clear();

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
					case IRON_BLOCK: {
						if (canMine) {
							// Mining column !
							miningColumn.add(block.getLocation());
						}
						break;
					}
					default:
						break;
					}
				}
			}
		}

		boolean hasOneSlotFree = false;

		if (chest.isEmpty()) {
			return;
		} else {
			// Look for at least one free slot.
			hasOneSlotFree = ChestHandler.hasFreeSlot(chest);
		}

		if (!furnaces.isEmpty()) {
			doFurnaceWork();
		}

		if (hasOneSlotFree && canMine) {
			checkPickaxes();

			doMineWork(world);
		}
	}

	/**
	 * Perform mine work based on scanned area.
	 * 
	 * @param world
	 *            Hosting world.
	 */
	private void doMineWork(World world) {
		long now = System.currentTimeMillis();

		// Manage cooldown.
		if (currentCooldown > 0) {
			long elapsed = now - lastMining;
			if (elapsed > currentCooldown) {
				currentCooldown = 0;
			}
		}

		if ((currentCooldown < 1) && !miningColumn.isEmpty()
				&& (stonePickaxe + ironPickaxe > 0)) {
			// Let's mine !
			// Get the column with the highest level.
			Location toMine = miningColumn.get(0);
			int height = 0;
			for (Location i : miningColumn) {
				int blockX = i.getBlockX();
				int blockZ = i.getBlockZ();
				int blockY = i.getBlockY() - 1;
				int depth = 1;

				while ((blockY > 0) && (depth < miningDepth)) {
					++depth;
					Block blk = world.getBlockAt(blockX, blockY, blockZ);
					if (blk.isEmpty()) {
						--blockY;
					} else {
						// Is it a block to mine ?
						Material bType = blk.getType();
						if (WorkerHandler.MINABLE.contains(bType)) {
							// Interesting.
							if ((blockY > height)
									&& (ironPickaxe > 0 || !WorkerHandler.HARDSTUFF
											.contains(bType))) {
								height = blockY;
								toMine = i;
							}
						}
						break;
					}
				}
			}

			// Do the mining duty
			int blockX = toMine.getBlockX();
			int blockZ = toMine.getBlockZ();
			Block toBreak = world.getBlockAt(blockX, height, blockZ);
			Material breakType = toBreak.getType();
			boolean mineIt = shallMine(breakType);
			if (mineIt) {
				mineBlock(now, toBreak);
			}
		}
	}

	/**
	 * Mine a block and use surroundings to store the result.
	 * 
	 * @param now
	 *            Mine time.
	 * @param toBreak
	 *            Block to mine.
	 */
	private void mineBlock(long now, Block toBreak) {
		lastMining = now;
		// TODO Cooldown table per material ?
		currentCooldown = miningCooldown;
		Collection<ItemStack> result = toBreak.getDrops();
		for (ItemStack i : result) {
			Material rType = i.getType();
			int amount = i.getAmount();
			ChestHandler.deposit(rType, amount, chest);
			// ## As we've checked that there's at least one free
			// slot, we're sure that nothing left.
			toBreak.setType(Material.AIR);
		}
	}

	/**
	 * Depending on the material type and the available tools, decide if the
	 * villager can mine or not.
	 * 
	 * @param type
	 *            Material type to mine.
	 * @return <code>true</code> if the villager is able to mine this.
	 */
	private boolean shallMine(Material type) {
		boolean mineIt = false;
		if (WorkerHandler.MINABLE.contains(type)) {
			if (WorkerHandler.HARDSTUFF.contains(type)) {
				if (ironPickaxe > 0) {
					--ironPickaxe;
					mineIt = true;
				}
			} else {
				if (stonePickaxe > 0) {
					--stonePickaxe;
					mineIt = true;
				} else if (ironPickaxe > 0) {
					--ironPickaxe;
					mineIt = true;
				}
			}

		}
		return mineIt;
	}

	/**
	 * Check tool state. If there's chests near, try to get some tool.
	 */
	private void checkPickaxes() {
		// Get pickaxes in chest if needed.
		if (stonePickaxe == 0) {
			int pickaxe = ChestHandler.get(Material.STONE_PICKAXE, 1, chest);
			if (pickaxe > 0) {
				stonePickaxe += ToolUsage.STONE.getUsage();
			}
		}

		if (ironPickaxe == 0) {
			// ## Code duplication is BAD ! Hopefully, it's beta ...
			int pickaxe = ChestHandler.get(Material.IRON_PICKAXE, 1, chest);
			if (pickaxe > 0) {
				ironPickaxe += ToolUsage.IRON.getUsage();
			}
		}
	}

	/**
	 * Perform furnace work (get result, refuel).
	 */
	private void doFurnaceWork() {
		// First, clear furnaces.
		cleanFurnaces();

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
				checkFurnace(fInv);
			}
		}
	}

	/**
	 * Check a furnace for new/current job.
	 * @param inventory Furnace inventory.
	 */
	private void checkFurnace(FurnaceInventory inventory) {
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
		startFurnaceJob(inventory, nextJob, maxToGet);
	}

	/**
	 * Start a new furnace job.
	 * @param inventory Furnace inventory.
	 * @param type Type of material to smelt.
	 * @param toTransfer Amount of material to transfer in furnace.
	 */
	private void startFurnaceJob(FurnaceInventory inventory, Material type,
			int toTransfer) {
		if (type != null) {
			// We've got a new job.
			int amount = storeJob(inventory, type, toTransfer, 0);
			// Got some fuel ?
			ItemStack fuel = inventory.getFuel();
			if ((fuel == null) || (fuel.getAmount() < 1)) {
				storeNewFuel(inventory, amount);
			} // No fuel addition.
		}
	}

	/**
	 * Clear surrounding furnaces.
	 */
	private void cleanFurnaces() {
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
	}

	/**
	 * Store stuff for chest to furnace smelting slot.
	 * 
	 * @param fInv
	 *            Furnace inventory.
	 * @param material
	 *            Material to store.
	 * @param toRetrieve
	 *            Amount to retrieve.
	 * @param amount
	 *            Amount already in the furnace.
	 * @return Transfered amount of stuff.
	 */
	private int storeJob(FurnaceInventory fInv, Material material,
			int toRetrieve, int amount) {
		int result = amount;
		int retrieved = ChestHandler.get(material, toRetrieve, chest);
		result += retrieved;
		fInv.setSmelting(new ItemStack(material, result));
		return result;
	}

	/**
	 * Store fuel found from chest to furnace.
	 * 
	 * @param fInv
	 *            Furnace inventory.
	 * @param stored
	 *            Stored material amount.
	 */
	private void storeNewFuel(FurnaceInventory fInv, int stored) {
		int toRetrieve = stored / 8;
		int retrieved = ChestHandler.get(Material.COAL, toRetrieve, chest);
		if (retrieved != 0) {
			// ## TODO: check about fuel type (coal OR charcoal).
			ItemStack fuel = new ItemStack(Material.COAL, retrieved);
			fInv.setFuel(fuel);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConfiguration(Configuration cnf) {
		// Nothing special.
		// Room based stuff will occur later.
		Configuration.Blacksmith blk = cnf.getBlacksmithConfiguration();
		horizontalScan = blk.getHorizontalRange();
		verticalAbove = blk.getVerticalAbove();
		verticalBelow = blk.getVerticalBelow();
		furnaces = new HashSet<Furnace>();
		chest = new HashSet<Chest>();
		miningColumn = new LinkedList<Location>();
		lastMining = System.currentTimeMillis();
		currentCooldown = 0;
		miningCooldown = blk.getMiningCooldown();
		miningDepth = blk.getMiningDepth();
	}

}
