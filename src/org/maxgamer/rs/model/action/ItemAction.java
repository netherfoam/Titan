package org.maxgamer.rs.model.action;

import java.io.File;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.module.ScriptUtil;
import org.maxgamer.rs.network.Client;
import org.maxgamer.rs.structure.timings.StopWatch;

import co.paralleluniverse.fibers.SuspendExecution;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;

/**
 * @author netherfoam
 */
public class ItemAction extends Action {
	private ItemStack item;
	private Interpreter environment;
	private File file;
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
				this.file = f;
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
		
		boolean done = false;
		NameSpace ns = environment.getNameSpace();
		Object o;
		
		while(!done){
			try {
				o = ns.invokeMethod("run", new Object[] { getOwner(), item, slot }, environment);
			}
			catch (EvalError e) {
				e.printStackTrace();
				return; //Error, stop.
			}
			
			try {
				done = (boolean) Primitive.unwrap(o);
				wait(1);
			}
			catch (RuntimeException e) {
				//ClassCastException or NullPointerException
				Log.warning("Method run(Persona, ItemStack) in ScriptAction in script " + file.getPath() + " should return true (done) or false (continue). Got " + o);
				return;
			}
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