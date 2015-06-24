package org.maxgamer.rs.model.action;

import java.io.File;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.module.ScriptUtil;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.structure.timings.StopWatch;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class ItemAction extends Action {
	private ItemStack item;
	private Interpreter environment;
	private int slot;
	
	public ItemAction(Persona mob, String option, ItemStack item, int slot) {
		super(mob);
		this.item = item;
		this.slot = slot;
		
		StopWatch w = Core.getTimings().start(getClass().getSimpleName());
		File[] files = new File[] { new File("scripts" + File.separator + "inventory_action" + File.separator + item.getName(), option + ".java"), new File("scripts" + File.separator + "inventory_action", option + ".java") };
		
		for (File f : files) {
			if (f.exists()) {
				this.environment = ScriptUtil.getScript(f);
				try {
					this.environment.set("self", this);
				}
				catch (EvalError e) {
					e.printStackTrace();
				}
				break;
			}
		}
		w.stop();
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
		if (environment == null) {
			if (getOwner() instanceof Client) {
				((Client) getOwner()).sendMessage("Item option not implemented.");
			}
			return;
		}
		
		NameSpace ns = environment.getNameSpace();
		try {
			ns.invokeMethod("run", new Object[] { getOwner(), item, slot }, environment);
		}
		catch (EvalError e) {
			e.printStackTrace();
			return; //Error, stop.
		}
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}