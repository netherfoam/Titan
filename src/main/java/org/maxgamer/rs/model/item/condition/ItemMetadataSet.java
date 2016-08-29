package org.maxgamer.rs.model.item.condition;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.skill.SkillType;
import org.maxgamer.rs.util.Prove;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple interface object which provides an easier and more readable method of interpreting
 * the metadata on items
 *
 * @author netherfoam
 */
public class ItemMetadataSet {
    /**
     * The underlying metadata values
     */
    private Map<Integer, Object> data;

    /**
     * Constructs a new ItemMetadataSet for the given values
     * @param values the values
     */
    public ItemMetadataSet(Map<Integer, Object> values) {
        Prove.isNotNull(values, "Map may not be null");

        this.data = values;
    }

    /**
     * Returns a reference to the map used in the constructor
     * @return the underlying key-value pairs
     */
    public Map<Integer, Object> getData() {
        return data;
    }

    /**
     * Returns true if the object has a broken flag, eg. Broken pickaxe heads
     * @return true if the object has a broken flag
     */
    public boolean isBroken() {
        return this.is(59, 1);
    }

    /**
     * Returns the list of ItemStacks which are required to build this item in a Player Owned House
     * @return the list of materials
     */
    public List<ItemStack> getConstructionMaterials() {
        ArrayList<ItemStack> list = new ArrayList<>(2);

        for(int i = 211; i <= 221; i += 2) {
            Integer type = getInt(i);
            if(type == null) continue;

            list.add(ItemStack.create(type, getInt(i + 1)));
        }

        return list;
    }

    /**
     * Returns true if the item is a regular skill cape.
     * This does not return true if the item is a trimmed skill cape.
     *
     * @return true if item is a skill cape
     */
    public boolean isSkillCape() {
        return is(258, 1);
    }

    /**
     * Returns true if the item is a trimmed skill cape.
     * This does not return true if the item is a regular skill cape.
     *
     * @return true if item is a trimmed skill cape
     */
    public boolean isTrimmedSkillCape() {
        return is(259, 1);
    }

    /**
     * Returns the SkillType that this skill cape belongs to.
     * If this is not a skill cape, returns null.
     *
     * @return the skill type for this cape
     */
    public SkillType getSkillCapeType() {
        Integer v = getInt(277);
        if(v == null) return null;

        return SkillType.forId(v);
    }

    /**
     * Eg. "Clear circle" is 1, but "Orange almond" is 2, and "Yellow Pentagon" is 5.
     * @return the shape ID, 1-6 inclusive values.
     */
    public int getShapeType() {
        return getInt(358);
    }

    /**
     * Eg. "Blue Hexagon" and "Blue pentagon" are 5, but "Green pentagon" is 4.
     * @return the shape ID, 1-7 inclusive values.
     */
    public int getShapeColour() {
        return getInt(359);
    }

    /**
     * The dialogue option at pos. Eg for amulet of glory, pos[0] is "Edgeville", pos[3] is Al Kharid. For ring of duelling, pos[0]
     * is "Duel Arena".
     * @param pos the position 0-3 inclusive
     * @return the name of the option
     */
    public String getDialogueOption(int pos) {
        return getString(pos + 528);
    }

    /**
     * Possibly the maximum number of spirit shards that this pouch can carry
     * @return Possibly the maximum number of spirit shards that this pouch can carry
     */
    public ItemStack getSpiritShards() {
        Integer a = getInt(540);
        if(a == null) return null;

        return ItemStack.create(a, getInt(541));
    }

    /**
     * Possibly the charms required to make a pouch? Always amount=1, but the item id is a
     * red/gold/blue charm item id
     * @return * Possibly the charms required to make a pouch?
     */
    public ItemStack getCharms() {
        Integer a = getInt(542);
        if(a == null) return null;

        return ItemStack.create(a, getInt(543));
    }

    /**
     * Returns true if this item has a special attack associated with it
     * @return true if this item has a special attack associated with it
     */
    public boolean hasSpecialAttack() {
        return has(687);
    }

    /**
     * Returns true if this item is a runecrafting pouch
     * @return true if this item is a runecrafting pouch
     */
    public boolean isRunecraftingPouch() {
        return is(723, 1);
    }

