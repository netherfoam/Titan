package org.maxgamer.rs.model.skill.prayer;

import org.maxgamer.rs.model.skill.SkillType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alva
 */
public enum PrayerType {
    THICK_SKIN(0, 1, 12, false, 1, -1), BURST_OF_STRENGTH(1, 4, 12, false, 2, -1), CLARITY_OF_THOUGHT(2, 7, 12, false, 4, -1), SHARP_EYE(3, 8, 12, false, 262144, -1), MYSTIC_WILL(4, 9, 12, false, 524288, -1), ROCK_SKIN(5, 10, 6, false, 8, -1), SUPERHUMAN_STRENGTH(6, 13, 6, false, 16, -1), IMPROVED_REFLEXES(7, 16, 6, false, 32, -1), RAPID_RESTORE(8, 19, 26,
            false, 64, -1), RAPID_HEAL(9, 22, 18, false, 128, -1), PROTECT_ITEM(10, 25, 18, false, 256, -1), HAWK_EYE(11, 26, 6, false, 1048576, -1), MYSTIC_LORE(12, 27, 6, false, 2097152, -1), STEEL_SKIN(13, 28, 3, false, 512, -1), ULTIMATE_STRENGTH(14, 31, 3, false, 1024, -1), INCREDIBLE_REFLEXES(15, 34, 3, false, 2048, -1), PROTECT_FROM_SUMMONING(16, 35,
            3, false, 16777216, 7), PROTECT_FROM_MAGIC(17, 37, 3, false, 4096, 2), PROTECT_FROM_MISSILES(18, 40, 3, false, 8192, 1), PROTECT_FROM_MELEE(19, 43, 3, false, 16384, 0), EAGLE_EYE(20, 44, 3, false, 4194304, -1), MYSTIC_MIGHT(21, 45, 3, false, 8388608, -1), RETRIBUTION(22, 46, 12, false, 32768, 3), REDEMPTION(23, 49, 6, false, 65536, 5), SMITE(24,
            52, 2, false, 131072, 4), CHIVALRY(25, 60, 1.5, false, 33554432, -1), RAPID_RENEWAL(26, 65, 1.8, false, 134217728, -1), PIETY(27, 70, 1.5, false, 67108864, -1), RIGOUR(28, 74, 2, false, 536870912, -1), AUGURY(29, 77, 2, false, 268435456, -1),
    /* Ancient prayers */
    CURSE_PROTECT_ITEM(0, 50, 18, true, 1, -1), SAP_WARRIOR(1, 50, 5, true, 2, -1), SAP_RANGER(2, 52, 5, true, 4, -1), SAP_MAGE(3, 54, 5, true, 8, -1), SAP_SPIRIT(4, 56, 5, true, 16, -1), BERSERKER(5, 59, 18, true, 32, -1), DEFLECT_SUMMONING(6, 62, 3, true, 64, 15), DEFLECT_MAGIC(7, 65, 3, true, 128, 13), DEFLECT_MISSILES(8, 68, 3, true, 256, 14), DEFLECT_MELEE(
            9, 71, 3, true, 512, 12), LEECH_ATTACK(10, 74, 3.6, true, 1024, -1), LEECH_RANGE(11, 76, 3.6, true, 2048, -1), LEECH_MAGIC(12, 78, 3.6, true, 4096, -1), LEECH_DEFENCE(13, 80, 3.6, true, 8192, -1), LEECH_STRENGTH(14, 82, 3.6, true, 16384, -1), LEECH_ENERGY(15, 84, 3.6, true, 32768, -1), LEECH_SPECIAL_ATTACK(16, 86, 3.6, true, 65536, -1), WRATH(
            17, 89, 12, true, 131072, 19), SOUL_SPLIT(18, 92, 1.5, true, 262144, 20), TURMOIL(19, 95, 1.5, true, 524288, -1);

    /**
     * HashMap for Normal Prayers
     */
    private static final Map<Integer, PrayerType> normalprayers;
    /**
     * HashMap for Ancient Prayers
     */
    private static final Map<Integer, PrayerType> ancientprayers;

    /**
     * HashMaps for each book containing slotID/prayerID + Key
     */
    static {
        normalprayers = new HashMap<Integer, PrayerType>();
        ancientprayers = new HashMap<Integer, PrayerType>();
        for (PrayerType v : EnumSet.range(PrayerType.THICK_SKIN, PrayerType.AUGURY)) {
            normalprayers.put(v.slotid, v);
        }
        for (PrayerType v : EnumSet.range(PrayerType.CURSE_PROTECT_ITEM, PrayerType.TURMOIL)) {
            ancientprayers.put(v.slotid, v);
        }
    }

    /**
     * SlotID/PrayerID
     */
    private int slotid;
    /**
     * Prayer level required
     */
    private int levelreq;
    /**
     * Drain ratio (double)
     */
    private double drainrate;
    /**
     * Whether normal prayer or ancient prayer/curse.
     */
    private boolean ancientprayer;
    /**
     * ConfigValue
     */
    private int configvalue;
    /**
     * PrayerHeadIcon (6 = protect magic + ranged) (8 = protect melee +
     * summoning) (9 = protect ranged + summoning) (10 = protect magic +
     * summoning) (11 = ?) (16 = deflect melee + summoning) (17 = deflect ranged
     * + summoning) (18 = deflect magic + summoning) (21 = red skull) (22 =
     * protect melee + ranged) (23 = protect melee + magic) (24 = protect all)
     */
    private int headicon;

