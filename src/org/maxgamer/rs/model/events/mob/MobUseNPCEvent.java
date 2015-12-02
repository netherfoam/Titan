package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;

public class MobUseNPCEvent extends MobEvent implements Cancellable {
	private NPC target;
	private String option;
	private boolean cancel;
	
	public MobUseNPCEvent(Mob mob, NPC target, String option) {
		super(mob);
		
		this.target = target;
		this.option = option;
	}
	
	public NPC getTarget() {
		return this.target;
	}
	
	public String getOption(){
		return option;
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
