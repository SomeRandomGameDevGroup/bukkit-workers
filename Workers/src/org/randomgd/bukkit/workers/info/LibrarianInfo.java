package org.randomgd.bukkit.workers.info;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.randomgd.bukkit.workers.util.ChestHandler;
import org.randomgd.bukkit.workers.util.Configuration;

/**
 * Information about a librarian activity and inventory.
 */
public class LibrarianInfo implements WorkerInfo {

	/**
	 * Unique Class Identifier.
	 */
	private static final long serialVersionUID = -3102616957479388299L;

	/**
	 * A librarian may study things.
	 */
	private static class Study implements Serializable {

		/**
		 * Unique Class Identifier.
		 */
		private static final long serialVersionUID = -4227577608048656898L;

		/**
		 * Study case.
		 */
		private Material material;

		/**
		 * Final reward.
		 */
		private int reward;

		/**
		 * Remaining study bits.
		 */
		private int remaining;

		/**
		 * Constructor.
		 * 
		 * @param mt
		 *            Material to study.
		 * @param pts
		 *            Study points to spend.
		 */
		public Study(Material mt, int pts) {
			reward = 0;
			remaining = pts;
			material = mt;
		}

		/**
		 * Is the study finished yet ?
		 * 
		 * @return true if the study is finished.
		 */
		public boolean isFinished() {
			return remaining == 0;
		}

		/**
		 * Provide the reward in experience points.
		 * 
		 * @return The reward if the study is finished. Else, 0.
		 */
		public int getReward() {
			return (remaining == 0) ? reward : 0;
		}

		/**
		 * Spend one study point.
		 */
		public void study() {
			if (remaining > 0) {
				--remaining;
				reward += (int) (Math.random() * 3);
			}
		}

		/**
		 * Provides the studied item type.
		 * 
		 * @return Material type.
		 */
		public Material getType() {
			return material;
		}
	}

	/**
	 * Current studies.
	 */
	private Map<UUID, Study> studies;

	/**
	 * Transported sugar cane.
	 */
	private int cane;

	/**
	 * Transported books.
	 */
	private int book;

	private transient int horizontalScan;

	private transient int verticalBelow;

	private transient int verticalAbove;

	private transient int librarianRange;

