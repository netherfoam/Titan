package org.maxgamer.rs.script;

import java.util.Map;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;

import co.paralleluniverse.fibers.SuspendExecution;

public abstract class ActionHandler{
	private Action action;
	
	public void setAction(Action a){
		this.action = a;
	}
	
	public Action getAction(){
		if(action == null){
			throw new NullPointerException("Action has not yet been initialized, ActionHandler incorrectly setup. Call setAction() first");
		}
		return this.action;
	}
	
	protected void yield(){
		this.action.yield();
	}
	
	public abstract void run(Mob mob, Map<String, Object> args) throws SuspendExecution;
}