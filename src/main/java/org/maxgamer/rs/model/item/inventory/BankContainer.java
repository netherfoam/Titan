package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * @author netherfoam
 */
public class BankContainer extends Container {
    /**
     * TODO: Duplication bug. Conditions:
     * * Noting = true
     * slot[length-3] = coins (200)
     * slot[length-2] = coins (200)
     * slot[length-1] = hatchet (80)
     *
     * 1. Withdraw all on coins
     * 2. Deposit all coins (hatchet will duplicate stacks)
     * 3. Rinse and repeat for infinite items
     */
    public static final int SIZE = 516;
    public static final int TABS = 11;

    private ItemStack[] items = new ItemStack[SIZE];

    public BankContainer() {
        super(StackType.ALWAYS);
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

    @Override
    public void deserialize(ConfigSection s) {
        // Since banks should be shifted to fill space, this is an easy sanity check
        super.deserialize(s);
        this.shift();
    }

    @Override
    public ContainerState getState() {
        return new ContainerState(this) {
            @Override
            public boolean apply() {
                if(super.apply()) {
                    // We override so that we make sure we shift after the transaction. We've got to be careful to do
                    // this after.
                    BankContainer.this.shift();
                    return true;
                }

                // No changes were applied
                return false;
            }
        };
    }
}