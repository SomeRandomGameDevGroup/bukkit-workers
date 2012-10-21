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

	/**
	 * Permission identifier.
	 */
	private String permission;

	/**
	 * Class descriptor.
	 */
	private Class<? extends WorkerInfo> api;

	/**
	 * Construction.
	 * 
	 * @param prf
	 *            Profession.
	 * @param cls
	 *            Handler class descriptor.
	 * @param wlc
	 *            Welcome message.
	 * @param prm
	 *            Permission identifier.
	 */
	public WorkerCreator(Villager.Profession prf,
			Class<? extends WorkerInfo> cls, String wlc, String prm) {
		profession = prf;
		welcomeMessage = wlc;
		permission = prm;
		api = cls;
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
	public final Villager.Profession getProfession() {
		return profession;
	}

	/**
	 * @return The welcome message.
	 */
	public final String getMessage() {
		return welcomeMessage;
	}

	/**
	 * @return The permission identifier.
	 */
	public final String getPermission() {
		return permission;
	}

	/**
	 * @return The class descriptor.
	 */
	public final Class<? extends WorkerInfo> getAPI() {
		return api;
	}
}
