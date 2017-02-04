package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public interface ContainerListener {
    /**
     * Called AFTER an item has been set on the inventory. Thus,
     * Container.get(slot) will return the new item in the slot.
     *
     * @param c    the container that changed
     * @param slot the slot that was modified inside the container
     * @param old  the previous item at the slot
     */
    void onSet(Container c, int slot, ItemStack old);
}