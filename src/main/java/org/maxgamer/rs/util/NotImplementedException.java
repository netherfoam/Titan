package org.maxgamer.rs.util;

public class NotImplementedException extends RuntimeException{
	private static final long serialVersionUID = -4487601711355217179L;
	
	public NotImplementedException(){
		super("Not implemented");
	}
	
	public NotImplementedException(String msg){
		super(msg);
	}
}
