package org.maxgamer.rs.model.entity.mob.npc.loot;

/**
 * Represents a Weighted chance object.
 *
 * @author netherfoam
 */
public interface Weighted {
    /**
     * The weight of this object. Usually between 1 and 100.
     */
    double getWeight();
}