package org.maxgamer.rs.model.action;

import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ScriptSpace;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class NPCAction extends Action {
	private NPC target;
	private String option;
	
	public NPCAction(Mob mob, String option, NPC npc) {
		super(mob);
		this.target = npc;
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
		HashMap<String, Object> map = new HashMap<>(2);
		map.put("target", target);
		map.put("option", option);
		
		ScriptSpace ss = Core.getScripts().get(getOwner(), map, "npc_action", target.getName(), option);
		if(ss == null){
			if(getOwner() instanceof Client){
				((Client) getOwner()).sendMessage(option + " not implemented.");
			}
			return;
		}
		
		ss.run();
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}