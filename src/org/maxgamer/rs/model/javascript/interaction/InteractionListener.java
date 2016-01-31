package org.maxgamer.rs.model.javascript.interaction;

import java.io.File;
import java.io.IOException;

import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.mob.MobUseGroundItemEvent;
import org.maxgamer.rs.model.events.mob.MobUseNPCEvent;
import org.maxgamer.rs.model.events.mob.MobUseObjectEvent;

public class InteractionListener implements EventListener{
	private InteractionManager manager;
	
	public InteractionListener(InteractionManager manager){
		this.manager = manager;
	}
	
	@EventHandler(consumer = true, priority = EventPriority.HIGH, skipIfCancelled = true)
	public void useNPC(MobUseNPCEvent e) throws IOException{
		if(use(e.getMob(), e.getTarget(), e.getOption())){
			e.consume();
		}
	}
	
	@EventHandler(consumer = true, priority = EventPriority.HIGH, skipIfCancelled = true)
	public void useObject(MobUseObjectEvent e) throws IOException{
		if(use(e.getMob(), e.getTarget(), e.getOption())){
			e.consume();
		}
	}
	
	@EventHandler(consumer = true, priority = EventPriority.HIGH, skipIfCancelled = true)
	public void useNPC(MobUseGroundItemEvent e) throws IOException{
		if(use(e.getMob(), e.getItem(), e.getOption())){
			e.consume();
		}
	}
	
	public boolean use(Mob user, Entity target, String option) throws IOException{
		File f = this.manager.get(target);
		if(f == null) return false;
		
		// Create the action, queue it
		InteractionAction action = new InteractionAction(user, target, f, this.manager.toFunction(option));
		user.getActions().clear();
		user.getActions().queue(action);
		return true;
	}
}