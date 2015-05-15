package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.Location;

import co.paralleluniverse.fibers.SuspendExecution;

/**
 * @author netherfoam
 */
public class TeleportSpell extends Spell {
	private Location target;
	
	public TeleportSpell(int level, int gfx, int anim, int castTime, Location target, ItemStack... runes) {
		super(level, gfx, anim, castTime, runes);
		this.target = target;
	}
	
	public void cast(final Mob source) {
		if (this.hasRequirements(source) == false || this.takeConsumables(source) == false) {
			return;
		}
		
		source.getActions().clear();
		source.getActions().queue(new Action(source) {
			@Override
			protected void run() throws SuspendExecution {
				displayCast(source);
				
				wait(2);
				source.teleport(target);
				wait(4);
				source.getUpdateMask().setAnimation(null, 25);
			}
			
			@Override
			protected void onCancel() {
				
			}
			
			@Override
			protected boolean isCancellable() {
				return false;
			}
		});
	}
}