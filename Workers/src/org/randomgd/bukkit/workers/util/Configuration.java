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

	private int librarianRange;

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
		librarianRange = configuration.getInt("librarianrange");
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

	public final int getLibrarianRange() {
		return librarianRange;
	}

}
