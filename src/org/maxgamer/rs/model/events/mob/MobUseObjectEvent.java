package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.GameObject;

public class MobUseObjectEvent extends MobEvent implements Cancellable {
	private GameObject target;
	private String option;
	private int optionIndex;
	private boolean cancel;
	
	public MobUseObjectEvent(Mob mob, GameObject target, int option) {
		super(mob);
		
		this.target = target;
		this.option = GameObject.getDefinition(target.getId()).getOption(option);
		this.optionIndex = option;
	}
	
	public GameObject getTarget() {
		return this.target;
	}
	
	public String getOption() {
		return this.option;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public int getOptionIndex() {
		return optionIndex;
	}
	
	public void setOptionIndex(int optionIndex) {
		this.optionIndex = optionIndex;
	}
	
}
