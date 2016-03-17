package org.maxgamer.rs.model.javascript.interaction;

import java.io.File;
import java.io.IOException;

import org.maxgamer.rs.event.EventHandler;
import org.maxgamer.rs.event.EventListener;
import org.maxgamer.rs.event.EventPriority;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.events.mob.MobItemOnItemEvent;
import org.maxgamer.rs.model.events.mob.MobUseGroundItemEvent;
import org.maxgamer.rs.model.events.mob.MobUseItemEvent;
import org.maxgamer.rs.model.events.mob.MobUseItemOnMobEvent;
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
	public void useGround(MobUseGroundItemEvent e) throws IOException{
		if(use(e.getMob(), e.getItem(), e.getOption())){
			e.consume();
		}
	}
	
	@EventHandler(consumer = true, priority = EventPriority.HIGH, skipIfCancelled = true)
	public void useNPC(MobUseItemEvent e) throws IOException{
		if(use(e.getMob(), e.getItem(), e.getOption())){
			e.consume();
		}
	}
	
	@EventHandler(consumer = true, priority = EventPriority.HIGH, skipIfCancelled = true)
	public void useItem(MobUseItemEvent e) throws IOException {
		if(use(e.getMob(), e.getItem(), e.getOption())){
			e.consume();
		}
	}
	
	@EventHandler(consumer = true, priority = EventPriority.HIGH, skipIfCancelled = true)
	public void useItemOnMob(MobUseItemOnMobEvent e) throws IOException {
		Log.info("useItemOnMob()");
		if(use(e.getMob(), e.getTarget(), "use", new Object[]{e.getMob(), e.getTarget(), e.getItem()})){
			e.consume(); 
		}
	}
	
	@EventHandler(consumer = true, priority = EventPriority.HIGH, skipIfCancelled = true)
	public void useItemOnItem(MobItemOnItemEvent e) throws IOException {
		if(use(e.getMob(), e.getUsingItem(), "use", new Object[]{e.getMob(), e.getUsingItem(), e.getUsingWithItem()})){
			e.consume();
		}
	}
	

	
	public boolean use(Mob user, Interactable target, String option) throws IOException{
		return use(user, target, option, new Object[]{user, target});
	}
	
	public boolean use(Mob user, Interactable target, String option, Object[] args) throws IOException{
		File f = this.manager.get(target, option);
		if(f == null) {
			return false;
		}
		
		// Create the action, queue it
		InteractionAction action = new InteractionAction(user, target, f, this.manager.toFunction(option), args);
		user.getActions().clear();
		user.getActions().queue(action);
		
		if(target instanceof Mob){
			// Turn the target around so they respond to the interaction
			((Mob) target).setFacing(Facing.face(user.getCenter()));
		}
		return true;
	}
}