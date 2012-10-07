package org.randomgd.bukkit.workers;

import java.util.Map;

import org.bukkit.Material;

/**
 * Utility class for tool crafting need.
 */
public class ToolNeed {

	private int stick;
	private int wood;
	private int cobble;
	private int iron;
	private int gold;
	private int diamond;

	public ToolNeed(int s, int w, int c, int i, int g, int d) {
		stick = s;
		wood = w;
		cobble = c;
		iron = i;
		gold = g;
		diamond = d;
	}

	/**
	 * @param s
	 *            Stick in stock.
	 * @param w
	 *            Wood in stock.
	 * @param c
	 *            Cobble in stock.
	 * @param i
	 *            Iron in stock.
	 * @param g
	 *            Gold in stock.
	 * @param d
	 *            Diamond in stock.
	 * @return <code>true</code> if the tool can be crafted.
	 */
	public boolean grant(int s, int w, int c, int i, int g, int d) {
		return (s >= stick) && (w >= wood) && (c >= cobble) && (i >= iron)
				&& (g >= gold) && (d >= diamond);
	}

	public static void set(Map<Material, ToolNeed> map, Material m, int s,
			int w, int c, int i, int g, int d) {
		map.put(m, new ToolNeed(s, w, c, i, g, d));
	}

	public int getStick() {
		return stick;
	}

	public int getWood() {
		return wood;
	}

	public int getCobble() {
		return cobble;
	}

	public int getIron() {
		return iron;
	}

	public int getGold() {
		return gold;
	}

	public int getDiamond() {
		return diamond;
	}

}
