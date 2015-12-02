package org.maxgamer.rs.event;

public class Event {
	private boolean consumed = false;
	
	public final void consume(){
		if(consumed){
			throw new RuntimeException("Attempted to consume an event that has already been consumed!");
		}
		consumed = true;
	}
	
	public final boolean isConsumed(){
		return consumed;
	}
}