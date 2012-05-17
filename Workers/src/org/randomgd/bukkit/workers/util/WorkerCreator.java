package org.randomgd.bukkit.workers.util;

import java.lang.reflect.Constructor;

import org.bukkit.entity.Villager;
import org.randomgd.bukkit.workers.info.WorkerInfo;

/**
 * Utility class for creating worker information handler.
 * 
 * @param <T>
 *            Worker information class descriptor.
 */
public class WorkerCreator {

	/**
	 * Targeted villager profession.
	 */
	private Villager.Profession profession;

	/**
	 * Handler constructor.
	 */
	private Constructor<? extends WorkerInfo> constructor;

	/**
	 * Welcome message.
	 */
	private String welcomeMessage;

	public WorkerCreator(Villager.Profession prf,
			Class<? extends WorkerInfo> cls, String wlc) {
		profession = prf;
		welcomeMessage = wlc;
		try {
			constructor = cls.getConstructor();
		} catch (Exception ex) {
			// ... Seriously, it sounds like an internal bug.
			System.err
					.println("Totally abnormal error. Contact administrators or some random dev guy.");
			ex.printStackTrace();
		}
	}

	/**
	 * @return A worker information handler.
	 */
	public WorkerInfo create() {
		WorkerInfo result = null;
		try {
			result = constructor.newInstance();
		} catch (Exception ex) {
			// ... Really ?? Too bad ...
			System.err.println("The code must be lame ...");
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * @return The targeted profession.
	 */
	public Villager.Profession getProfession() {
		return profession;
	}

	/**
	 * @return The welcome message.
	 */
	public String getMessage() {
		return welcomeMessage;
	}
}
