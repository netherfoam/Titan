package org.maxgamer.rs.model.interact.use;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;

/**
 * @author netherfoam
 */
public class ItemTargetUse implements Use {
    private Container container;
    private ItemStack item;
    private int slot;

    public ItemTargetUse(Container container, ItemStack item, int slot) {
        this.container = container;
        this.item = item;
        this.slot = slot;
    }

    public Container getContainer() {
        return container;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public String toString(){
        return item.toString();
    }
}
