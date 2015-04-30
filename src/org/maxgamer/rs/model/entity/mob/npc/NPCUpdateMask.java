package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.entity.mob.UpdateMask;

/**
 * @author netherfoam
 */
public class NPCUpdateMask extends UpdateMask {
	private boolean teleporting;
	
	public NPCUpdateMask(Mob owner, MovementUpdate movementMask) {
		super(owner, movementMask);
	}
	
	/**
	 * Returns whether this mob is teleporting. If a NPC wants to teleport, it
	 * is required that we remove the NPC from the player's list of local NPC's
	 * and re-add it later. This is only a getter.
	 * @return true if NPC is teleporting, false otherwise.
	 */
	public boolean isTeleporting() {
		return teleporting;
	}
	
	/**
	 * Sets whether this mob is teleporting. If a NPC wants to teleport, it is
	 * required that we remove the NPC from the player's list of local NPC's and
	 * re-add it later. This is only a setter and is non-functional.
	 * @param tele true if we want to teleport this mob, else false.
	 */
	public void setTeleporting(boolean tele) {
		this.teleporting = tele;
	}
	
	@Override
	public boolean hasChanged() {
		/**
		 * Worth documenting here, that because of how the update works, this is
		 * not necessary to check if teleporting has changed because removing
		 * the NPC is not part of the update block.
		 */
		return super.hasChanged();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.teleporting = false;
	}
}