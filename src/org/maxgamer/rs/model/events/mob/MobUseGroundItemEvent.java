package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ground.GroundItemStack;

public class MobUseGroundItemEvent extends MobEvent implements Cancellable {
	private GroundItemStack item;
	private String option;
	
	private boolean cancel;
	
	public MobUseGroundItemEvent(Mob mob, GroundItemStack item, String option) {
		super(mob);
		
		if(mob == null) throw new NullPointerException("Mob can't be null");
		if(item == null) throw new NullPointerException("GroundItemStack can't be null");
		if(option == null) throw new NullPointerException("Option can't be null");
		if(item.isDestroyed()) throw new IllegalArgumentException("Item may not be destroyed");
		if(item.getLocation() == null) throw new NullPointerException("Item location may not be null");
		
		this.item = item;
		this.option = option;
	}
	
	public String getOption(){
		return option;
	}
	
	public GroundItemStack getItem(){
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
