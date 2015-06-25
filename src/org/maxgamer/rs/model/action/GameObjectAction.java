package org.maxgamer.rs.model.action;

import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ScriptSpace;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class GameObjectAction extends Action {
	private GameObject target;
	private String option;
	
	public GameObjectAction(Mob mob, GameObject target, String option) {
		super(mob);
		this.target = target;
		this.option = option;
	}
	
	@Override
	public String toString() {
		return super.toString() + " obj=[" + target.toString() + "]";
	}
	
	@Override
	public Persona getOwner() {
		return (Persona) super.getOwner();
	}
	
	@Override
	public void run() throws SuspendExecution {
		getOwner().setFacing(Facing.face(target.getCenter()));
		HashMap<String, Object> map = new HashMap<>(2);
		map.put("target", target);
		map.put("option", option);
		
		ScriptSpace ss = Core.getScripts().get(getOwner(), this, map, "gameobject", target.getName(), option);
		if(ss == null){
			if(getOwner() instanceof Client){
				((Client) getOwner()).sendMessage(option + " not implemented.");
			}
			return;
		}
		
		ss.run();
		this.yield(); //This action was an alias for another, it should not cost a tick.
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}