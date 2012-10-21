package org.randomgd.bukkit.workers.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Ring<T extends Executable> {

	protected static class Token<K extends Executable> implements Disposable {

		protected Token<K> previous;

		protected Token<K> next;

		protected K core;

		public Token(K val) {
			core = val;
		}

		public boolean perform() {
			return core.perform();
		}

		@Override
		public void dispose() {
			next = null;
			previous = null;
			core.dispose(); // ## Is it our job ?
			core = null;
		}
	}

	/**
	 * Token base.
	 */
	private Map<UUID, Token<T>> tokens = new HashMap<UUID, Token<T>>();

	/**
	 * Current token.
	 */
	private Token<T> current;

	public Ring() {
		current = null;
	}

	/**
	 * Perform one ring tick.
	 * 
	 * @param time
	 *            Maximum time of this tick in milliseconds.
	 */
	public void tick(long time, Collection<T> toAdd) {
		long start = System.nanoTime();
		long elapsed = 0;
		long now;

		add(toAdd);

		Token<T> last = current;
		while ((elapsed < time) && (current != null)) {
			boolean toRemove = !current.perform();
			if (toRemove) {
				tokens.remove(current);
				if (last == current) {
					last = current.next;
				}
				Token<T> previous = current.previous;
				Token<T> next = current.next;
				if (previous == next) { // Instance check.
					current.dispose();
					current = null;
				} else {
					previous.next = next;
					next.previous = previous;
					current.dispose();
					current = next;
				}
			} else {
				current = current.next;
				if (current == last) {
					break;
				}
			}

			now = System.nanoTime();
			elapsed += now - start;
			start = now;
		}
	}

	private void add(Collection<T> toAdd) {
		if ((toAdd != null) && (!toAdd.isEmpty())) {
			for (T i : toAdd) {
				UUID id = i.getUniqueId();
				Token<T> token = tokens.get(id);
				if (token == null) {
					token = new Token<T>(i);
					if (current == null) {
						current = token;
						current.next = current;
						current.previous = current;
					} else {
						Token<T> next = current.next;
						token.next = next;
						token.previous = current;
						current.next = token;
						next.previous = token;
					}
					tokens.put(id, token);
				} else {
					token.core = i;
				}
			}
		}
	}
}
