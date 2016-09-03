package org.maxgamer.rs.model.requirement;

import org.maxgamer.rs.model.entity.mob.EquipmentHolder;
import org.maxgamer.rs.model.item.ItemStack;

public class EquipmentRequirement implements Requirement<EquipmentHolder> {

    private final ItemStack item;

    public EquipmentRequirement(ItemStack item) {
        this.item = item;
    }

    @Override
    public boolean passes(EquipmentHolder type) {
        return type.getEquipment().contains(item);
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

}
