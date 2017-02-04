package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.item.ItemStack;

import java.util.ArrayList;

/**
 * @author netherfoam
 */
public class InfiniteContainer extends Container {
    private ArrayList<ItemStack> items = new ArrayList<>();

    public InfiniteContainer(StackType stack) {
        super(stack);
    }

    @Override
    protected void setItem(int slot, ItemStack item) {
        if (slot == items.size()) {
            // We're adding a new slot
            items.add(item);
        } else {
            items.set(slot, item);
        }
    }

    @Override
    public ItemStack get(int slot) {
        if (slot == items.size()) return null; // Last slot is always empty

        return items.get(slot);
    }

    @Override
    public int getSize() {
        // This means there's always room for one more
        return items.size() + 1;
    }
}