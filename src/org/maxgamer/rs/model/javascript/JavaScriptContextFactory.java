package org.maxgamer.rs.model.javascript;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

public abstract class JavaScriptContextFactory extends ContextFactory{
	public JavaScriptContextFactory(){
		super();
	}
	
	
	public abstract long credits();
	public abstract void pay(long cost);
	
	@Override
	protected Context makeContext(){
		JavaScriptContext c = new JavaScriptContext(this);
		c.setOptimizationLevel(-1);
		c.setInstructionObserverThreshold(1000);
		return c;
	}
	
	@Override
	protected void observeInstructionCount(Context cx, int instructionCount){
		JavaScriptContext c = (JavaScriptContext) cx;
		
		long cost = c.getRuntime();
		if(c.getRuntime() > credits()){
			throw new TimeoutError("Script has surpassed maximum time.  Runtime: " + c.getRuntime() + "ms, bytecode instructions: " + instructionCount);
		}
		pay(cost);
	}
	
	@Override
	protected Object doTopCall(Callable callable, Context cx, Scriptable scope, Scriptable thisObj, Object[] args){
		JavaScriptContext c = (JavaScriptContext) cx;
		
		c.start();
		return super.doTopCall(callable, cx, scope, thisObj, args);
	}
}