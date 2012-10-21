package org.randomgd.bukkit.workers.util;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Configuration.
 */
public final class Configuration {

	public static class Priest {
		private int horizontalRange;

		private int verticalBelow;

		private int verticalAbove;

		private int healingCooldown;

		private int burningCooldown;

		private int healPerDust;

		private int burnPerRod;

		private int radius;

		private int period;

		private int burnPower;

		public Priest(FileConfiguration configuration) {
			horizontalRange = configuration.getInt("priest.horizontalrange");
			verticalBelow = configuration.getInt("priest.verticalbelow");
			verticalAbove = configuration.getInt("priest.verticalabove");
			healingCooldown = configuration.getInt("priest.healingcooldown");
			burningCooldown = configuration.getInt("priest.burningcooldown");
			healPerDust = configuration.getInt("priest.healperdust");
			burnPerRod = configuration.getInt("priest.burnperrod");
			radius = configuration.getInt("priest.radius");
			period = configuration.getInt("priest.check");
			burnPower = configuration.getInt("priest.burnpower");
		}

		public final int getHorizontalRange() {
			return horizontalRange;
		}

		public final int getVerticalBelow() {
			return verticalBelow;
		}

		public final int getVerticalAbove() {
			return verticalAbove;
		}

		public final int getHealingCooldown() {
			return healingCooldown;
		}

		public final int getBurningCooldown() {
			return burningCooldown;
		}

		public final int getHealPerDust() {
			return healPerDust;
		}

		public final int getBurnPerRod() {
			return burnPerRod;
		}

		public final int getRadius() {
			return radius;
		}

		public final int getPeriod() {
			return period;
		}

		public final int getBurnPower() {
			return burnPower;
		}
	}

	/**
	 * Specific blacksmith configuration.
	 */
	public static class Blacksmith {
		private int horizontalRange;

		private int verticalBelow;

		private int verticalAbove;

		private int miningCooldown;

		private int miningDepth;

		public Blacksmith(FileConfiguration configuration) {
			horizontalRange = configuration
					.getInt("blacksmith.horizontalrange");
			verticalBelow = configuration.getInt("blacksmith.verticalbelow");
			verticalAbove = configuration.getInt("blacksmith.verticalabove");
			miningCooldown = configuration.getInt("blacksmith.miningcooldown");
			miningDepth = configuration.getInt("blacksmith.miningdepth");
		}

		public final int getMiningDepth() {
			return miningDepth;
		}

		public final int getMiningCooldown() {
			return miningCooldown;
		}

		public final int getHorizontalRange() {
			return horizontalRange;
		}

		public final int getVerticalBelow() {
			return verticalBelow;
		}

		public final int getVerticalAbove() {
			return verticalAbove;
		}
	}

	// ## I hate this ! It generates an execution overhead, but for the sake of
	// flexibility, let's do it.

	private int horizontalRange;

	private int verticalBelow;

	private int verticalAbove;

	private int listUpdatePeriod;

	private int entityUpdatePeriod;

	private int timePerUpdate;

	private int librarianBookTime;

	private int butcherRadius;

	private int butcherShearPeriod;

	private Blacksmith blacksmith;

	private Priest priest;

	private int autosavePeriod;

	private boolean displayNotManaged;

	/**
	 * Constructor.
	 * 
	 * @param configuration
	 *            Configuration file.
	 */
	public Configuration(FileConfiguration configuration) {
		horizontalRange = configuration.getInt("scanning.horizontalrange");
		verticalBelow = configuration.getInt("scanning.verticalbelow");
		verticalAbove = configuration.getInt("scanning.verticalabove");
		listUpdatePeriod = configuration.getInt("timing.listupdate");
		entityUpdatePeriod = configuration.getInt("timing.entity.period");
		timePerUpdate = configuration.getInt("timing.entity.payload");
		librarianBookTime = configuration.getInt("librarian.bookproduction");
		butcherRadius = configuration.getInt("butcher.radius");
		butcherShearPeriod = configuration.getInt("butcher.shear");
		autosavePeriod = configuration.getInt("timing.autosave") * 1000;
		displayNotManaged = configuration.getBoolean("misc.displaynotmanaged");
		blacksmith = new Blacksmith(configuration);
		priest = new Priest(configuration);
	}

	public final int getAutosavePeriod() {
		return autosavePeriod;
	}

	public final Blacksmith getBlacksmithConfiguration() {
		return blacksmith;
	}

	public final Priest getPriestConfiguration() {
		return priest;
	}

	public final int getHorizontalRange() {
		return horizontalRange;
	}

	public final int getVerticalBelow() {
		return verticalBelow;
	}

	public final int getVerticalAbove() {
		return verticalAbove;
	}

	public final int getListUpdatePeriod() {
		return listUpdatePeriod;
	}

	public final int getEntityUpdatePeriod() {
		return entityUpdatePeriod;
	}

	public final int getTimePerUpdate() {
		return timePerUpdate;
	}

	public final int getLibrarianBookTime() {
		return librarianBookTime;
	}

	public final int getButcherRadius() {
		return butcherRadius;
	}

	public final int getButcherShearPeriod() {
		return butcherShearPeriod;
	}

	public final boolean isDisplayedNotManaged() {
		return displayNotManaged;
	}
}
