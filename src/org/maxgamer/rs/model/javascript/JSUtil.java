package org.maxgamer.rs.model.javascript;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;

import co.paralleluniverse.fibers.SuspendExecution;

public class JSUtil {
	public static void wait(final JavaScriptFiber fiber, int ticks){
		Tickable t = new Tickable() {
			@Override
			public void tick() {
				fiber.unpause(null);
			}
		};
		
		t.queue(ticks);
		
		fiber.pause();
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
			walk = new WalkAction(mob, path){
				@Override
				public void run() throws SuspendExecution{
					super.run();
					fiber.unpause(null);
				}
				
				@Override
				public boolean isCancellable(){
					//Our best attempt at making this a "force walk".
					return false;
				}
			};
			mob.getActions().queue(walk);
			fiber.pause();
		}
	}
	
	private JSUtil(){
		// Private constructor
	}
}
