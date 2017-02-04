package org.maxgamer.rs.model.entity.mob.combat.mage;

import java.util.HashMap;

/**
 * @author netherfoam
 */
public class AncientBook extends Spellbook {
    public static final AncientBook ANCIENT = new AncientBook();

    private HashMap<Integer, Spell> spells = new HashMap<>();

    protected AncientBook() {
        super(193);
        spells.put(28, new CombatSpell(50, -1, 1978, 5, 385, -1, -1, 8, 140, 63, CHAOS_RUNE.setAmount(2), DEATH_RUNE.setAmount(2), FIRE_RUNE.setAmount(1), AIR_RUNE.setAmount(1))); //Smoke Rush, Exp: 30
        spells.put(32, new CombatSpell(52, -1, 1978, 5, 379, -1, -1, 8, 150, 65, CHAOS_RUNE.setAmount(2), DEATH_RUNE.setAmount(2), AIR_RUNE.setAmount(1), SOUL_RUNE.setAmount(1))); //Shadow Rush, Exp: 31
        spells.put(24, new CombatSpell(56, -1, 1978, 5, 373, -1, -1, 8, 160, 67, CHAOS_RUNE.setAmount(2), DEATH_RUNE.setAmount(2), BLOOD_RUNE.setAmount(1))); //Blood Rush, Exp: 33
        spells.put(20, new IceSpell(58, -1, 1978, 5, 361, -1, -1, 8, 170, 69, false, 17, CHAOS_RUNE.setAmount(2), DEATH_RUNE.setAmount(2), WATER_RUNE.setAmount(2))); //Ice Rush, Exp: 34
        spells.put(36, new CombatSpell(61, 1845, 10513, 5, 1847, -1, 0, 8, 180, 95, CHAOS_RUNE.setAmount(2), EARTH_RUNE.setAmount(1), SOUL_RUNE.setAmount(1))); //Miasmic Rush, Exp: 35
        spells.put(30, new CombatSpell(62, -1, 1979, 5, 389, -1, -1, 8, 180, 71, CHAOS_RUNE.setAmount(4), DEATH_RUNE.setAmount(2), FIRE_RUNE.setAmount(2), AIR_RUNE.setAmount(2))); //Smoke Burst, Exp: 36
        spells.put(34, new CombatSpell(64, -1, 1979, 5, 382, -1, -1, 8, 190, 73, CHAOS_RUNE.setAmount(4), DEATH_RUNE.setAmount(2), AIR_RUNE.setAmount(1), SOUL_RUNE.setAmount(2))); //Shadow Burst, Exp: 37
        spells.put(26, new CombatSpell(68, -1, 1979, 5, 376, -1, -1, 8, 210, 75, CHAOS_RUNE.setAmount(4), DEATH_RUNE.setAmount(2), BLOOD_RUNE.setAmount(2))); //Blood Burst, Exp: 39
        spells.put(22, new IceSpell(70, -1, 1979, 5, 363, -1, -1, 8, 220, 77, true, 17, CHAOS_RUNE.setAmount(4), DEATH_RUNE.setAmount(2), WATER_RUNE.setAmount(4))); //Ice Burst, Exp: 40
        spells.put(38, new CombatSpell(73, 1848, 10516, 5, 2228, -1, -1, 8, 230, 97, CHAOS_RUNE.setAmount(4), EARTH_RUNE.setAmount(2), SOUL_RUNE.setAmount(1))); //Miasmic Burst, Exp: 42
        spells.put(29, new CombatSpell(74, -1, 1978, 5, 387, -1, -1, 8, 230, 79, DEATH_RUNE.setAmount(2), BLOOD_RUNE.setAmount(2), FIRE_RUNE.setAmount(2), AIR_RUNE.setAmount(2))); //Smoke Blitz, Exp: 42
        spells.put(33, new CombatSpell(76, -1, 1978, 5, 381, -1, -1, 8, 240, 81, DEATH_RUNE.setAmount(2), BLOOD_RUNE.setAmount(2), AIR_RUNE.setAmount(2), SOUL_RUNE.setAmount(2))); //Shadow Blitz, Exp: 43
        spells.put(25, new CombatSpell(80, -1, 1978, 5, 375, -1, -1, 8, 250, 83, DEATH_RUNE.setAmount(2), BLOOD_RUNE.setAmount(4))); //Blood Blitz, Exp: 45
        spells.put(21, new IceSpell(82, -1, 1978, 5, 367, -1, -1, 8, 260, 85, false, 17, DEATH_RUNE.setAmount(2), BLOOD_RUNE.setAmount(2), WATER_RUNE.setAmount(3))); //Ice Blitz, Exp: 46
        spells.put(37, new CombatSpell(85, 1850, 10524, 5, 1851, -1, -1, 8, 270, 99, BLOOD_RUNE.setAmount(2), EARTH_RUNE.setAmount(2), SOUL_RUNE.setAmount(1))); //Miasmic Blitz, Exp: 47
        spells.put(31, new CombatSpell(86, -1, 1979, 5, 391, -1, -1, 8, 270, 87, DEATH_RUNE.setAmount(4), BLOOD_RUNE.setAmount(2), FIRE_RUNE.setAmount(4), AIR_RUNE.setAmount(4))); //Smoke Barrage, Exp: 48
        spells.put(35, new CombatSpell(88, -1, 1979, 5, 383, -1, -1, 8, 280, 89, DEATH_RUNE.setAmount(4), BLOOD_RUNE.setAmount(2), AIR_RUNE.setAmount(4), SOUL_RUNE.setAmount(3))); //Shadow Barrage, Exp: 49
        spells.put(27, new CombatSpell(92, -1, 1979, 5, 377, -1, -1, 8, 290, 91, DEATH_RUNE.setAmount(4), BLOOD_RUNE.setAmount(4), SOUL_RUNE.setAmount(1))); //Blood Barrage, Exp: 51
        spells.put(23, new IceSpell(94, -1, 1979, 5, 369, -1, -1, 8, 300, 93, true, 17, DEATH_RUNE.setAmount(4), BLOOD_RUNE.setAmount(2), WATER_RUNE.setAmount(6))); //Ice Barrage, Exp: 52
        spells.put(39, new CombatSpell(97, 1850, 10524, 5, 1851, -1, -1, 8, 330, 99, BLOOD_RUNE.setAmount(4), EARTH_RUNE.setAmount(4), SOUL_RUNE.setAmount(4))); //Miasmic Barrage, Exp: 53
    }

    @Override
    public Spell getSpell(int id) {
        return spells.get(id);
    }

    @Override
    public Spell getSpell(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Spell[] getSpells() {
        return spells.values().toArray(new Spell[spells.size()]);
    }
}