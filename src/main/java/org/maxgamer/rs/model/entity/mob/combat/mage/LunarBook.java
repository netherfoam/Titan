package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.map.Location;

import java.util.HashMap;

/**
 * @author netherfoam
 */
public class LunarBook extends Spellbook {
    public static final LunarBook LUNAR = new LunarBook();

    private HashMap<Integer, Spell> spells = new HashMap<>();

    protected LunarBook() {
        super(430);

        spells.put(38, new TeleportSpell(0, 1576, 8939, 5, new Location(3221, 3218, 0))); // Home tele
        spells.put(42, new TeleportSpell(0, 1576, 8939, 5, new Location(2106, 3914, 0))); // Moonclan teleport
        spells.put(53, new TeleportSpell(0, 1576, 8939, 5, new Location(3274, 4857, 0))); // Ourania teleport
        spells.put(46, new TeleportSpell(0, 1576, 8939, 5, new Location(2531, 3758, 0))); // Waterbirth teleport
        spells.put(22, new TeleportSpell(0, 1576, 8939, 5, new Location(3223, 3224, 0))); // Barbarian outpost teleport
        spells.put(43, new TeleportSpell(0, 1576, 8939, 5, new Location(2809, 3436, 0))); // Catherby bank
        spells.put(50, new TeleportSpell(0, 1576, 8939, 5, new Location(2953, 3889, 0))); // Ice Plateau
        //spells.put()
        // Bake pi: 37
        // Cure Poison: 23
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