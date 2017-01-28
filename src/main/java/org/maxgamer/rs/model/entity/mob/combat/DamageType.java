package org.maxgamer.rs.model.entity.mob.combat;

/**
 * @author netherfoam
 */
public enum DamageType {
    MELEE(0), RANGE(1), MAGE(2), RED_DAMAGE(3), DEFLECT(4), SOAK(5), POISON(6), DISEASED(7), MISS(8), HEAL(9), CANNON(13);

    private byte netcode;

    DamageType(int code) {
        this.netcode = (byte) code;
    }

    public static byte getCode(DamageType base, boolean active, boolean max) {
        int v = base.toByte();
        if (!active) v += 14;
        if (max) v += 10;
        return (byte) v;
    }

    public byte toByte() {
        return netcode;
    }
}