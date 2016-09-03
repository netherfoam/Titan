package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;

public class MobEquipEvent extends MobEvent implements Cancellable {

    private final ItemStack unequipped;
    private final ItemStack equipped;
    private boolean cancel;

    public MobEquipEvent(Mob mob, ItemStack unequipped, ItemStack equipped) {
        super(mob);
        this.equipped = equipped;
        this.unequipped = unequipped;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public ItemStack getUnequipped() {
        return unequipped;
    }

    public ItemStack getEquipped() {
        return equipped;
    }

}
