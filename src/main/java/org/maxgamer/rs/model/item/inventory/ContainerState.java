package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.util.NotImplementedException;

import java.util.ConcurrentModificationException;

/**
 * Represents a container at its current state.
 *
 * @author netherfoam
 */
public class ContainerState extends Container {
    /**
     * the container we're updating
     */
    private Container c;
    /**
     * The items this state represents. It is created with the contents of the
     * container, and modified as necessary
     */
    private ItemStack[] items;
    /**
     * Corresponds to the items array, true if an update has been made at the
     * given slot, false otherwise
     */
    private boolean[] updates;

    private int originalModCount;

    private boolean applied = false;

    /**
     * Creates a new container state for the given container.
     *
     * @param c the container
     */
    public ContainerState(Container c) {
        super(c.getStackType());
        this.c = c;
        this.items = c.getItems();
        this.updates = new boolean[items.length];
        this.originalModCount = c.modCount;
    }

    /**
     * The container that this state was created for.
     *
     * @return the internal container
     */
    public Container getContainer() {
        return c;
    }

    /**
     * Applies all changes made by this container state to the container, and
     * clears the internal set of updates.
     */
    public synchronized boolean apply() {
        if (this.applied) {
            throw new IllegalStateException("This ContainerState has already been applied.");
        }
        if (this.originalModCount != c.modCount) {
            throw new ConcurrentModificationException("The original container has been modified " + (c.modCount - this.originalModCount) + " times. Container state cannot be applied.");
        }

        boolean change = false;
        for (int i = 0; i < updates.length; i++) {
            if (updates[i]) {
                c.set(i, items[i]);
                updates[i] = false;
                change = true;
            }
        }
        // Don't allow re-application
        this.applied = true;

        return change;
    }

    @Override
    protected void setItem(int slot, ItemStack item) {
        items[slot] = item;
        updates[slot] = true;
    }

    @Override
    public ItemStack get(int slot) {
        return items[slot];
    }

    @Override
    public int getSize() {
        return c.getSize();
    }

    @Override
    public void shift() {
        throw new NotImplementedException("Can't shift a container state");
    }
}