package org.maxgamer.rs.model.action;

import java.io.File;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.map.GameObject;
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
public class GameObjectAction extends Action {
	private GameObject obj;
	private Interpreter environment;
	private File file;
	
	public GameObjectAction(Persona mob, String option, GameObject obj) {
		super(mob);
		StopWatch w = Core.getTimings().start(getClass().getSimpleName());
		this.obj = obj;
		
		File[] files = new File[] { new File("scripts" + File.separator + "gameobject_action" + File.separator + obj.getName(), option + ".java"), new File("scripts" + File.separator + "gameobject_action", option + ".java") };
		
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
		return super.toString() + " obj=[" + obj.toString() + "]";
	}
	
	@Override
	public Persona getOwner() {
		return (Persona) super.getOwner();
	}
	
	@Override
	public void run() throws SuspendExecution {
		getOwner().setFacing(Facing.face(obj.getCenter()));
		
		boolean done = false;
		
		while(!done){
			if (environment == null) {
				if (getOwner() instanceof Client) {
					((Client) getOwner()).sendMessage("Option not implemented.");
				}
				return;
			}
			NameSpace ns = environment.getNameSpace();
			Object o;
			try {
				o = ns.invokeMethod("run", new Object[] { getOwner(), obj }, environment);
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
				Log.warning("Method run(Persona, GameObject) in ScriptAction in script " + file.getPath() + " should return true (done) or false (continue). Got " + o);
				return;
			}
		}
		
		/*
		if (first) {
			getOwner().setFacing(Facing.face(obj.getCenter()));
			first = false;
		}
		
		if (environment == null) {
			if (getOwner() instanceof Client) {
				((Client) getOwner()).sendMessage("Option not implemented.");
			}
			return true;
		}
		NameSpace ns = environment.getNameSpace();
		Object o;
		try {
			o = ns.invokeMethod("run", new Object[] { getOwner(), obj }, environment);
		}
		catch (EvalError e) {
			e.printStackTrace();
			return true; //Error, stop.
		}
		
		try {
			boolean b = (boolean) Primitive.unwrap(o);
			return b;
		}
		catch (RuntimeException e) {
			//ClassCastException or NullPointerException
			Log.warning("Method run(Persona, GameObject) in ScriptAction in script " + file.getPath() + " should return true (done) or false (continue). Got " + o);
			return true;
		}*/
	}
	
	@Override
	protected void onCancel() {
		
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
}