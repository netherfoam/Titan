package org.maxgamer.rs.model.entity.mob;

import java.util.HashMap;

/**
 * @author netherfoam
 */
public class Faction {
	private static enum FactionStatus {
		NEUTRAL, ENEMY, ALLY;
	}
	
	private String name;
	private HashMap<Faction, FactionStatus> relations = new HashMap<Faction, FactionStatus>();
	
	public Faction(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Invalid name given " + name);
		}
		this.name = name;
	}
	
	protected FactionStatus getStatus(Faction f) {
		FactionStatus status = relations.get(f);
		if (status == null) return FactionStatus.NEUTRAL;
		return status;
	}
	
	protected void setStatus(Faction f, FactionStatus status) {
		if (status == FactionStatus.NEUTRAL) status = null;
		relations.put(f, status);
	}
	
	public boolean isEnemy(Faction f) {
		return getStatus(f) == FactionStatus.ENEMY;
	}
	
	public boolean isAlly(Faction f) {
		return getStatus(f) == FactionStatus.ALLY;
	}
	
	public boolean isNeutral(Faction f) {
		return getStatus(f) == FactionStatus.NEUTRAL;
	}
	
	public String getName() {
		return name;
	}
	
	public void setEnemy(Faction f) {
		setStatus(f, FactionStatus.ENEMY);
	}
	
	public void setAlly(Faction f) {
		setStatus(f, FactionStatus.ALLY);
	}
	
	public void setNeutral(Faction f) {
		setStatus(f, FactionStatus.NEUTRAL);
	}
}