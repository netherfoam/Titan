package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class GenericContainer extends Container {
    private ItemStack[] items;

    public GenericContainer(int size, StackType stack) {
        super(stack);
        this.items = new ItemStack[size];
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