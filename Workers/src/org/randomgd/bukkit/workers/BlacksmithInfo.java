package org.randomgd.bukkit.workers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
 * Information about blacksmith activity and inventory.
 */
public class BlacksmithInfo implements WorkerInfo {

	/**
	 * !unique identifier.
	 */
	private static final long serialVersionUID = 1745736212911013020L;

	/**
	 * Needed coal for charcoal production.
	 */
	private static final int NEEDED_COAL_STOCK = 8;

	/**
	 * Utility map for crafting needs.
	 */
	private static final Map<Material, ToolNeed> TOOL_NEEDS = new HashMap<Material, ToolNeed>();
	{
		// ## Don't do this at home kids. It's harmful.
		ToolNeed.set(TOOL_NEEDS, Material.WOOD_AXE, 2, 3, 0, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.STONE_AXE, 2, 0, 3, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.IRON_AXE, 2, 0, 0, 3, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.GOLD_AXE, 2, 0, 0, 0, 3, 0);
		ToolNeed.set(TOOL_NEEDS, Material.DIAMOND_AXE, 2, 0, 0, 0, 0, 3);

		ToolNeed.set(TOOL_NEEDS, Material.WOOD_PICKAXE, 2, 3, 0, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.STONE_PICKAXE, 2, 0, 3, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.IRON_PICKAXE, 2, 0, 0, 3, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.GOLD_PICKAXE, 2, 0, 0, 0, 3, 0);
		ToolNeed.set(TOOL_NEEDS, Material.DIAMOND_PICKAXE, 2, 0, 0, 0, 0, 3);

		ToolNeed.set(TOOL_NEEDS, Material.WOOD_HOE, 2, 2, 0, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.STONE_HOE, 2, 0, 2, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.IRON_HOE, 2, 0, 0, 2, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.GOLD_HOE, 2, 0, 0, 0, 2, 0);
		ToolNeed.set(TOOL_NEEDS, Material.DIAMOND_HOE, 2, 0, 0, 0, 0, 2);

		ToolNeed.set(TOOL_NEEDS, Material.WOOD_SPADE, 2, 1, 0, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.STONE_SPADE, 2, 0, 1, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.IRON_SPADE, 2, 0, 0, 1, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.GOLD_SPADE, 2, 0, 0, 0, 1, 0);
		ToolNeed.set(TOOL_NEEDS, Material.DIAMOND_SPADE, 2, 0, 0, 0, 0, 1);

		ToolNeed.set(TOOL_NEEDS, Material.WOOD_SWORD, 1, 2, 0, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.STONE_SWORD, 1, 0, 2, 0, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.IRON_SWORD, 1, 0, 0, 2, 0, 0);
		ToolNeed.set(TOOL_NEEDS, Material.GOLD_SWORD, 1, 0, 0, 0, 2, 0);
		ToolNeed.set(TOOL_NEEDS, Material.DIAMOND_SWORD, 1, 0, 0, 0, 0, 2);
	}

	/**
	 * Axe.
	 */
	private int tool;

	/**
	 * Possessed wood log.
	 */
	private int log;

	/**
	 * Spare wood.
	 */
	private int wood;

	/**
	 * Useable coal or charcoal.
	 */
	private int useableCoal;

	/**
	 * Charcoal to deposit.
	 */
	private int charcoal;

	/**
	 * Cobblestone.
	 */
	private int cobble;

	/**
	 * Iron ingot.
	 */
	private int iron;

	/**
	 * Gold ingot.
	 */
	private int gold;

	/**
	 * Diamond.
	 */
	private int diamond;

	/**
	 * Sticks.
	 */
	private int stick;

	/**
	 * Tool demands.
	 */
	private Map<UUID, Material> demand;

	/**
	 * Tools ready to be supplied.
	 */
	private Map<UUID, Material> supply;

	/**
	 * Constructor.
	 */
	public BlacksmithInfo() {
		tool = 0;
		log = 0;
		useableCoal = 0;
		charcoal = 0;
		cobble = 0;
		iron = 0;
		gold = 0;
		diamond = 0;
		stick = 0;
		wood = 0;
		demand = new HashMap<UUID, Material>();
		supply = new HashMap<UUID, Material>();
	}

