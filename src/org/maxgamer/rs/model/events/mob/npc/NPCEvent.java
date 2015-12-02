package org.maxgamer.rs.model.events.mob.npc;

import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.npc.NPCDefinition;
import org.maxgamer.rs.model.events.mob.MobEvent;

/**
 * @author netherfoam
 */
public class NPCEvent extends MobEvent {
	public NPCEvent(NPC mob) {
		super(mob);
	}
	
	@Override
	public NPC getMob() {
		return (NPC) super.getMob();
	}
	
	public NPCDefinition getDefinition() {
		return getMob().getDefinition();
	}
}