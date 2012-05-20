package org.randomgd.bukkit.workers.common;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.randomgd.bukkit.workers.info.WorkerInfo;

public class Worker implements Executable {

	/**
	 * Managed entity.
	 */
	private Entity entity;

	/**
	 * Worker information database.
	 */
	private Map<UUID, WorkerInfo> database;

	/**
	 * Entity identifier.
	 */
	private UUID id;

	public Worker(Entity ent, Map<UUID, WorkerInfo> db) {
		entity = ent;
		id = entity.getUniqueId();
		database = db;
	}

	@Override
	public boolean perform() {
		boolean result = true;
		if (entity.isDead()) {
			result = false;
		} else {
			WorkerInfo information = database.get(id);
			if (information != null) {
				Location currentLocation = entity.getLocation();
				int x = currentLocation.getBlockX();
				int y = currentLocation.getBlockY();
				int z = currentLocation.getBlockZ();
				World world = entity.getWorld();
				information.perform(entity, x, y, z, world);
			}
		}
		return result;
	}

	@Override
	public void dispose() {
		entity = null;
		database = null;
	}

	@Override
	public String toString() {
		Location currentLocation = entity.getLocation();
		int x = currentLocation.getBlockX();
		int y = currentLocation.getBlockY();
		int z = currentLocation.getBlockZ();
		String information = String.format("%s [%s] (%d, %d, %d)",
				id.toString(), entity.getType(), x, y, z);
		return information;
	}
}
