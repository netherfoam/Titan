package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class Damage {
	private int hit;
	private DamageType type;
	private Mob target;
	private boolean max;
	
	public Damage(int hit, DamageType type, Mob target) {
		if (hit < 0) throw new IllegalArgumentException("Damage hit must be >= 0, given " + hit);
		if (type == null) throw new NullPointerException("DamageType may not be null.");
		
		this.hit = hit;
		this.type = type;
		this.target = target;
	}
	
	public int getHit() {
		return hit;
	}
	
	public boolean isMax() {
		return max;
	}
	
	public void setMax(boolean max) {
		this.max = max;
	}
	
	public DamageType getType() {
		return type;
	}
	
	public Mob getTarget() {
		return target;
	}
}