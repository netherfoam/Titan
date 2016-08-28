package org.maxgamer.rs.model.item.condition;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.skill.SkillType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author netherfoam
 */
public class ItemFlagSet {
    private Map<Integer, Object> values;

    public Map<Integer, Object> getValues() {
        return values;
    }

    public ItemFlagSet(Map<Integer, Object> values) {
        this.values = values;
    }

    public boolean isBroken() {
        return this.is(59, 1);
    }

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
     * Appears to be the skill requirement, but the skill is arbitrary
     * @return
     */
    public Integer getSkillRequirement() {
        return getInt(750);
    }

    /**
     * Attack type - such as a mace, or a sword or an axe. These determine what methods
     * of attack there are for melee weapons, eg. crush and slash or slash and stab.
     * @return the attack type
     */
    public Integer getAttackType() {
        Integer v = getInt(686);
        if(v == null) return null;

        return v;
    }

    public Integer getLevel(SkillType type) {
        Integer a = getInt(770);
        if(a == null) return null;

        if(a == type.getId()) {
            return getInt(771);
        }

        Integer b = getInt(772);
        if(b == null) return null;

        if(b == type.getId()) {
            return getInt(773);
        }

        Integer c = getInt(774);
        if(c == null) return null;

        if(c == type.getId()) {
            return getInt(775);
        }

        return null;
    }

    public Integer getInt(int key) {
        return (Integer) values.get(key);
    }

    public String getString(int key) {
        return (String) values.get(key);
    }

    public boolean is(int key, Integer value) {
        Integer stored = getInt(key);
        if(stored == value) return true;

        if(stored == null || value == null) return false;

        return stored.equals(value);
    }

    public boolean is(int key, String value) {
        String stored = getString(key);
        if(stored == value) return true;

        if(stored == null || value == null) return false;

        return stored.equals(value);
    }

    public boolean has(int key) {
        return values.containsKey(key);
    }
}
