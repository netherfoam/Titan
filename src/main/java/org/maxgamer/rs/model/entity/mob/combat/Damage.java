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
	private int hitDelay;
	
	public Damage(int hit, DamageType type, Mob target) {
		if (hit < 0) throw new IllegalArgumentException("Damage hit must be >= 0, given " + hit);
		if (type == null) throw new NullPointerException("DamageType may not be null.");
		
		this.hit = hit;
		this.type = type;
		this.target = target;
	}
	
	public Damage setType(DamageType type) {
		this.type = type;
		return this;
	}
	
	public Damage setHit(int hit) {
		this.hit = hit;
		return this;
	}
	
	public int getHit() {
		return hit;
	}
	
	public boolean isMax() {
		return max;
	}
	
	public Damage setMax(boolean max) {
		this.max = max;
		return this;
	}
	
	public DamageType getType() {
		return type;
	}
	
	public Mob getTarget() {
		return target;
	}

	public int getHitDelay() {
		return hitDelay;
	}

	public Damage setHitDelay(int hitDelay) {
		this.hitDelay = hitDelay;
		return this;
	}
}