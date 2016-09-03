package org.maxgamer.rs.model.item;

/**
 * @author netherfoam
 */
public enum WieldType {
    /**
     * A wield type that represents an item in the hat (head) slot.
     */
    HAT(0),

    /**
     * A wield type that represents an item in the cape (back) slot.
     */
    CAPE(1),

    /**
     * A wield type that represents an item in the shield slot.
     */
    SHIELD(5),

    /**
     * A wield type that represents an item in the gloves (hands) slot.
     */
    GLOVES(9),

    /**
     * A wield type that represents an item in the boots (feet) slot.
     */
    BOOTS(10),

    /**
     * A wield type that represents an item in the amulet (neck) slot.
     */
    AMULET(2),

    /**
     * A wield type that represents an item in the ring (finger) slot.
     */
    RING(12),

    /**
     * A wield type that represents an item in the arrow (projectile) slot.
     */
    ARROWS(13),

    /**
     * A wield type that represents an item in the body slot.
     */
    BODY(4),

    /**
     * A wield type that represents an item in the legs slot.
     */
    LEGS(7),

    /**
     * A wield type that represents an item in the weapon (hand) slot.
     */
    WEAPON(3);

    /**
     * The slot in which an item will be contained
     */
    private final int slot;

    WieldType(final int slot) {
        this.slot = slot;
    }

    public static WieldType forSlot(int id) {
        for (WieldType t : WieldType.values()) {
            if (t.slot == id) return t;
        }
        return null;
    }

    public int getSlot() {
        return this.slot;
    }

}