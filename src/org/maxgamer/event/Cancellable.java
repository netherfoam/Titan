package org.maxgamer.event;

/**
 * Represents an action that may be cancelled once called.
 * @author netherfoam
 */
public interface Cancellable {
	/**
	 * Returns true if this action is cancelled.
	 * @return true if this action is cancelled.
	 */
	public boolean isCancelled();
	/**
	 * Sets whether this action is cancelled or not.
	 * @param cancel Whether to cancel it or not.
	 */
	public void setCancelled(boolean cancel);
}