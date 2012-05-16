package org.randomgd.bukkit.workers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.randomgd.bukkit.workers.info.FarmerInfo;
import org.randomgd.bukkit.workers.info.GolemInfo;
import org.randomgd.bukkit.workers.info.WorkerInfo;

/**
 * Just a bunch of test.
 */
public class WorkerHandler extends JavaPlugin implements Listener, Runnable {

	private Map<UUID, WorkerInfo> workerStack;

	/**
	 * Constructor.
	 */
	public WorkerHandler() {
		super();
		workerStack = new HashMap<UUID, WorkerInfo>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		FileConfiguration configuration = getConfig();

		// Get configuration.
		int period = configuration.getInt("period");

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, this, 10,
				period);
		// Populate the worker map.
		File directory = getDataFolder();
		if (!directory.exists()) {
			directory.mkdir();
		}
		if (directory.exists() && directory.isDirectory()) {
			// We can work now.
			String path = String.format("%s%cworkers.dat", directory.getPath(),
					File.separatorChar);
			File dataFile = new File(path);
			if (dataFile.exists() && dataFile.canRead() && dataFile.isFile()) {
				try {
					ObjectInputStream input = new ObjectInputStream(
							new FileInputStream(dataFile));
					Object result = input.readObject();
					// Type erasure, all that stuff ... not good ... noooot
					// good.
					input.close();
					workerStack = (Map<UUID, WorkerInfo>) result;
				} catch (Exception ex) {
					// Ouch ...
					System.out
							.println("Can't load informations about our fellow workers");
					ex.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onDisable() {
		// Populate the worker map.
		File directory = getDataFolder();
		if (!directory.exists()) {
			directory.mkdir();
		}
		if (directory.exists() && directory.isDirectory()) {
			// We can work now.
			String path = String.format("%s%cworkers.dat", directory.getPath(),
					File.separatorChar);
			File dataFile = new File(path);
			try {
				if (!dataFile.exists()) {
					dataFile.createNewFile();
				}
				ObjectOutputStream output = new ObjectOutputStream(
						new FileOutputStream(dataFile));
				output.writeObject(workerStack);
				output.flush();
				output.close();
			} catch (Exception ex) {
				// Ouch ...
				System.out
						.println("Can't write informations about our fellow workers");
				ex.printStackTrace();
			}
		}
	}

//	@EventHandler
//	public void onPlayerInteract(PlayerInteractEvent event) {
//		event.getPlayer().sendMessage(
//				ChatColor.BLUE + "Block " + event.getClickedBlock() + " ("
//						+ event.getClickedBlock().getData() + ")");
//	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		EntityType entityType = entity.getType();
		Player player = event.getPlayer();
		ItemStack stack = player.getItemInHand();
		Material material = stack.getType();

		if (entityType.equals(EntityType.VILLAGER)) {
			Villager villager = (Villager) entity;

			Villager.Profession profession = null;
			WorkerInfo info = null;
			switch (material) {
			case WHEAT:
				profession = Villager.Profession.FARMER;
				info = new FarmerInfo();
				break;
			case STICK: {
				WorkerInfo currentInfo = workerStack
						.get(villager.getUniqueId());
				if (currentInfo != null) {
					currentInfo.printInfoToPlayer(player);
				} else {
					player.sendMessage(ChatColor.GRAY
							+ "This villager is not a worker.");
				}
				break;
			}
			default: {
				WorkerInfo currentInfo = workerStack
						.get(villager.getUniqueId());
				if (currentInfo != null) {
					give(currentInfo, player, stack, material);
				}
				break;
			}
			}
			if (profession != null) {
				Villager.Profession oldProf = villager.getProfession();
				UUID id = villager.getUniqueId();
				boolean reset = (!workerStack.containsKey(id))
						|| (oldProf != profession);
				if (reset) {
					player.sendMessage(ChatColor.GRAY
							+ "Set this villager as a " + profession);
					workerStack.put(id, info);
					villager.setProfession(profession);
				}
			}
		} else if (entityType.equals(EntityType.IRON_GOLEM)) {
			UUID uuid = entity.getUniqueId();
			WorkerInfo currentInfo = workerStack.get(uuid);
			if (material.equals(Material.TORCH)
					|| (material.equals(Material.GLOWSTONE))) {
				if (currentInfo == null) {
					currentInfo = new GolemInfo();
					workerStack.put(uuid, currentInfo);
				}
				give(currentInfo, player, stack, material);
			} else if (material.equals(Material.STICK)) {
				if (currentInfo != null) {
					currentInfo.printInfoToPlayer(player);
				}
			}
		}
	}

	private void give(WorkerInfo info, Player player, ItemStack stack,
			Material material) {
		if (info.give(material, player)) {
			int sAmount = stack.getAmount();
			stack.setAmount(sAmount - 1);
			player.setItemInHand(stack);
		}
	}

	@Override
	public void run() {
		// Villagers are wandering ... oooh the great life.
		// They will fix blocks nearby, according to their profession.
		for (World world : getServer().getWorlds()) {
			browseEntities(Villager.class, world);
			browseEntities(IronGolem.class, world);
		}

	}

	private <T extends Entity> void browseEntities(Class<T> api, World world) {
		Collection<T> entities = world.getEntitiesByClass(api); // ## Really ?
																// Like
																// this ?
		Collection<T> browseable = new LinkedList<T>(); // Avoid concurrent
														// modification.
		browseable.addAll(entities);

		// ## No overcost compared to using the workerInfo keys ?
		for (T i : browseable) {
			// Look at the surrounding.
			UUID id = i.getUniqueId();
			WorkerInfo info = workerStack.get(id);
			if (info != null) {
				Location currentLocation = i.getLocation();
				int x = currentLocation.getBlockX();
				int y = currentLocation.getBlockY();
				int z = currentLocation.getBlockZ();
				// It's always nice to know where we are.
				info.perform(i, x, y, z, world);
			}
		}

		browseable.clear();
	}
}
