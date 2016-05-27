package org.maxgamer.rs.interact.use;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;

/**
 * @author netherfoam
 */
public class ItemOptionUse implements Use{
    private Container container;
    private ItemStack item;
    private int slot;
    private String option;

    public ItemOptionUse(Container container, ItemStack item, int slot, String option) {
        this.container = container;
        this.item = item;
        this.slot = slot;
        this.option = option;
    }

    public String getOption() {
        return this.option;
    }

    public Container getContainer() {
        return container;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }
}
