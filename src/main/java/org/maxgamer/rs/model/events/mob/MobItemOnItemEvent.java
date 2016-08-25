package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;

public class MobItemOnItemEvent extends MobEvent implements Cancellable {

	private boolean cancel;
	private final ItemStack usingItem, usingWithItem;

	public MobItemOnItemEvent(Mob mob, ItemStack usingItem, ItemStack usingWithItem) {
		super(mob);
		this.usingItem = usingItem;
		this.usingWithItem = usingWithItem;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	public ItemStack getUsingItem() {
		return usingItem;
	}

	public ItemStack getUsingWithItem() {
		return usingWithItem;
	}

}