    /**
     * The level required to wear this armour
     * @param skillType the skill type
     * @return The level required to wear this armour
     */
    public Integer getWearLevel(SkillType skillType) {
        for(int i = 749; i <= 761; i += 2) {
            Integer t = getInt(i);
            if(t == null || t != skillType.getId()) continue;

            return getInt(i + 1);
        }

        return null;
    }

    /**
     * Fetches all SkillTypes that are required to equip the item
     * @return all SkillTypes that are required to equip the item
     */
    public List<SkillType> getWearSkills() {
        ArrayList<SkillType> types = new ArrayList<>(3);
        for(int i = 749; i <= 761; i += 2) {
            Integer t = getInt(i);
            if(t == null) continue;

            types.add(SkillType.forId(t));
        }

        return types;
    }

    /**
     * True if this is some kind of "Squad" item
     * @return True if this is some kind of "Squad" item
     */
    public boolean isSquad() {
        return is(802, 1);
    }

    public boolean isDefeatedSquad() {
        return is(803, 1);
    }

    public Integer getSquadType() {
        return getInt(805);
    }

    public boolean isBarbarianAssaultReward() {
        return is(954, 1);
    }

    public Integer getBarbarianAssaultWaveTicketNumber() {
        return getInt(955);
    }

    /**
     * The idle, standing animation to use for this weapon when it's wielded
     * @return The idle wield animation
     */
    public Integer getRenderAnimation() {
        return getInt(644);
    }

    /**
     * Attack type - such as a mace, or a sword or an axe. These determine what methods
     * of attack there are for melee weapons, eg. crush and slash or slash and stab.
     *
     * See also: {@link org.maxgamer.rs.model.entity.mob.combat.AttackStyle}
     *
     * @return the attack type
     */
    public Integer getAttackType() {
        Integer v = getInt(686);
        if(v == null) return null;

        return v;
    }

    /**
     * Get the skill level required to use this item
     * @param type the skill type
     * @return the required level or null if no level is required
     */
    public Integer getCraftLevel(SkillType type) {
        Prove.isNotNull(type, "SkillType may not be null");

        for(int i = 770; i <= 774; i += 2) {
            Integer v = getInt(i);
            if(v == null || v != type.getId()) continue;

            return getInt(i + 1);
        }

        return null;
    }

    /**
     * Fetches all SkillTypes that are required to equip the item
     * @return all SkillTypes that are required to equip the item
     */
    public List<SkillType> getCraftSkills() {
        ArrayList<SkillType> types = new ArrayList<>(3);
        for(int i = 770; i <= 774; i += 2) {
            Integer t = getInt(i);
            if(t == null) continue;

            types.add(SkillType.forId(t));
        }

        return types;
    }

    /**
     * Gets the integer under the given key. This shouldn't really be used externally, instead
     * convenience methods should be defined for readability.
     * @param key the key
     * @return the value or null if not found
     * @throws ClassCastException if the given key is a String instead
     */
    public Integer getInt(int key) {
        return (Integer) data.get(key);
    }

    /**
     * Gets the String under the given key. This shouldn't really be used externally, instead
     * convenience methods should be defined for readability.
     * @param key the key
     * @return the value or null if not found
     * @throws ClassCastException if the given key is an Integer instead
     */
    public String getString(int key) {
        return (String) data.get(key);
    }

    /**
     * Returns true if the given key is of the given value
     * @param key the key
     * @param value the value
     * @return true if value == map.get(key) || value.equals(map.get(key))
     */
    public boolean is(int key, Integer value) {
        Integer stored = getInt(key);
        if(stored == value) return true;

        if(stored == null || value == null) return false;

        return stored.equals(value);
    }

    /**
     * Returns true if the given key is of the given value
     * @param key the key
     * @param value the value
     * @return true if value == map.get(key) || value.equals(map.get(key))
     */
    public boolean is(int key, String value) {
        String stored = getString(key);
        if(stored == value) return true;

        if(stored == null || value == null) return false;

        return stored.equals(value);
    }

    /**
     * Returns true if the map has the given key
     * @param key the key
     * @return true if the map contains the given key (the value may be null though)
     */
    public boolean has(int key) {
        return data.containsKey(key);
    }
}
