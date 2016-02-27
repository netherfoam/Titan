package org.maxgamer.rs.model.javascript;

import org.maxgamer.rs.core.server.ServerTicker;
import org.maxgamer.rs.core.tick.FastTickable;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;

import co.paralleluniverse.fibers.SuspendExecution;

public class JSUtil {
	public static void wait(final JavaScriptFiber fiber, int ticks){
		final JavaScriptCall state = fiber.context().getCall();
		
		FastTickable t = new FastTickable() {
			@Override
			public void tick() {
				fiber.unpause(state, null);
			}
		};
		
		t.queue(ServerTicker.getTickDuration() * ticks);
		
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
			final JavaScriptCall state = fiber.context().getCall();
			walk = new WalkAction(mob, path){
				@Override
				public void run() throws SuspendExecution{
					super.run();
					fiber.unpause(state, null);
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
	
	public static void animate(final JavaScriptFiber fiber, Mob mob, int anim, int priority){
		Animation emo = new Animation(anim);
		mob.animate(emo, priority);
		
		final JavaScriptCall state = fiber.context().getCall();
		
		Tickable t = new Tickable() {
			@Override
			public void tick() {
				fiber.unpause(state, null);
			}
		};
		
		t.queue(emo.getDuration(true));
		
		fiber.pause();
	}
	
	private JSUtil(){
		// Private constructor
	}
}