	@Override
	public void printInfoToPlayer(Player player) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ChatColor.GRAY);
		buffer.append("I'm a blacksmith.");
		if (!demand.isEmpty()) {
			buffer.append(" I've got some work to do.");
		}
		if (!supply.isEmpty()) {
			UUID playerId = player.getUniqueId();
			buffer.append(" I've some tools to deliver.");

			Material tool = supply.get(playerId);
			if (tool != null) {
				buffer.append(" And I've got your command. Here it is.");
				Inventory inventory = player.getInventory();
				ItemStack stack = new ItemStack(tool, 1);
				int slot = inventory.firstEmpty();
				if (slot >= 0) {
					inventory.setItem(slot, stack);
				}
				supply.remove(playerId);
			}
		}
		player.sendMessage(buffer.toString());
	}

	@Override
	public boolean canWork() {
		// Don't need any tool (by now). A blacksmith chops trees BY HAND !
		return true;
	}

	@Override
	public boolean give(Material material) {
		boolean result = true;
		switch (material) {
		case COBBLESTONE:
			++cobble;
			break;
		case COAL:
			if (useableCoal < NEEDED_COAL_STOCK) {
				++useableCoal;
			} else {
				result = false;
			}
			break;
		case IRON_INGOT:
			++iron;
			break;
		case GOLD_INGOT:
			++gold;
			break;
		case DIAMOND:
			++diamond;
			break;
		case LOG:
			++log;
			break;
		case WOOD:
			++wood;
			break;
		default:
			result = false;
			break;
		}
		if (result) {
			preventSticks();
		}
		return result;
	}

	private void preventSticks() {
		// Count how many sticks we might need.
		int needed = 0;
		synchronized (demand) {
			for (Material material : demand.values()) {
				switch (material) {
				case WOOD_AXE:
				case WOOD_HOE:
				case WOOD_PICKAXE:
				case WOOD_SPADE:
				case STONE_AXE:
				case STONE_HOE:
				case STONE_PICKAXE:
				case STONE_SPADE:
				case IRON_AXE:
				case IRON_HOE:
				case IRON_PICKAXE:
				case IRON_SPADE:
				case GOLD_AXE:
				case GOLD_HOE:
				case GOLD_PICKAXE:
				case GOLD_SPADE:
				case DIAMOND_AXE:
				case DIAMOND_HOE:
				case DIAMOND_PICKAXE:
				case DIAMOND_SPADE:
					needed += 2;
					break;
				case WOOD_SWORD:
				case STONE_SWORD:
				case GOLD_SWORD:
				case IRON_SWORD:
				case DIAMOND_SWORD:
					++needed;
					break;
				default:
					break;
				}
			}
			// So, we need THAT amount of stick.
			if (stick >= needed) {
				return;
			}
			needed -= stick;
			// Time to make some.
			// How many wood would it take ?
			int neededWood = (int) Math.ceil(needed / 2.0);
			if (neededWood > wood) {
				// Not enough wood. Produce from logs.
				int neededLog = (int) Math.ceil((neededWood - wood) / 4.0);
				if (neededLog > log) {
					neededLog = log; // Can do better.
				}
				log -= neededLog;
				wood += neededLog * 4;
			}
			if (neededWood > wood) {
				neededWood = wood;
			}
			wood -= neededWood;
			stick += neededWood * 2; // Ok, it's a shortcut. We should have
										// ensure to have a least
			// an even number of wood to produce four sticks.
		}

	}

	@Override
	public void perform(Entity entity, int x, int y, int z, World world) {
		if ((y > 252) || (y < 5)) {
			return;
		}

		for (int xOffset = -2; xOffset < 3; ++xOffset) {
			int xA = x + xOffset;
			for (int zOffset = -2; zOffset < 3; ++zOffset) {
				int zA = z + zOffset;
				for (int yOffset = -2; yOffset < 3; ++yOffset) {
					int yA = y + yOffset;
					Block block = world.getBlockAt(xA, yA, zA);
					Material material = block.getType();
					boolean marked = false;
					switch (material) {
					case IRON_BLOCK: {
						// The blacksmith marker !
						// It says that block above this one can be used by the
						// blacksmith.
						marked = true;
						break;
					}
					case CHEST: {
						// TODO get needed material for tool supply.
						Chest chest = (Chest) block.getState();
						charcoal = ChestHandler.deposit(Material.COAL,
								charcoal, chest, (byte) 1);
						if (marked) {
							// Let's take some need raw material.
							// TODO.
						}
						break;
					}
					case FURNACE: {
						if (marked) {
							// Only use marked furnace.
							// Furnace furnace = (Furnace) block.getState();
							// TODO Furnace usage.
						}
						break;
					}
					case LOG: {
						if ((marked) && (block.getData() == 2)) {
							// By contract, if we met LOG here, it means that
							// it's the first valid log we met.
							// So chop it off with the edge of our hands.
							for (int yB = yA; yB < 255; ++yB) {
								Block toCut = world.getBlockAt(xA, yB, zA);
								Material blockMaterial = toCut.getType();
								byte blockData = toCut.getData();
								if (blockMaterial.equals(Material.LOG)
										&& (blockData == (byte) 2)) {
									++log;
									toCut.setType(Material.AIR);
									toCut.setData((byte) 0); // ## Really ?
								} else {
									break;
								}
							}
						}
						break;
					}
					case WORKBENCH: {
						// Do we have demands ?
						// By contract, we know that we've crafted stick as soon
						// as a demand
						// as been done. But, we might lack some.
						produceTools();
						break;
					}
					default:
						break;
					}
				}
			}
		}
	}

	private void produceTools() {
		synchronized (demand) {
			Set<Map.Entry<UUID, Material>> entrySet = demand.entrySet();
			Iterator<Map.Entry<UUID, Material>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Map.Entry<UUID, Material> i = iterator.next();
				UUID client = i.getKey();
				Material command = i.getValue();
				ToolNeed needs = TOOL_NEEDS.get(command);
				if ((needs != null)
						&& needs.grant(stick, wood, cobble, iron, gold, diamond)) {
					stick -= needs.getStick();
					wood -= needs.getWood();
					cobble -= needs.getCobble();
					iron -= needs.getIron();
					gold -= needs.getGold();
					diamond -= needs.getDiamond();
					iterator.remove();
					supply.put(client, command);
				}
			}
		}
	}

}