	/**
	 * Constructor.
	 */
	public LibrarianInfo() {
		cane = 0;
		book = 0;
		studies = new HashMap<UUID, Study>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printInfoToPlayer(Player player) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(ChatColor.GRAY);
		buffer.append("I'm a librarian.");
		if (cane > 0) {
			buffer.append(" I have some book to make");
			if (book > 0) {
				buffer.append(" and to desposit.");
			} else {
				buffer.append(".");
			}
		} else if (book > 0) {
			buffer.append(" I have some book to deposit.");
		}
		player.sendMessage(buffer.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean give(Material material, Player player) {
		boolean result = true;
		UUID pid = player.getUniqueId();
		Study study = studies.get(pid);
		switch (material) {
		case SUGAR_CANE:
			++cane;
			break;
		case STICK: {
			printInfoToPlayer(player);
			if (study != null) {
				player.sendMessage(ChatColor.GRAY + "By the way, I'm studying "
						+ study.getType() + " for you.");
			}
			result = false;
			break;
		}
		default:
			if (study != null) {
				if (!study.isFinished()) {
					StringBuffer buffer = new StringBuffer();
					buffer.append(ChatColor.GRAY);
					buffer.append("Be patient, I'm already studying this ");
					buffer.append(study.getType());
					buffer.append(" for you.");
					player.sendMessage(buffer.toString());
					result = false;
				}
			} else {
				result = initiateStudy(material, player);
			}
			break;
		}
		return result;
	}

	/**
	 * Launch a study.
	 * 
	 * @param subject
	 *            Subject of the study.
	 * @param player
	 *            Client.
	 * @return true if the study has been accepted.
	 */
	public boolean initiateStudy(Material subject, Player player) {
		boolean result = false;
		UUID id = player.getUniqueId();
		int potential = 0;
		StringBuffer buffer = new StringBuffer();
		buffer.append(ChatColor.GRAY);

		switch (subject) {
		case GLOWSTONE_DUST:
			buffer.append("Oh, glowing dust from the nether. Interesting even if it's rather common.");
			potential = 5;
			break;
		case NETHER_WARTS:
			buffer.append("Good grief ! One of those nether ... things. It smells ! But it's worth a quick study.");
			potential = 10;
			break;
		case BLAZE_ROD:
		case BLAZE_POWDER:
			buffer.append("Well, my dear, it's a nice hot piece of study you've brought me here !");
			potential = 30;
			break;
		case GHAST_TEAR:
			buffer.append("A tear from the fury of the deep ? Thank you my friend. Let see what I could learn ...");
			potential = 40;
			break;
		case DRAGON_EGG:
			buffer.append("*GASP* ... Erm, that is ... that's not the kind of thing you can study everyday ! Thank you !");
			potential = 1000;
			break;
		case ENDER_PEARL:
			buffer.append("I'm always amazed by those weird looking orbs.");
			potential = 50;
			break;
		case EYE_OF_ENDER:
			buffer.append("You've must had a rude journey my friend. Let's look at this !");
			potential = 90;
			break;
		case ENDER_STONE:
			buffer.append("Did you bring that from ... this 'place' ?? Thank you so much ! You won't regret this !");
			potential = 400;
			break;
		default:
			buffer.append("This ... thing ... has no interest for me. Move on !");
			break;
		}
		player.sendMessage(buffer.toString());
		if (potential > 0) {
			// TODO Put permission here.
			synchronized (studies) {
				Study study = new Study(subject, potential);
				studies.put(id, study);
				result = true;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void perform(Entity entity, int x, int y, int z, World world) {
		if ((y > 252) || (y < 5)) {
			return;
		}
		// Scanning.
		for (int xOffset = -horizontalScan; xOffset <= horizontalScan; ++xOffset) {
			int xA = x + xOffset;
			for (int zOffset = -horizontalScan; zOffset <= horizontalScan; ++zOffset) {
				int zA = z + zOffset;
				for (int yOffset = -verticalBelow; yOffset <= verticalAbove; ++yOffset) {
					int yA = y + yOffset;
					Block block = world.getBlockAt(xA, yA, zA);
					Material material = block.getType();
					switch (material) {
					case WORKBENCH: {
						int generated = cane / 3;
						book += generated;
						cane %= 3;
						break;
					}
					case CHEST: {
						// Time for chest fun !
						Chest chest = (Chest) block.getState();
						book = ChestHandler.deposit(Material.BOOK, book, chest);
						getCaneFromChest(chest);
					}
					case BOOKSHELF: {
						// ## For now, make it simple : every studies get
						// one point.
						synchronized (studies) {
							for (Study i : studies.values()) {
								i.study();
							}
						}
					}
					default:
						break;
					}
				}
			}
		}

		// Experience rewarding.
		List<Entity> entities = entity.getNearbyEntities(librarianRange,
				librarianRange, librarianRange);
		synchronized (entities) {
			for (Entity i : entities) {
				if (i instanceof Player) {
					UUID id = i.getUniqueId();
					Study study = studies.get(id);
					if ((study != null) && study.isFinished()) {
						int reward = study.getReward();
						Player player = (Player) i;
						StringBuffer buffer = new StringBuffer();
						buffer.append(ChatColor.GRAY);
						buffer.append("I've finished studying the ");
						buffer.append(study.getType());
						buffer.append(" you've given to me. Here is what I can learn to you : ");
						buffer.append(Integer.toString(reward));
						buffer.append(" XP.");
						player.sendMessage(buffer.toString());
						player.giveExp(reward);
						studies.remove(id);
					}
				}
			}
		}

	}

	private void getCaneFromChest(Chest chest) {
		if (cane < 3) {
			Inventory inventory = chest.getInventory();
			while (cane < 3) {
				int position = inventory.first(Material.SUGAR_CANE);
				if (position < 0) {
					break;
				}
				ItemStack stack = inventory.getItem(position);
				cane += stack.getAmount();
				stack.setAmount(0);
				stack.setType(Material.AIR);
				inventory.setItem(position, stack);
			}
		}
	}

	@Override
	public void setConfiguration(Configuration cnf) {
		// This code should be mutualized with all the "scanners" !
		horizontalScan = cnf.getHorizontalRange();
		verticalAbove = cnf.getVerticalAbove();
		verticalBelow = cnf.getVerticalBelow();
		// Librarian specific.
		librarianRange = cnf.getLibrarianRange();
	}
}
