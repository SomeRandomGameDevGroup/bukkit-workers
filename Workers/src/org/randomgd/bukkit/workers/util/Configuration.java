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

	/**
	 * Constructor.
	 * 
	 * @param configuration
	 *            Configuration file.
	 */
	public Configuration(FileConfiguration configuration) {
		horizontalRange = configuration.getInt("horizontalrange");
		verticalBelow = configuration.getInt("verticalbelow");
		verticalAbove = configuration.getInt("verticalabove");
		listUpdatePeriod = configuration.getInt("listupdateperiod");
		entityUpdatePeriod = configuration.getInt("entityupdateperiod");
		timePerUpdate = configuration.getInt("timeperupdate");
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

}
