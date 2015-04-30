package org.maxgamer.rs.model.entity.mob.combat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * A class representing the outcome of an attack. This class may handle multiple
 * damages to multiple targets of multiple different types of damages.
 * @author netherfoam
 */
public class AttackResult implements Iterable<Damage> {
	/** The damages dealt by the attack */
	private LinkedList<Damage> damages = new LinkedList<Damage>();
	
	/**
	 * Adds the given damage to the list of damages to apply
	 * @param damage The damage to apply
	 */
	public void add(Damage damage) {
		if (damage == null) return;
		damages.add(damage);
	}
	
	/**
	 * Applies all damages once, based on the given attacker.
	 * @param attacker The mob who is attacking.
	 */
	public void apply(Mob attacker) {
		for (Damage d : damages) {
			d.getTarget().getCombat().damage(attacker, d);
		}
	}
	
	/**
	 * Fetches a HashSet of all of the mobs which are targetted by this attack
	 * result. This does not include duplicates, due to the nature of a set.
	 * @return all of the mobs that will be hit by this attack
	 */
	public HashSet<Mob> getTargets() {
		HashSet<Mob> targets = new HashSet<>(damages.size());
		for (Damage d : damages) {
			targets.add(d.getTarget());
		}
		return targets;
	}
	
	/**
	 * Fetches the list of damages. Modifying this list will modify the outcome.
	 * @return The list of damages
	 */
	public LinkedList<Damage> getDamages() {
		return damages;
	}
	
	/**
	 * Gets the total damage dealt to the given mob.
	 * @param to The mob
	 * @return The total damage dealt
	 */
	public int getDamage(Mob to) {
		int n = 0;
		for (Damage d : damages) {
			if (d.getTarget() == to) {
				n += d.getHit();
			}
		}
		return n;
	}
	
	/**
	 * Gets the total damage done by this attack
	 * @return The total damage
	 */
	public int getTotalHit() {
		int n = 0;
		for (Damage d : damages) {
			n += d.getHit();
		}
		return n;
	}
	
	@Override
	public Iterator<Damage> iterator() {
		return damages.iterator();
	}
}