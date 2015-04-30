package org.maxgamer.rs.model.entity.mob.npc;

import java.io.IOException;

import org.maxgamer.io.OutputStreamWrapper;
import org.maxgamer.rs.model.entity.mob.MobModel;

/**
 * @author netherfoam
 */
public class NPCModel extends MobModel {
	private NPCDefinition def;
	
	public NPCModel(NPCDefinition def) {
		if (def == null) throw new NullPointerException("NPC Definition may not be null");
		this.def = def;
		this.setCombatLevel(def.getCombatLevel());
	}
	
	@Override
	protected void appendUpdate(OutputStreamWrapper out) throws IOException {
		out.writeShort(-1); //Mob type or something?
		out.writeShort(def.getId());
		out.writeByte(0); //Unknown
	}
	
}