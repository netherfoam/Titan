package org.maxgamer.rs.model.interfaces.impl.chat;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.item.ItemStack;

public abstract class ItemPickerDialogue extends PickerDialogue<ItemStack> {

    public ItemPickerDialogue(Player p, int maxAmount) {
        super(p, maxAmount);
    }

    public final void pick(ItemStack item, int amount) {
        this.pick(item.setAmount(amount));
    }

    public void add(ItemStack item) {
        this.add(item, item.getName(), item.getId());
    }

    public abstract void pick(ItemStack item);
}