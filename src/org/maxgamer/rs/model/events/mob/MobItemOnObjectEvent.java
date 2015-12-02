package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.GameObject;

public class MobItemOnObjectEvent extends MobEvent implements Cancellable {
	private GameObject target;
	private ItemStack item;
	private boolean cancel;
	
	public MobItemOnObjectEvent(Mob mob, GameObject target, ItemStack item) {
		super(mob);
		
		this.target = target;
		this.item = item;
	}
	
	public GameObject getObject() {
		return this.target;
	}
	
	public ItemStack getItem() {
		return this.item;
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
