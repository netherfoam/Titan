package org.maxgamer.rs.script;

public class ScriptFilter {
	public static final int NO_ID = -1;
	
	private Class<?> type;
	private int id = NO_ID;
	private String option;
	private String name;
	
	public ScriptFilter(Class<?> type) {
		if(type == null){
			throw new IllegalArgumentException("Type may not be null");
		}
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getOption() {
		return option;
	}
	
	public void setOption(String option) {
		this.option = option;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
