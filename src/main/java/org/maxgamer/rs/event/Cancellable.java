package org.maxgamer.rs.event;

/**
 * Represents an action that may be cancelled once called.
 *
 * @author netherfoam
 */
public interface Cancellable {
    /**
     * Returns true if this action is cancelled.
     *
     * @return true if this action is cancelled.
     */
    boolean isCancelled();

    /**
     * Sets whether this action is cancelled or not.
     *
     * @param cancel Whether to cancel it or not.
     */
    void setCancelled(boolean cancel);
}