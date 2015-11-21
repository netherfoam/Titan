package org.maxgamer.rs.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;

public class MobUseItemEvent extends MobEvent implements Cancellable {
	private ItemStack item;
	private String option;
	private int slot;
	
	private boolean cancel;
	
	public MobUseItemEvent(Mob mob, ItemStack item, String option, int slot) {
		super(mob);
		
		this.item = item;
		this.option = option;
		this.slot = slot;
	}
	
	public String getOption(){
		return option;
	}
	
	public ItemStack getItem(){
		return this.item;
	}
	
	/**
	 * The slot. This may be -1 if unknown
	 */
	public int getSlot(){
		return this.slot;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
}
