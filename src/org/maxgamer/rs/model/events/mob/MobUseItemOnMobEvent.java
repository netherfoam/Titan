package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;

public class MobUseItemOnMobEvent extends MobEvent implements Cancellable {
	private ItemStack item;
	private Mob target;
	private int slot;
	
	private boolean cancel;
	
	public MobUseItemOnMobEvent(Mob mob, ItemStack item, Mob target, int slot) {
		super(mob);
		
		this.item = item;
		this.target = target;
		this.slot = slot;
	}
	
	public Mob getTarget(){
		return target;
	}
	
	public ItemStack getItem(){
		return this.item;
	}
	
	/**
	 * The slot. This may be -1 if unknown. This is the slot in the Mob's inventory
	 * that was used on the NPC.
	 * 
	 * @return the slot of the item that was used.
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
