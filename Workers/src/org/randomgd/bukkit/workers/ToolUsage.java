package org.randomgd.bukkit.workers;

/**
 * Enumerate tool usage number given their material (no source ... need to check
 * minecraftwiki.net).
 */
public enum ToolUsage {

	WOOD(50),

	STONE(130),

	IRON(370),

	GOLD(500),

	DIAMOND(2000);

	private int usage;

	private ToolUsage(int usg) {
		usage = usg;
	}

	public int getUsage() {
		return usage;
	}
}
