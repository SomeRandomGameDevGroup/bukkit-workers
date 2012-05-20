package org.randomgd.bukkit.workers.common;

public interface Executable extends Disposable {

	/**
	 * Perform an action.
	 * 
	 * @return true if it succeeded.
	 */
	boolean perform();

}
