package org.maxgamer.rs.model.requirement;

import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.item.ItemStack;

public class InventoryRequirement implements Requirement<InventoryHolder> {
    private final ItemStack item;

    public InventoryRequirement(ItemStack item) {
        this.item = item;
    }

    @Override
    public boolean passes(InventoryHolder type) {
        return type.getInventory().contains(item);
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

}
