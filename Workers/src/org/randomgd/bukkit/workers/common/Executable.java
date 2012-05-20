package org.randomgd.bukkit.workers.common;

import java.util.UUID;

public interface Executable extends Disposable {

	/**
	 * Perform an action.
	 * 
	 * @return true if it succeeded.
	 */
	boolean perform();

	/**
	 * Provide a unique identifier.
	 * 
	 * @return Unique identifier.
	 */
	UUID getUniqueId();
}
