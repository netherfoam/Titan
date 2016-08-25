package org.maxgamer.rs.model.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class JavaScriptContext extends Context{
	private long startTime = -1;
	private JavaScriptCall call;
	
	public JavaScriptContext(ContextFactory factory){
		super(factory);
	}
	
	public JavaScriptCall getCall(){
		if(call == null){
			throw new IllegalStateException("Call not yet set!");
		}
		return call;
	}
	
	public void setCall(JavaScriptCall call){
		this.call = call;
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