package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.item.ItemStack;

/**
 * Controls the persona's inventory and its contents. This class sends updates
 * to the client as necessary.
 *
 * @author netherfoam
 */
public class Inventory extends Container {
    public static final int SIZE = 28;
    private ItemStack[] items;

    public Inventory() {
        super(StackType.NORMAL);
        this.items = new ItemStack[SIZE];
    }

    @Override
    protected void setItem(int slot, ItemStack item) {
        items[slot] = item;
    }

    @Override
    public ItemStack get(int slot) {
        return items[slot];
    }

    @Override
    public int getSize() {
        return items.length;
    }
}
