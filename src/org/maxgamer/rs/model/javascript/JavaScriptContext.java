package org.maxgamer.rs.model.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class JavaScriptContext extends Context{
	private long startTime = -1;
	
	public JavaScriptContext(ContextFactory factory){
		super(factory);
	}
	
	public long getStart(){
		if(startTime == -1){
			throw new IllegalStateException("Context hasn't been started yet!");
		}
		return startTime;
	}
	
	public long getRuntime(){
		return System.currentTimeMillis() - getStart();
	}
	
	public void start(){
		startTime = System.currentTimeMillis();
	}
}