package org.randomgd.bukkit.workers.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;

/**
 * Help building a room by browsing blocks.
 */
public class RoomBuilder {

	public AxisAlignedBoundingBox builRoomFromFurnace(Block block) {
		AxisAlignedBoundingBox room = null;
		if (block == null) {
			return null;
		}
		Material material = block.getType();
		World world = block.getWorld();

		if (Material.FURNACE.equals(material)) {
			Furnace furnace = (Furnace) block.getState();
			byte data = block.getData();
			int xRel = 0;
			int zRel = 0;
			// http://www.minecraftwiki.net/wiki/Data_values#Ladders.2C_Wall_Signs.2C_Furnaces.2C_Dispensers_and_Chests

			switch (data) {
			case 2: // North
				zRel = -1;
				break;
			case 3: // South
				zRel = 1;
				break;
			case 4: // West
				xRel = -1;
				break;
			case 5: // East
				xRel = 1;
				break;
			default:
				break;
			}

			Location furnacePosition = furnace.getLocation();
			int x = furnacePosition.getBlockX();
			int y = furnacePosition.getBlockY();
			int z = furnacePosition.getBlockZ();

			LinkedList<SimplePosition> cursor = new LinkedList<SimplePosition>();
			Set<SimplePosition> visited = new HashSet<SimplePosition>();

			SimplePosition furnaceSimplePosition = new SimplePosition(x, y, z,
					0);
			SimplePosition workbenchSimplePosition = null;
			SimplePosition chestSimplePosition = null;
			visited.add(furnaceSimplePosition);
			SimplePosition current = new SimplePosition(x + xRel, y, z + zRel,
					1);
			room = new AxisAlignedBoundingBox(current);
			cursor.add(current);
			visited.add(current);

			while (!cursor.isEmpty()) {
				current = cursor.poll();
				room.add(current);
				int cX = current.getX();
				int cY = current.getY();
				int cZ = current.getZ();
				Block inspected = world.getBlockAt(cX, cY, cZ);
				Material iMaterial = inspected.getType();
				int l = current.getL();
				if (l > 5) {
					continue;
				}
				switch (iMaterial) {
				case AIR: {
					// Move on !
					propagate(cX + 1, cY, cZ, l, visited, cursor);
					propagate(cX - 1, cY, cZ, l, visited, cursor);
					propagate(cX, cY + 1, cZ, l, visited, cursor);
					propagate(cX, cY - 1, cZ, l, visited, cursor);
					propagate(cX, cY, cZ + 1, l, visited, cursor);
					propagate(cX, cY, cZ - 1, l, visited, cursor);
					break;
				}
				case CHEST: {
					if ((chestSimplePosition == null)
							|| (chestSimplePosition.getL() > current.getL())) {
						chestSimplePosition = current;
					}
					break;
				}
				case WORKBENCH: {
					if ((workbenchSimplePosition == null)
							|| (workbenchSimplePosition.getL() > current.getL())) {
						workbenchSimplePosition = current;
					}
					break;
				}
				default:
					// Do nothing. Not an interesting block.
					break;
				}
			}

			// At the end, let's build the room.
			if ((chestSimplePosition != null)
					&& (workbenchSimplePosition != null)) {
				System.out.println("We got a fully fonctional room !");
			} else {
				room = null;
			}
		}
		return room;
	}

	private void propagate(int x, int y, int z, int l,
			Set<SimplePosition> visited, List<SimplePosition> cursor) {
		SimplePosition toInsert = new SimplePosition(x, y, z, l + 1);
		if (!visited.contains(toInsert)) {
			cursor.add(toInsert);
			visited.add(toInsert);
		}
	}
}
