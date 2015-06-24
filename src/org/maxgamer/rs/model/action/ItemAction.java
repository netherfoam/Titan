package org.maxgamer.rs.model.action;

import java.util.HashMap;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.script.ScriptSpace;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class ItemAction extends Action {
	private ItemStack item;
	private int slot;
	private String option;
	
	public ItemAction(Persona mob, String option, ItemStack item, int slot) {
		super(mob);
		this.item = item;
		this.slot = slot;
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
		HashMap<String, Object> map = new HashMap<>(2);
		map.put("item", item);
		map.put("option", option);
		map.put("slot", slot);
		
		ScriptSpace ss = Core.getScripts().get(getOwner(), this, map, "inventory_action", item.getName(), option);
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