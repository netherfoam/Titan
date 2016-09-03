package org.maxgamer.rs.model.entity.mob.npc;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.MovementUpdate;
import org.maxgamer.rs.model.entity.mob.UpdateMask;

/**
 * @author netherfoam
 */
public class NPCUpdateMask extends UpdateMask {
    public NPCUpdateMask(Mob owner, MovementUpdate movementMask) {
        super(owner, movementMask);
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
    }
}