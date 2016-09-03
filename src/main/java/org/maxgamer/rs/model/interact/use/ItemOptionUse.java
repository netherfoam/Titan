package org.maxgamer.rs.model.interact.use;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;

/**
 * @author netherfoam
 */
public class ItemOptionUse extends OptionUse {
    private Container container;
    private ItemStack item;
    private int slot;

    public ItemOptionUse(Container container, ItemStack item, int slot, String option) {
        super(option);
        this.container = container;
        this.item = item;
        this.slot = slot;
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

    @Override
    public String toString() {
        return item.toString() + "#" + getOption();
    }
}