    private PrayerType(int slotid, int levelreq, double drainrate, boolean ancientprayer, int configvalue, int headicon) {
        this.slotid = slotid;
        this.levelreq = levelreq;
        this.drainrate = drainrate;
        this.ancientprayer = ancientprayer;
        this.configvalue = configvalue;
        this.headicon = headicon;
    }

    /**
     * The ConfigValue according to the key element.
     *
     * @param key
     * @return Config Value of the key element
     */
    public static int getConfigValue(PrayerType key) {
        for (PrayerType slotv : EnumSet.allOf(PrayerType.class)) {
            if (key == slotv) {
                return slotv.getConfigValue();
            }
        }
        throw new IllegalArgumentException("This config value doesn't exist");
    }

    /**
     * Track the key by it's field (slotID/prayerID)
     *
     * @param slotid/prayerid clicked ingame
     * @param ancientprayer   or normal prayer
     * @return slot from hashmap
     */
    public static PrayerType findBySlotID(int slotid, boolean ancientprayer) {
        return ancientprayer ? ancientprayers.get(slotid) : normalprayers.get(slotid);
    }

    /**
     * The corresponding HeadIcon in the enum.
     *
     * @param slot clicked in-game
     * @return corresponding headicon of the enum.
     */
    public static int getHeadIcon(PrayerType key) {
        for (PrayerType slotv : EnumSet.allOf(PrayerType.class)) {
            if (key == slotv) {
                return slotv.getHeadIcon();
            }
        }
        throw new IllegalArgumentException("This headicon doesn't exist");
    }

    /**
     * The corresponding slotID in the enum.
     *
     * @param slotid  clicked in-game
     * @param ancient or normal prayers
     * @return corresponding slot of the enum.
     */
    public static PrayerType getSlot(int slotid, boolean ancientprayer) {
        for (PrayerType slot : EnumSet.allOf(PrayerType.class)) {
            if (slotid == slot.slotid && ancientprayer == slot.ancientprayer) {
                return slot;
            }
        }
        throw new IllegalArgumentException("This slot doesn't exist");
    }

    public double getStatBonus(SkillType skill) {
        switch (skill) {
            case ATTACK:
                switch (this) {
                    case CLARITY_OF_THOUGHT:
                        return 0.05;
                    case IMPROVED_REFLEXES:
                        return 0.10;
                    case INCREDIBLE_REFLEXES:
                        return 0.15;
                    case CHIVALRY:
                        return 0.15;
                    case PIETY:
                        return 0.20;
                    default:
                        return 0;
                }
            case STRENGTH:
                switch (this) {
                    case BURST_OF_STRENGTH:
                        return 0.05;
                    case SUPERHUMAN_STRENGTH:
                        return 0.10;
                    case ULTIMATE_STRENGTH:
                        return 0.15;
                    case CHIVALRY:
                        return 0.18;
                    case PIETY:
                        return 0.23;
                    default:
                        return 0;
                }
            case DEFENCE:
                switch (this) {
                    case THICK_SKIN:
                        return 0.05;
                    case ROCK_SKIN:
                        return 0.10;
                    case STEEL_SKIN:
                        return 0.15;
                    case CHIVALRY:
                        return 0.20;
                    case PIETY:
                        return 0.25;
                    case RIGOUR:
                        return 0.25;
                    case AUGURY:
                        return 0.25;
                    default:
                        return 0;
                }
            case RANGE:
                switch (this) {
                    case SHARP_EYE:
                        return 0.05;
                    case HAWK_EYE:
                        return 0.10;
                    case EAGLE_EYE:
                        return 0.15;
                    case RIGOUR:
                        return 0.20;
                    default:
                        return 0;
                }
            case MAGIC:
                switch (this) {
                    case MYSTIC_WILL:
                        return 0.05;
                    case MYSTIC_LORE:
                        return 0.10;
                    case MYSTIC_MIGHT:
                        return 0.15;
                    case AUGURY:
                        return 0.20;
                    default:
                        return 0;
                }
            default:
                return 0;
        }
    }

    /**
     * The SlotID/PrayerID
     *
     * @return The SlotID/PrayerID
     */
    public int getSlotId() {
        return slotid;
    }

    /**
     * The prayer level required
     *
     * @return the prayer level required
     */
    public int getLevelReq() {
        return levelreq;
    }

    /**
     * The drain ratio of the specific prayer
     *
     * @return the drain ratio of the prayer as a double
     */
    public double getDrainRate() {
        return 1 / drainrate;
    }

    /**
     * Checks whether the prayer is normal or ancient
     *
     * @return boolean normal or ancient prayer
     */
    public boolean isAncientPrayer() {
        return ancientprayer;
    }

    /**
     * The ConfigValue of the selected prayer
     *
     * @return config ID of the selected prayer
     */
    public int getConfigValue() {
        return configvalue;
    }

    /**
     * The PrayerHeadIcon of the selected prayer
     *
     * @return headicon ID of the selected prayer
     */
    public int getHeadIcon() {
        return headicon;
    }
}