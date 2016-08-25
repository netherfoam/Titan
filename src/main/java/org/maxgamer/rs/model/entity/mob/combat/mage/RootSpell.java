package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.AttackResult;
import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class RootSpell extends TargetSpell {
	private int duration;
	
	public RootSpell(int level, int gfx, int anim, int castTime, int targetGfx, int targetAnim, int projectileId, int range, int duration, ItemStack... runes) {
		super(level, gfx, anim, castTime, targetGfx, targetAnim, projectileId, range, runes);
		
		if (duration <= 0) {
			throw new IllegalArgumentException("RootSpell duration must be > 0");
		}
		
		this.duration = duration;
	}
	
	@Override
	public boolean prepare(Mob source, Mob target, AttackResult damages) {
		//No preparation
		return true;
	}
	
	@Override
	public void perform(Mob source, Mob target, AttackResult damages) {
		target.root(duration, 10, false);
	}
}