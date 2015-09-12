package org.maxgamer.rs.model.entity.mob.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.action.CombatFollow;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.map.path.PathFinder;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class Combat extends Action {
	/** A hashmap of source -> list of hits */
	private HashMap<Mob, ArrayList<Damage>> hits;
	
	/** The current target */
	private Mob target;
	
	/** The last mob we were attacked by */
	private Mob lastAttacker;
	
	/** The time (in ticks) that the last attacker attacked us */
	private long lastAttacked;
	
	/** The attack we're currently performing */
	private Attack attack;
	
	/**
	 * The tick number that the last attack was executed on.
	 */
	protected long lastAttack = 0;
	
	private CombatFollow follow;
	
	/**
	 * Constructs a new DamageCounter for the given mob
	 * @param owner the mob who is being damaged
	 */
	public Combat(Mob owner) {
		super(owner);
		this.hits = new HashMap<Mob, ArrayList<Damage>>();
	}
	
	/**
	 * Sets our next target to be the given target. This may fail if the mob is
	 * unreachable, in combat already, etc.
	 * @param target Who we would like to fight.
	 * @throws IllegalArgumentException if the target is the owner, or the
	 *         target is dead or hidden (By extension, destroyed too)
	 */
	public void setTarget(Mob target) {
		if (target == getOwner()) {
			throw new IllegalArgumentException("A mob may not set themself as a combat target.");
		}
		
		if (target != null) {
			if (target.isDead() || target.isHidden()) { //isHidden() is set when destroyed.
				throw new IllegalArgumentException("That target (" + target + ") is not valid! It is dead, destroyed or hidden!");
			}
			
			if (target.isAttackable(getOwner()) == false) {
				throw new IllegalArgumentException("That target (" + target + ") is not attackable by " + getOwner() + "!");
			}
			
			if (target.getLocation().z != getOwner().getLocation().z) {
				throw new IllegalArgumentException("Target is on Z axis " + target.getLocation().z + ", but " + getOwner() + " is on Z axis " + getOwner().getLocation().z);
			}
			
			if (target.getLocation().near(getOwner().getLocation(), 100) == false) {
				throw new IllegalArgumentException("That target (" + target + ") is too far away!");
			}
			
			if (isInCombat() == false) {
				this.hits = new HashMap<Mob, ArrayList<Damage>>();
			}
			
			if (target.getCombat().isInCombat() == false) {
				target.getCombat().hits = new HashMap<Mob, ArrayList<Damage>>();
			}
		}
		
		if (target != null) { //Starting attack
			//Begin processing this
			
			//if(attack == null){
			attack = getOwner().nextAttack();
			if (attack == null) {
				this.target = null; //We have no attack we can use
				return;
			}
			//}
			
			int range = attack.getMaxDistance();
			
			//TODO: This allows diagonal combat. 
			follow = new CombatFollow(getOwner(), target, range, range + 10, new AStar(10));
			if (follow.isFollowing() == false) {
				//Our follow is not valid, eg they're too far away or bad target
				follow = null;
				this.target = null; //Our new target is too far
				return;
			}
			
			//We will stop getting run requests until we're near the player and follow calls yield().
			getOwner().getActions().clear();
			follow.pair(this);
			getOwner().getActions().queue(this);
			getOwner().getActions().insertBefore(this, follow); //Attempt to reach the target first. This yield()'s
		}
		else if (target == null && this.target != null) { //Stopping attack
			//Stop processing this.
			getOwner().getActions().cancel(this);
			getOwner().getActions().cancel(follow); //Stop following as well.
			if (getOwner().getFacing() != null) {
				getOwner().setFacing(null);
			}
		}
		this.target = target;
	}
	
	/**
	 * Returns true if the owner currently has a target, is following the
	 * target, and the target is reachable with the current attack style.
	 * @return true if reachable, false if combat can't be done.
	 */
	public boolean isTargetReachable() {
		//if getTarget() != null && this.follow != null && this.follow.isFollowing() && this.follow.isReachable();
		if(getTarget() == null || this.follow == null || this.follow.isFollowing() == false){
			return false;
		}
		
		PathFinder finder = new AStar(8);
		Path path = finder.findPath(getOwner().getLocation(), getTarget().getLocation(), getTarget().getLocation(), getOwner().getSizeX(), getOwner().getSizeY());
		if(path.hasFailed()) return false;
		
		return true;
	}
	
	/**
	 * Gets the mob which is the desired target, if they still exist.
	 * @return The desired target.
	 */
	public Mob getTarget() {
		if (target != null) {
			//TODO: Maybe we should check we can reach the target still, with a primitive path finder?
			if (target.getLocation() == null || target.isDead() || target.isDestroyed() || target.getLocation().near(getOwner().getLocation(), 15) == false) {
				setTarget(null);
			}
		}
		return target;
	}
	
	/**
	 * Cancels the current attack, eg if it is no longer feasible to be used.
	 * For example, if a player switches from a Bow to a Sword, this should be
	 * called because the Sword cannot perform the same ranged attack that a Bow
	 * can (Despite the fact a Ranged attack could be queued)
	 * @return the discarded attack, possibly null if none queued.
	 */
	public Attack cancelAttack() {
		Attack a = this.attack;
		this.attack = null;
		return a;
	}
	
	/**
	 * Sets the mob which last attacked this one.
	 * @param m The mob which last attacked this one.
	 */
	public void setLastAttacker(Mob m) {
		this.lastAttacker = m;
		if (m != null) {
			this.lastAttacked = Core.getServer().getTicker().getTicks();
			if (getOwner().isRetaliate() && getTarget() == null && m.isAttackable(getOwner()) && m.isHidden() == false && m.isDead() == false && m.getLocation().z == getOwner().getLocation().z) {
				getOwner().getActions().clear(); //Cancel any actions previously given if possible
				setTarget(m);
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
	public void damage(Mob from, Damage d) {
		ArrayList<Damage> list = hits.get(from);
		if (list == null) {
			list = new ArrayList<Damage>();
			hits.put(from, list);
		}
		
		//Fix miss attacks
		if (d.getHit() <= 0) {
			boolean max = d.isMax();
			d = new Damage(d.getHit(), DamageType.MISS, d.getTarget());
			if (max) d.setMax(max);
		}
		
		list.add(d);
		getOwner().getUpdateMask().addHit(from, d);
		
		getOwner().setHealth(getOwner().getHealth() - d.getHit());
		
		int anim = getOwner().getCombatStats().getDefenceAnimation();
		if (anim > 0 && getOwner().getUpdateMask().getAnimation() == null) {
			getOwner().getUpdateMask().setAnimation(new Animation(anim), 5);
		}
		
		if (from != null) {
			this.setLastAttacker(from);
		}
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
		setTarget(null);
		setLastAttacker(null);
		this.attack = null;
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
		if (getTarget() != null) return true;
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
	
	@Override
	protected void run() throws SuspendExecution {
		
		while(true){
			//Dead mobs don't fight very often.
			if (this.getOwner().isDead() || this.getOwner().getLocation() == null || this.getOwner().isDestroyed()) {
				setTarget(null);
				return;
			}
			
			Mob target = getTarget();
			if (target == null) {
				return;
			}
			
			//TODO: Check if getOwner().isVisible(target) or something
			if (target.getLocation() == null || target.isDead() || target.isDestroyed()) {
				//The target is no longer valid.
				setTarget(null);
				return;
			}
			
			//TODO: Attackable check
			//TODO: Non-multicombat zone check
			
			if (this.attack == null) {
				this.attack = this.getOwner().nextAttack();
				
				if (this.attack == null) {
					setTarget(null);
					return; //We have no performable attack.
				}
			}
			
			//If combat check fails, return false (attempt again)
			int range = attack.getMaxDistance();
			if (target.getLocation().near(getOwner().getLocation(), range) == false && getOwner().getLocation().isDiagonal(target.getLocation()) == false) {
				//return false;
				wait(1);
				continue;
			}
			
			getOwner().setFacing(Facing.face(target));
			
			//Ensure we're adequately warmed up to do the attack
			if (Core.getServer().getTicker().getTicks() < lastAttack + attack.getWarmupTicks()) {
				//return false; //We need to spend longer waiting
				wait(1);
				continue;
			}
			
			if (this.attack.run(target)) {
				//The attack was completed.
				this.attack = null;
				this.lastAttack = Core.getServer().getTicker().getTicks();
			}
			
			if (target.isDead()) {
				setTarget(null);
				//return true; //We're done, our opponent is dead
				wait(1);
				continue;
			}
			else {
				//return false; //We're not done fighting
				wait(1);
				continue;
			}
		}
	}
	
	@Override
	protected void onCancel() {
		setTarget(null);
	}
	
	@Override
	protected boolean isCancellable() {
		return true;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "[target=" + getTarget() + (getTarget() == null ? "" : "reach=" + isTargetReachable()) + "]";
	}
}