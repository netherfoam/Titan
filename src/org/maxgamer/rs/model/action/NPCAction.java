package org.maxgamer.rs.model.action;

import java.io.File;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
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
public class NPCAction extends Action {
	private NPC npc;
	private Interpreter environment;
	public NPCAction(Persona mob, String option, NPC obj) {
		super(mob);
		StopWatch w = Core.getTimings().start(getClass().getSimpleName());
		this.npc = obj;
		
		File[] files = new File[] { new File("scripts" + File.separator + "npc_action" + File.separator + obj.getName(), option + ".java"), new File("scripts" + File.separator + "npc_action", option + ".java") };
		
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
		return super.toString() + " obj=[" + npc.toString() + "]";
	}
	
	@Override
	public Persona getOwner() {
		return (Persona) super.getOwner();
	}
	
	@Override
	public void run() throws SuspendExecution {
		if (environment == null) {
			if (getOwner() instanceof Client) {
				((Client) getOwner()).sendMessage("Option not implemented.");
			}
			return;
		}
		
		getOwner().setFacing(Facing.face(npc));
		
		NameSpace ns = environment.getNameSpace();
		
		try {
			ns.invokeMethod("run", new Object[] { getOwner(), npc }, environment);
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