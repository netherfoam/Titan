package org.maxgamer.rs.model.javascript.interaction;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.javascript.JavaScriptCall;
import org.maxgamer.rs.model.javascript.JavaScriptFiber;

import co.paralleluniverse.fibers.SuspendExecution;

public class InteractionAction extends Action{
	private String function;
	private Interactable target;
	private File file;
	private JavaScriptCall call;
	private Object[] args;
	
	@Override
	public String toString(){
		return "InteractionAction [function=" + function + ",target=" + target + ",file=" + file + ",call=" + call + ",args=" + Arrays.toString(args);
	}
	
	public InteractionAction(Mob mob, Interactable target, File jsFile, String function, Object[] args) {
		super(mob);
		this.target = target;
		this.function = function;
		this.file = jsFile;
		this.args = args;
	}
	
	public InteractionAction(Mob mob, Interactable target, File jsFile, String function){
		this(mob, target, jsFile, function, new Object[]{mob, target});
	}

	@Override
	protected void run() throws SuspendExecution {
		JavaScriptFiber fiber = new JavaScriptFiber();
		
		try{
			fiber.set("fiber", fiber);
			fiber.set("player", getOwner());
			
			if(fiber.parse("lib/core.js").isFinished() == false){
				throw new RuntimeException("lib/core.js cannot contain pauses outside of functions."); 
			}
			if(fiber.parse("lib/dialogue.js").isFinished() == false){
				throw new RuntimeException("lib/dialogue.js cannot contain pauses outside of functions."); 
			}
			if(fiber.parse(file).isFinished() == false){
				throw new RuntimeException(file + " cannot contain pauses outside of functions.");
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		if(target instanceof Entity){
			getOwner().face((Entity) target);
		}
		
		try {
			call = fiber.invoke(function, args);
		}
		catch (NoSuchMethodException e) {
			Log.debug("File " + file + " exists, but the function " + function + "() does not.");
			return;
		}
		
		Action.wait(1);
		while(call.isFinished() == false){
			Action.wait(1);
		}
	}

	@Override
	protected void onCancel() {
		if(call != null){
			call.terminate();
		}
	}

	@Override
	protected boolean isCancellable() {
		return true;
	}
}