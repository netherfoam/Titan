package org.maxgamer.rs.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.GameObject;

public class MobUseObjectEvent extends MobEvent implements Cancellable {
	private GameObject target;
	private int option;
	private boolean cancel;
	
	public MobUseObjectEvent(Mob mob, GameObject target, int option) {
		super(mob);
		
		this.target = target;
		this.option = option;
	}
	
	public GameObject getTarget() {
		return this.target;
	}
	
	public int getOptionNumber(){
		return this.option;
	}
	
	public String getOption(){
		return target.getDefiniton().getOption(this.option);
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
