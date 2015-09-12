package org.maxgamer.rs.model.action;

import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.GameObject;
import org.maxgamer.rs.script.ScriptFilter;
import org.maxgamer.rs.script.ScriptSpace;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class ItemOnObjectAction extends Action {
	private ItemStack item;
	private GameObject object;
	
	public ItemOnObjectAction(Persona mob, GameObject object, ItemStack item) {
		super(mob);
		this.item = item;
		this.object = object;
	}
	
	@Override
	public String toString() {
		return super.toString() + " item=[" + item.toString() + "] object=[" + object + "]";
	}
	
	@Override
	public Persona getOwner() {
		return (Persona) super.getOwner();
	}
	
	@Override
	public void run() throws SuspendExecution {
		HashMap<String, Object> map = new HashMap<String, Object>(2);
		map.put("item", item);
		map.put("object", object);
		
		ScriptFilter filter = new ScriptFilter(item.getClass());
		filter.setId(item.getId());
		filter.setName(item.getName());
		filter.setOption("Use");
		
		ScriptSpace ss = Core.getScripts().get(getOwner(), this, map, filter);
		if(ss == null){
			getOwner().sendMessage(item + " on " + object + " not implemented.");
			return;
		}
		
		ss.run();
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return false;
	}
}