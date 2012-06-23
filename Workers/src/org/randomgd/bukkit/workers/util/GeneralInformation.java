/**
 * Public Domain.
 */
package org.randomgd.bukkit.workers.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;

/**
 * General information about materials.
 */
public class GeneralInformation {
	/**
	 * Set of material that can be smelt.
	 */
	public static final Set<Material> SMELTABLE = new HashSet<Material>();
	{
		SMELTABLE.add(Material.CACTUS);
		SMELTABLE.add(Material.CLAY_BALL);
		SMELTABLE.add(Material.COBBLESTONE);
		SMELTABLE.add(Material.GOLD_ORE);
		SMELTABLE.add(Material.IRON_ORE);
		// TODO SMELTABLE.add(Material.LOG);
		SMELTABLE.add(Material.RAW_BEEF);
		SMELTABLE.add(Material.RAW_CHICKEN);
		SMELTABLE.add(Material.RAW_FISH);
		SMELTABLE.add(Material.SAND);
	}

	/**
	 * Set of material a villager is authorized to mine.
	 */
	public static final Set<Material> MINABLE = new HashSet<Material>();
	{
		MINABLE.add(Material.STONE);
		MINABLE.add(Material.GRASS);
		MINABLE.add(Material.DIRT);
		MINABLE.add(Material.COBBLESTONE);
		MINABLE.add(Material.SAND);
		MINABLE.add(Material.GRAVEL);
		MINABLE.add(Material.GOLD_ORE);
		MINABLE.add(Material.IRON_ORE);
		MINABLE.add(Material.COAL_ORE);
		MINABLE.add(Material.LAPIS_ORE);
		MINABLE.add(Material.SANDSTONE);
		MINABLE.add(Material.SOIL);
		MINABLE.add(Material.REDSTONE_ORE);
		MINABLE.add(Material.GLOWING_REDSTONE_ORE);
		MINABLE.add(Material.NETHERRACK);
		MINABLE.add(Material.SOUL_SAND);
		MINABLE.add(Material.GLOWSTONE);
	}

	/**
	 * Set of material requiring iron pickaxe.
	 */
	public static final Set<Material> HARDSTUFF = new HashSet<Material>();
	{
		HARDSTUFF.add(Material.GOLD_ORE);
		HARDSTUFF.add(Material.LAPIS_ORE);
		HARDSTUFF.add(Material.REDSTONE_ORE);
	}

	/**
	 * Constructor.
	 */
	public GeneralInformation() {
		super();
	}
}
