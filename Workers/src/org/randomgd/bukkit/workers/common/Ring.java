package org.randomgd.bukkit.workers.common;

import java.util.Collection;

public class Ring<T extends Executable> {

	protected static class Token<K extends Executable> implements Disposable {

		private Token<K> previous;

		private Token<K> next;

		private K core;

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
		long start = System.currentTimeMillis();
		long elapsed = 0;
		long now;

		add(toAdd);

		Token<T> last = current;
		while ((elapsed < time) && (current != null)) {
			boolean toRemove = !current.perform();
			if (toRemove) {
				System.out.println("Remove " + current.core);
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

			now = System.currentTimeMillis();
			elapsed += now - start;
			start = now;
		}
	}

	private void add(Collection<T> toAdd) {
		if ((toAdd != null) && (!toAdd.isEmpty())) {
			System.out.println("Must add " + toAdd.size() + " tokens.");
			for (T i : toAdd) {
				System.out.println(i);
				Token<T> newToken = new Token<T>(i);
				if (current == null) {
					current = newToken;
					current.next = current;
					current.previous = current;
				} else {
					Token<T> next = current.next;
					newToken.next = next;
					newToken.previous = current;
					current.next = newToken;
					next.previous = newToken;
				}
			}
		}
	}
}
