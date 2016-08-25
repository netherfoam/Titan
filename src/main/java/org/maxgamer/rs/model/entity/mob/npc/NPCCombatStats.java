package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.entity.mob.CombatStats;

/**
 * @author netherfoam
 */
public class NPCCombatStats extends CombatStats {
	public NPCCombatStats(NPC owner) {
		super(owner);
	}
	
	@Override
	public NPC getOwner() {
		return (NPC) super.getOwner();
	}
	
	@Override
	public int getAttackAnimation() {
		//return 428; //Dagger stab
		return getOwner().getDefinition().getAttackAnimation();
	}
	
	@Override
	public int getDefenceAnimation() {
		return getOwner().getDefinition().getDefenceAnimation();
	}
	
	@Override
	public int getDeathAnimation() {
		return getOwner().getDefinition().getDeathAnimation();
	}
	
}