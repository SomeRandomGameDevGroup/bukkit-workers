/**
 * Public Domain.
 */
package org.randomgd.bukkit.workers.info;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.randomgd.bukkit.workers.util.Configuration;

/**
 * Priest job information.
 */
public class PriestInfo extends ScannerInfo {

	/**
	 * Serialisation identifier.
	 */
	private static final long serialVersionUID = 9050587055805295508L;

	/**
	 * Number of performable healing.
	 */
	private int healingPower;

	/**
	 * Number of performable burning.
	 */
	private int burningPower;

	/**
	 * Burning spell cooldown.
	 */
	private transient long burningCooldown;

	/**
	 * Healing spell cooldown.
	 */
	private transient long healingCooldown;

	/**
	 * Last time the priest perform an healing spell.
	 */
	private transient long lastHealing;

	/**
	 * Last time the priest perform a burning spell.
	 */
	private transient long lastBurning;

	/**
	 * Healing spell per glowstone dust.
	 */
	private transient int healPerDust;

	/**
	 * Burning spell per blaze rod.
	 */
	private transient int burnPerRod;

	/**
	 * Spell radius.
	 */
	private transient int radius;

	/**
	 * Check nearby entities period.
	 */
	private transient int period;

	/**
	 * Last entity check.
	 */
	private transient long lastCheck;

	/**
	 * Burning power (number of ticks).
	 */
	private transient int burnPower;

	/**
	 * List of nearby brewing stands.
	 */
	private transient List<BrewingStand> stands;

	/**
	 * List of nearby chests.
	 */
	private transient List<Chest> chests;

	/**
	 * Constructor.
	 */
	public PriestInfo() {
		super();
		healingPower = 0;
		burningPower = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printInfoToPlayer(Player player) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ChatColor.GRAY);
		buffer.append("I'm a priest.");
		if (healingPower > 0) {
			buffer.append(" I can heal people.");
		}
		if (burningPower > 0) {
			buffer.append(" I can burn nasty monsters.");
		}
		player.sendMessage(buffer.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean give(Material material, Player player) {
		if (material == null) {
			return false;
		}
		boolean result = false;
		switch (material) {
		case GLOWSTONE_DUST: {
			healingPower += healPerDust;
			result = true;
			break;
		}
		case BLAZE_ROD: {
			burningPower += burnPerRod;
			result = true;
			break;
		}
		case STICK: {
			printInfoToPlayer(player);
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
	protected void scan(Block block, World world, int xA, int yA, int zA) {
		Material material = block.getType();
		if (Material.CHEST.equals(material)) {
			chests.add((Chest) block.getState());
		} else if (Material.BREWING_STAND.equals(material)) {
			stands.add((BrewingStand) block.getState());
		}
	}

	@Override
	protected void preScan(World world) {
		stands.clear();
		chests.clear();
	}

	@Override
	protected void postScan(World world, Entity entity) {
		if (stands.size() > 0) {
			// Transfer brewing result to chest.
			// TODO

			// Transfer net wart to available slot.
			// TODO

			// Transfer water bottle to available slot.
			// TODO
		}

		// Check nearby entities.
		long now = System.currentTimeMillis();
		long sinceLast = now - lastCheck;
		boolean canHeal = (healingPower > 0)
				&& ((now - lastHealing) > healingCooldown);
		boolean canBurn = (burningPower > 0)
				&& ((now - lastBurning) > burningCooldown);
		boolean hasHealed = false;
		boolean hasBurnt = false;

		if ((canHeal || canBurn) && (sinceLast > period)) {
			List<Entity> entities = entity.getNearbyEntities(radius, radius,
					radius);
			for (Entity i : entities) {
				if (i.isDead()) {
					continue; // Ignore dead stuff.
				}
				EntityType eType = i.getType();
				switch (eType) {
				case PLAYER:
				case VILLAGER:
				case IRON_GOLEM:
				case SNOWMAN:
				case SHEEP: {
					LivingEntity living = (LivingEntity) i;
					int current = living.getHealth();
					int diff = living.getMaxHealth() - current;
					if ((canHeal) && (diff > 0)) {
						hasHealed = true;
						living.setHealth(current + 1);
						if (EntityType.PLAYER.equals(eType)) {
							Player player = (Player) living;
							player.sendMessage(ChatColor.DARK_GRAY
									+ "A priest heals you ...");
						}
					}
					break;
				}
				case CREEPER:
				case SPIDER:
				case ZOMBIE:
				case CAVE_SPIDER:
				case SKELETON: {
					int current = i.getFireTicks();
					if ((current < 1) && (canBurn)) {
						i.setFireTicks(burnPower);
						hasBurnt = true;
					}
					break;
				}
				default:
					break;
				}
			}
			if (hasHealed) {
				--healingPower;
				lastHealing = now;
			}
			if (hasBurnt) {
				--burningPower;
				lastBurning = now;
			}
			lastCheck = now;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setConfiguration(Configuration cnf) {
		super.setConfiguration(cnf);
		Configuration.Priest prs = cnf.getPriestConfiguration();
		burningCooldown = prs.getBurningCooldown();
		healingCooldown = prs.getHealingCooldown();
		healPerDust = prs.getHealPerDust();
		burnPerRod = prs.getBurnPerRod();
		period = prs.getPeriod();
		lastHealing = lastBurning = lastCheck = System.currentTimeMillis();
		stands = new LinkedList<BrewingStand>();
		chests = new LinkedList<Chest>();
		radius = prs.getRadius();
		burnPower = prs.getBurnPower();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int extractVerticalAbove(Configuration cnf) {
		return cnf.getPriestConfiguration().getVerticalAbove();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int extractVerticalBelow(Configuration cnf) {
		return cnf.getPriestConfiguration().getVerticalBelow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int extractHorizontalScan(Configuration cnf) {
		return cnf.getPriestConfiguration().getHorizontalRange();
	}

}
