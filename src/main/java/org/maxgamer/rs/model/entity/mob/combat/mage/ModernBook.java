package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.skill.SkillType;

import java.util.HashMap;

/**
 * @author netherfoam
 */
public class ModernBook extends Spellbook {
    public static final ModernBook MODERN = new ModernBook();

    private HashMap<Integer, Spell> spells = new HashMap<>();

    protected ModernBook() {
        super(192);
        //Elemental spells
        spells.put(98, new CombatSpell(1, 457, 10546, 5, 463, -1, 458, 8, 10, -1, AIR_RUNE.setAmount(2))); //Wind Rush, Exp: 2.7
        spells.put(25, new CombatSpell(1, 457, 10546, 5, 463, -1, 458, 8, 20, 3, AIR_RUNE.setAmount(1), MIND_RUNE.setAmount(1))); //Wind Strike, Exp: 5.5
        spells.put(28, new CombatSpell(5, 2701, 10542, 5, 2708, -1, 2703, 8, 40, 5, AIR_RUNE.setAmount(1), MIND_RUNE.setAmount(1), WATER_RUNE.setAmount(1))); //Water Strike, Exp: 7.5
        spells.put(30, new CombatSpell(9, 2713, 14209, 5, 2723, -1, 2718, 8, 60, 7, AIR_RUNE.setAmount(1), MIND_RUNE.setAmount(1), EARTH_RUNE.setAmount(2))); //Earth Strike, Exp: 9.5
        spells.put(32, new CombatSpell(13, 2728, 2791, 5, 2737, -1, 2729, 8, 80, 9, AIR_RUNE.setAmount(2), MIND_RUNE.setAmount(1), FIRE_RUNE.setAmount(3))); //Fire Strike, Exp: 11.5
        spells.put(34, new CombatSpell(17, 457, 10546, 5, 464, -1, 459, 8, 90, 11, AIR_RUNE.setAmount(2), CHAOS_RUNE.setAmount(1))); //Wind Bolt, Exp: 13.5
        spells.put(39, new CombatSpell(23, 2701, 10542, 5, 2709, -1, 2704, 8, 100, 13, AIR_RUNE.setAmount(2), CHAOS_RUNE.setAmount(1), WATER_RUNE.setAmount(2))); //Water Bolt, Exp: 16.5
        spells.put(42, new CombatSpell(29, 2714, 14209, 5, 2724, -1, 2719, 8, 110, 15, AIR_RUNE.setAmount(2), CHAOS_RUNE.setAmount(1), EARTH_RUNE.setAmount(3))); //Earth Bolt, Exp: 19.5
        spells.put(45, new CombatSpell(35, 2728, 2791, 5, 2738, -1, 2730, 8, 120, 17, AIR_RUNE.setAmount(3), CHAOS_RUNE.setAmount(1), FIRE_RUNE.setAmount(4))); //Fire Bolt, Exp: 22.5
        spells.put(47, new CombatSpell(39, -1, -1, 5, -1, -1, 458, 8, 150, 35, EARTH_RUNE.setAmount(2), AIR_RUNE.setAmount(2), CHAOS_RUNE.setAmount(1))); //Crumble Undead, Exp: 16.5
        spells.put(49, new CombatSpell(41, 457, 10546, 5, 1863, -1, 460, 8, 130, 19, AIR_RUNE.setAmount(3), DEATH_RUNE.setAmount(1))); //Wind Blast, Exp: 25.5
        spells.put(52, new CombatSpell(47, 2701, 10542, 5, 2706, -1, 2705, 8, 140, 21, AIR_RUNE.setAmount(3), DEATH_RUNE.setAmount(1), WATER_RUNE.setAmount(3))); //Water Blast, Exp: 28.5
        spells.put(56, new CombatSpell(50, -1, -1, 5, -1, -1, 458, 8, 100, 37, DEATH_RUNE.setAmount(1), MIND_RUNE.setAmount(4))); //Magic Dart, Exp: 30
        spells.put(54, new CombatSpell(50, -1, -1, 5, -1, -1, 458, 8, 250, 45, FIRE_RUNE.setAmount(5), DEATH_RUNE.setAmount(1))); //Iban Blast, Exp: 42.5
        spells.put(58, new CombatSpell(53, 2715, 14209, 5, 2425, -1, 2720, 8, 150, 23, AIR_RUNE.setAmount(3), DEATH_RUNE.setAmount(1), EARTH_RUNE.setAmount(4))); //Earth Blast, Exp: 31.5
        spells.put(63, new CombatSpell(59, 2728, 2791, 5, 2739, -1, 2731, 8, 160, 25, AIR_RUNE.setAmount(4), DEATH_RUNE.setAmount(1), FIRE_RUNE.setAmount(5))); //Fire Blast, Exp: 34.5
        spells.put(66, new CombatSpell(60, -1, 811, 5, 76, -1, -1, 8, 200, 41, FIRE_RUNE.setAmount(2), BLOOD_RUNE.setAmount(2), AIR_RUNE.setAmount(4))); //Saradomin Strike, Exp: 35
        spells.put(67, new CombatSpell(60, -1, 811, 5, 77, -1, -1, 8, 200, 39, FIRE_RUNE.setAmount(1), BLOOD_RUNE.setAmount(2), AIR_RUNE.setAmount(4))); //Claws of Guthix, Exp: 35
        spells.put(68, new CombatSpell(60, -1, 811, 5, 78, -1, -1, 8, 200, 43, FIRE_RUNE.setAmount(4), BLOOD_RUNE.setAmount(2), AIR_RUNE.setAmount(1))); //Flames of Zamorak, Exp: 35
        spells.put(70, new CombatSpell(62, 457, 10546, 5, 2699, -1, 461, 8, 170, 27, AIR_RUNE.setAmount(5), BLOOD_RUNE.setAmount(1))); //Wind Wave, Exp: 36
        spells.put(73, new CombatSpell(65, 2701, 10542, 5, 2701, -1, 2706, 8, 180, 29, AIR_RUNE.setAmount(5), BLOOD_RUNE.setAmount(1), WATER_RUNE.setAmount(7))); //Water Wave, Exp: 37.5
        spells.put(77, new CombatSpell(70, 2716, 14209, 5, 2726, -1, 2721, 8, 190, 31, AIR_RUNE.setAmount(5), BLOOD_RUNE.setAmount(1), EARTH_RUNE.setAmount(7))); //Earth Wave, Exp: 40
        spells.put(80, new CombatSpell(75, 2728, 2791, 5, 2740, -1, 2733, 8, 200, 33, AIR_RUNE.setAmount(5), BLOOD_RUNE.setAmount(1), FIRE_RUNE.setAmount(7))); //Fire Wave, Exp: 42.5
        spells.put(84, new CombatSpell(81, 457, 10546, 5, 2700, -1, 462, 8, 220, 47, AIR_RUNE.setAmount(7), BLOOD_RUNE.setAmount(1), DEATH_RUNE.setAmount(1))); //Wind Surge, Exp: 75
        spells.put(87, new CombatSpell(85, 2701, 10542, 5, 2712, -1, 2707, 8, 240, 49, AIR_RUNE.setAmount(7), WATER_RUNE.setAmount(10), BLOOD_RUNE.setAmount(1), DEATH_RUNE.setAmount(1))); //Water Surge, Exp: 80
        spells.put(89, new CombatSpell(90, 2717, 14209, 5, 2727, -1, 2722, 8, 260, 51, AIR_RUNE.setAmount(7), EARTH_RUNE.setAmount(10), BLOOD_RUNE.setAmount(1), DEATH_RUNE.setAmount(1))); //Earth Surge, Exp: 85
        spells.put(91, new CombatSpell(95, 2728, 2791, 5, 2741, -1, 2735, 8, 280, 53, AIR_RUNE.setAmount(7), FIRE_RUNE.setAmount(10), BLOOD_RUNE.setAmount(1), DEATH_RUNE.setAmount(1))); //Fire Surge, Exp: 95

        //5% debuff spells
        spells.put(26, new BuffSpell(3, 177, 710, 5, -1, -1, -1, 8, 0.95, SkillType.ATTACK, WATER_RUNE.setAmount(3), EARTH_RUNE.setAmount(2), BODY_RUNE.setAmount(1))); //Confuse, Exp: 13
        spells.put(31, new BuffSpell(11, 177, 710, 5, -1, -1, -1, 8, 0.95, SkillType.STRENGTH, WATER_RUNE.setAmount(3), EARTH_RUNE.setAmount(2), BODY_RUNE.setAmount(1))); //Weaken, Exp: 20.5
        spells.put(35, new BuffSpell(19, 177, 710, 5, -1, -1, -1, 8, 0.95, SkillType.DEFENCE, WATER_RUNE.setAmount(2), EARTH_RUNE.setAmount(3), BODY_RUNE.setAmount(1))); //Curse, Exp: 29

        //10% debuff spells
        spells.put(75, new BuffSpell(66, 177, 710, 5, -1, -1, -1, 8, 0.90, SkillType.DEFENCE, EARTH_RUNE.setAmount(5), WATER_RUNE.setAmount(5), SOUL_RUNE.setAmount(1))); //Vulnerability, Exp: 76
        spells.put(78, new BuffSpell(73, 177, 710, 5, -1, -1, -1, 8, 0.90, SkillType.STRENGTH, EARTH_RUNE.setAmount(8), WATER_RUNE.setAmount(8), SOUL_RUNE.setAmount(1))); //Enfeeble, Exp: 83
        spells.put(82, new BuffSpell(80, 177, 710, 5, -1, -1, -1, 8, 0.90, SkillType.ATTACK, EARTH_RUNE.setAmount(12), WATER_RUNE.setAmount(12), SOUL_RUNE.setAmount(1))); //Stun, Exp: 83

        //Rooting spells
        spells.put(36, new RootSpell(20, 177, 710, 5, 181, -1, -1, 8, 8, EARTH_RUNE.setAmount(3), WATER_RUNE.setAmount(3), NATURE_RUNE.setAmount(2))); //Bind, Exp: 30
        spells.put(55, new RootSpell(20, 177, 710, 5, 180, -1, -1, 8, 17, EARTH_RUNE.setAmount(4), WATER_RUNE.setAmount(4), NATURE_RUNE.setAmount(3))); //Snare, Exp: 60.5
        spells.put(81, new RootSpell(20, 177, 710, 5, 179, -1, -1, 8, 25, EARTH_RUNE.setAmount(5), WATER_RUNE.setAmount(5), NATURE_RUNE.setAmount(4))); //Bind, Exp: 91


        //Teleport spells
        spells.put(24, new TeleportSpell(1, 1576, 8939, 5, new Location(3221, 3218, 0))); //Home teleport, Exp: 0
        spells.put(37, new TeleportSpell(10, 1576, 8939, 5, new Location(2411, 2832, 0), LAW_RUNE.setAmount(1), WATER_RUNE.setAmount(1), AIR_RUNE.setAmount(1))); //Mobilising Armies teleport, Exp: 19
        spells.put(40, new TeleportSpell(25, 1576, 8939, 5, new Location(3211, 3422, 0), LAW_RUNE.setAmount(1), FIRE_RUNE.setAmount(1), AIR_RUNE.setAmount(3))); //Varrock teleport, Exp: 35
        spells.put(43, new TeleportSpell(31, 1576, 8939, 5, new Location(3221, 3219, 0), LAW_RUNE.setAmount(1), EARTH_RUNE.setAmount(1), AIR_RUNE.setAmount(3))); //Lumbridge teleport, Exp: 41
        spells.put(46, new TeleportSpell(37, 1576, 8939, 5, new Location(2966, 3379, 0), LAW_RUNE.setAmount(1), WATER_RUNE.setAmount(1), AIR_RUNE.setAmount(3))); //Falador teleport, Exp: 48
        spells.put(51, new TeleportSpell(45, 1576, 8939, 5, new Location(2757, 3477, 0), LAW_RUNE.setAmount(1), AIR_RUNE.setAmount(5))); //Camelot teleport, Exp: 55.5
        spells.put(57, new TeleportSpell(51, 1576, 8939, 5, new Location(2662, 3307, 0), LAW_RUNE.setAmount(2), WATER_RUNE.setAmount(2))); //Ardougne teleport, Exp: 61
        spells.put(62, new TeleportSpell(58, 1576, 8939, 5, new Location(2891, 3678, 0), LAW_RUNE.setAmount(2), EARTH_RUNE.setAmount(2))); //Watch Tower teleport, Exp: 68
        spells.put(69, new TeleportSpell(61, 1576, 8939, 5, new Location(2547, 3113, 0), LAW_RUNE.setAmount(2), FIRE_RUNE.setAmount(2))); //Trollheim teleport, Exp: 68
        spells.put(72, new TeleportSpell(64, 1576, 8939, 5, new Location(2797, 2798, 1), LAW_RUNE.setAmount(2), FIRE_RUNE.setAmount(2), WATER_RUNE.setAmount(2))); //Ape Atoll teleport, Exp: 74

        //Alchemy spells
        spells.put(38, new AlchemySpell(21, 112, 712, 5, 0.6, NATURE_RUNE.setAmount(1), FIRE_RUNE.setAmount(3)));
        spells.put(59, new AlchemySpell(55, 112, 712, 5, 0.9, NATURE_RUNE.setAmount(1), FIRE_RUNE.setAmount(5)));
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