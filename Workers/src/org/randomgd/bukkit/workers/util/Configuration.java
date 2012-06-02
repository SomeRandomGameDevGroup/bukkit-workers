package org.randomgd.bukkit.workers.util;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Configuration.
 */
public final class Configuration {
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

}
