package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.AttackResult;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class AncientCombatSpell extends CombatSpell {
	private boolean multi;
	
	public AncientCombatSpell(int level, int gfx, int anim, int castTime, int targetGfx, int targetAnim, int projectileId, int range, int maxHit, int autocastId, boolean multi, ItemStack... runes) {
		super(level, gfx, anim, castTime, targetGfx, targetAnim, projectileId, range, maxHit, autocastId, runes);
		this.multi = multi;
	}
	
	@Override
	public boolean prepare(Mob src, Mob target, AttackResult damage) {
		if (multi) {
			for (Mob t : target.getLocation().getNearby(Mob.class, 1)) {
				if (t == src) continue; //Don't target yourself
				if (t.isAttackable(src) == false) continue; //Can't attack that target
				//TODO: Check if the neighbour target is attackable first.
				if (super.prepare(src, t, damage) == false) {
					return false;
				}
			}
			return true;
		}
		else {
			return super.prepare(src, target, damage);
		}
	}
	
	/**
	 * Returns true if this spell is a multi-target spell
	 * @return true if this spell is a multi-target spell
	 */
	public boolean isMulti() {
		return multi;
	}
}