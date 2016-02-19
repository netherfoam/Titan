package org.maxgamer.rs.module;

import org.maxgamer.rs.model.events.RSEvent;

public class ModuleEvent extends RSEvent{
	private Module module;
	
	public ModuleEvent(Module module){
		this.module = module;
	}
	
	public Module getModule(){
		return module;
	}
}