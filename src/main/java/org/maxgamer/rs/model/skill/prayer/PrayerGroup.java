package org.maxgamer.rs.model.skill.prayer;

import java.util.LinkedList;

/**
 * @author netherfoam, alva
 */
public enum PrayerGroup {
    //Standard prayer book
    /**
     * All prayers that boost defense
     */
    DEFENSE(PrayerType.THICK_SKIN, PrayerType.ROCK_SKIN, PrayerType.STEEL_SKIN, PrayerType.CHIVALRY, PrayerType.PIETY, PrayerType.RIGOUR, PrayerType.AUGURY),
    /**
     * All prayers that boost strength
     */
    STRENGTH(PrayerType.BURST_OF_STRENGTH, PrayerType.SUPERHUMAN_STRENGTH, PrayerType.ULTIMATE_STRENGTH, PrayerType.CHIVALRY, PrayerType.PIETY),
    /**
     * All prayers that boost attack
     */
    ATTACK(PrayerType.CLARITY_OF_THOUGHT, PrayerType.IMPROVED_REFLEXES, PrayerType.INCREDIBLE_REFLEXES, PrayerType.CHIVALRY, PrayerType.PIETY),
    /**
     * All prayers that boost range
     */
    RANGE(PrayerType.SHARP_EYE, PrayerType.HAWK_EYE, PrayerType.EAGLE_EYE, PrayerType.RIGOUR),
    /**
     * All prayers that boost magic
     */
    MAGIC(PrayerType.MYSTIC_WILL, PrayerType.MYSTIC_LORE, PrayerType.MYSTIC_MIGHT, PrayerType.AUGURY),
    /**
     * most prayers that put a symbol above player head (Prot
     * (melee/magic/range), retribution, smite, redemption)
     */
    STANDARD_SPECIAL(PrayerType.PROTECT_FROM_MELEE, PrayerType.PROTECT_FROM_MISSILES, PrayerType.PROTECT_FROM_MAGIC, PrayerType.RETRIBUTION, PrayerType.REDEMPTION, PrayerType.SMITE),
    /**
     * Protect from melee/range/magic prayers
     */
    PROTECT_DAMAGE(PrayerType.PROTECT_FROM_MELEE, PrayerType.PROTECT_FROM_MISSILES, PrayerType.PROTECT_FROM_MAGIC),

    //Curses prayer book
    /**
     * Sap prayers (warrior, range, spirit)
     */
    SAP(PrayerType.SAP_WARRIOR, PrayerType.SAP_RANGER, PrayerType.SAP_SPIRIT),
    /**
     * leech prayers (attack, range, magic, defence, strength, energy, special
     * attack)
     */
    LEECH(PrayerType.LEECH_ATTACK, PrayerType.LEECH_RANGE, PrayerType.LEECH_MAGIC, PrayerType.LEECH_DEFENCE, PrayerType.LEECH_STRENGTH, PrayerType.LEECH_ENERGY, PrayerType.LEECH_SPECIAL_ATTACK),
    /**
     * similar to standard_special. Wrath, Soulsplit, deflect (magic, missiles,
     * melee)
     */
    CURSE_SPECIAL(PrayerType.WRATH, PrayerType.SOUL_SPLIT, PrayerType.DEFLECT_MAGIC, PrayerType.DEFLECT_MISSILES, PrayerType.DEFLECT_MELEE),
    /**
     * All deflections (magic, missiles, melee)
     */
    DEFLECT(PrayerType.DEFLECT_MAGIC, PrayerType.DEFLECT_MISSILES, PrayerType.DEFLECT_MELEE);

    private PrayerType[] types;

    PrayerGroup(PrayerType... types) {
        this.types = types;
    }

    /**
     * Returns an array of groups that the given prayer is in.
     *
     * @param type the prayer
     * @return an array of groups that the given prayer is in.
     */
    public static LinkedList<PrayerGroup> getGroups(PrayerType type) {
        LinkedList<PrayerGroup> groups = new LinkedList<>();
        for (PrayerGroup g : values()) {
            if (g.contains(type)) {
                groups.add(g);
            }
        }
        return groups;
    }

    public PrayerType[] getTypes() {
        return types;
    }

    /**
     * Returns true if this prayer group contains the given prayer.
     *
     * @param type the prayer
     * @return true if it is contained, else false.
     */
    public boolean contains(PrayerType type) {
        for (PrayerType p : this.types) {
            if (type == p) {
                return true;
            }
        }
        return false;
    }
}