package org.maxgamer.rs.model.javascript;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;
import org.mozilla.javascript.ContinuationPending;

import co.paralleluniverse.fibers.SuspendExecution;

public class JSUtil {
	public static void wait(final JavaScriptFiber fiber, int ticks){
		final ContinuationPending state = fiber.state();
		
		Tickable t = new Tickable() {
			@Override
			public void tick() {
				try{
					fiber.unpause(state, null);
				}
				catch(ContinuationPending e){
					/* Quietly - Someone elses responsibility! */
				}
			}
		};
		
		t.queue(ticks);
		
		throw state;
	}
	
	public static void move(final JavaScriptFiber fiber, Mob mob, Location dest, boolean block){
		AStar finder = new AStar(10);
		Path path = finder.findPath(mob, dest, dest);
		
		WalkAction walk;
		if(block == false){
			walk = new WalkAction(mob, path);
			mob.getActions().queue(walk);
		}
		else{
			final ContinuationPending state = fiber.state();
			walk = new WalkAction(mob, path){
				@Override
				public void run() throws SuspendExecution{
					super.run();
					try{
						fiber.unpause(state, null);
					}
					catch(ContinuationPending e){
						/* Someone elses problem! */
					}
				}
				
				@Override
				public boolean isCancellable(){
					//Our best attempt at making this a "force walk".
					return false;
				}
			};
			mob.getActions().queue(walk);
			
			throw state;
		}
	}
	
	private JSUtil(){
		// Private constructor
	}
}
