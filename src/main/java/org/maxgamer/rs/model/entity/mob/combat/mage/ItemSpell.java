package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;

/**
 * @author netherfoam
 */
public abstract class ItemSpell extends Spell {
	
	public ItemSpell(int level, int gfx, int anim, int castTime, ItemStack... runes) {
		super(level, gfx, anim, castTime, runes);
	}
	
	public abstract void cast(Mob source, Container c, int slot);
}