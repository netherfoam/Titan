package org.maxgamer.rs.model.entity.mob.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.ServerTicker;
import org.maxgamer.rs.core.tick.FastTickable;
import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class DamageLog {
	/** A hashmap of source -> list of hits */
	private Mob owner;
	private HashMap<Mob, ArrayList<Damage>> hits;
	private long lastAttacked;
	private Mob lastAttacker;
	private long lastAttack;
	private Mob lastTarget;
	
	/**
	 * Constructs a new DamageCounter for the given mob
	 * @param owner the mob who is being damaged
	 */
	public DamageLog(Mob owner) {
		this.owner = owner;
		this.hits = new HashMap<Mob, ArrayList<Damage>>();
	}
	
	public Mob getOwner(){
		return this.owner;
	}
	
	public long getLastAttack(){
		return this.lastAttack;
	}
	
	public Mob getLastTarget(){
		return this.lastTarget;
	}
	
	public void setLastAttack(long l){
		this.lastAttack = l;
	}
	
	public void setLastTarget(Mob lastTarget){
		this.lastTarget = lastTarget;
	}
	
	/**
	 * Sets the mob which last attacked this one.
	 * @param m The mob which last attacked this one.
	 */
	public void setLastAttacker(Mob m) {
		this.lastAttacker = m;
		if (m != null) {
			this.lastAttacked = Core.getServer().getTicks();
			
			if (getOwner().isRetaliate() && getOwner().getActions().hasUncancellable() == false && getOwner().getTarget() == null && m.isAttackable(getOwner()) && m.isHidden() == false && m.isDead() == false && m.getLocation().z == getOwner().getLocation().z) {
				getOwner().getActions().clear(); //Cancel any actions previously given if possible
				getOwner().setTarget(m);
			}
		}
	}
	
	/**
	 * Fetches the mob that last attacked this one, if we are still in combat
	 * with them. Returns null otherwise.
	 * @return the mob that last attacked this one, if we are still in combat
	 *         with them. Returns null otherwise.
	 */
	public Mob getLastAttacker() {
		if (lastAttacker != null) {
			if (lastAttacker.isDead() || lastAttacker.isDestroyed() || lastAttacker.getLocation() == null || lastAttacked + 8 < Core.getServer().getTicker().getTicks()) {
				setLastAttacker(null);
			}
		}
		return lastAttacker;
	}
	
	/**
	 * Appends the given hit to this damage meter, which was dealt by the given
	 * mob. This notifies the owner's update mask. This subtracts the damage
	 * dealt from the target's health and sets the last known attacker if a
	 * non-null mob is given.
	 * @param from the mob who is damaging us, may be null for unknown
	 * @param d the damage
	 */
	public void damage(final Mob from, final Damage d) {
		ArrayList<Damage> list = hits.get(from);
		if (list == null) {
			list = new ArrayList<Damage>();
			hits.put(from, list);
		}
		
		list.add(d);
		new FastTickable(0) {
			@Override
			public void tick() {
				if (getOwner().getHealth() < d.getHit())
					d.setHit(getOwner().getHealth());
				//Fix miss attacks
				if (d.getHit() <= 0)
					d.setType(DamageType.MISS);
				getOwner().setHealth(getOwner().getHealth() - d.getHit());
				getOwner().getUpdateMask().addHit(from, d);
				
				int anim = getOwner().getCombatStats().getDefenceAnimation();
				if (anim > 0 && getOwner().getUpdateMask().getAnimation() == null)
					getOwner().animate(anim, 5);
				
				if (from != null)
					setLastAttacker(from);
				cancel();
			}
			
		}.queue(d.getHitDelay() * ServerTicker.TICK_DURATION);
	}
	
	/**
	 * Returns an unmodifiable list of hits that the given mob has dealt to the
	 * owner of this damage counter. The given mob may be null.
	 * @param from the mob dealing damage, may be null
	 * @return the unmodifiable list, may be null
	 */
	public List<Damage> getHits(Mob from) {
		ArrayList<Damage> list = hits.get(from);
		if (list == null) {
			return null;
		}
		
		return Collections.unmodifiableList(list);
	}
	
	/**
	 * Removes all existing hits from the counter, nulls the current target and
	 * last attacker, and nulls the attack that was about to be performed, if
	 * there was one.
	 */
	public void reset() {
		setLastAttacker(null);
		this.hits = new HashMap<Mob, ArrayList<Damage>>();
	}
	
	/**
	 * Returns the total damage that the given mob has dealt to the owner of
	 * this counter. Note that healing is added positively to this counter.
	 * @param from the mob who has been dealing damage, may be null
	 * @param types the types of combat we're counting
	 * @return the amount of damage, >= 0, that the mob has dealt.
	 */
	public int getTotal(Mob from, DamageType... types) {
		ArrayList<Damage> list = hits.get(from);
		if (list == null) return 0;
		
		int total = 0;
		for (Damage d : list) {
			for (DamageType t : types) {
				if (d.getType() == t) {
					//Hit type match
					total += d.getHit();
					break;
				}
			}
		}
		
		return total;
	}
	
	/**
	 * Returns the total damage that has been dealt to the owner of this meter.
	 * Note that healing is added positively to this counter.
	 * @param types the types of combat we're counting
	 * @return the amount of damage, >= 0, that the owner has been dealt.
	 */
	public int getTotal(DamageType... types) {
		int total = 0;
		for (Mob m : hits.keySet()) {
			total += getTotal(m, types);
		}
		return total;
	}
	
	/**
	 * Returns false if there is no target and the last attacker is no longer
	 * valid.
	 * @return true if in combat, false if not in combat
	 */
	public boolean isInCombat() {
		if (getOwner().getTarget() != null) return true;
		if (getLastAttacker() != null) return true;
		
		return false;
	}
	
	/**
	 * Returns a sorted array of mobs who have dealt the most damage to the
	 * owner of this counter. [0] will have dealt the highest (or equal to the
	 * highest) damage. If this mob has been dealt damage from an unspecified
	 * source, then the array will contain null values.
	 * @return a sorted array of highest to lowest killers.
	 */
	public Mob[] getKillers(DamageType... types) {
		Mob[] mobs = new Mob[hits.size()];
		int[] damages = new int[hits.size()];
		
		int i = 0;
		for (Mob m : hits.keySet()) {
			mobs[i] = m;
			damages[i] = getTotal(m, types);
			i++;
		}
		
		//Bubble sort is fine here, we will never be sorting huge lists
		//And we'll only call this once every time something dies.
		for (int j = 0; j < damages.length; j++) {
			for (int k = 0; k < damages.length; k++) {
				if (damages[j] > damages[k]) {
					Mob m = mobs[k];
					int d = damages[k];
					
					mobs[k] = mobs[j];
					damages[k] = damages[j];
					
					mobs[j] = m;
					damages[j] = d;
				}
			}
		}
		
		return mobs;
	}
}