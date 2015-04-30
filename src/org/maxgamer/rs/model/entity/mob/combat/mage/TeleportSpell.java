package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.Location;

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
			private int startTick = -1;
			
			@Override
			protected boolean run() {
				if (startTick == -1) {
					startTick = Core.getServer().getTicker().getTicks();
					displayCast(source);
				}
				
				if (startTick + 2 == Core.getServer().getTicker().getTicks()) {
					source.teleport(target);
				}
				
				if (startTick + 4 <= Core.getServer().getTicker().getTicks()) {
					source.getUpdateMask().setAnimation(null, 25);
					return true; //Done
				}
				
				return false;
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