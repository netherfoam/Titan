package org.maxgamer.rs.model.action;

import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ground.GroundItemStack;
import org.maxgamer.rs.script.ScriptFilter;
import org.maxgamer.rs.script.ScriptSpace;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class GroundItemAction extends Action {
	private GroundItemStack item;
	private String option;
	
	public GroundItemAction(Persona mob, String option, GroundItemStack item) {
		super(mob);
		this.item = item;
		this.option = option;
	}
	
	@Override
	public String toString() {
		return super.toString() + " item=[" + item.toString() + "]";
	}
	
	@Override
	public Persona getOwner() {
		return (Persona) super.getOwner();
	}
	
	@Override
	public void run() throws SuspendExecution {
		HashMap<String, Object> map = new HashMap<String, Object>(2);
		map.put("item", item);
		map.put("option", option);
		
		ScriptFilter filter = new ScriptFilter(item.getClass());
		filter.setId(item.getItem().getId());
		filter.setName(item.getItem().getName());
		filter.setOption(option);
		
		//ScriptSpace ss = Core.getScripts().get(getOwner(), this, map, "ground", item.getItem().getName(), option);
		ScriptSpace ss = Core.getScripts().get(getOwner(), this, map, filter);
		if(ss == null){
			getOwner().sendMessage(option + " not implemented.");
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