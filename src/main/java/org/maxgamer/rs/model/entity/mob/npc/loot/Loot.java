package org.maxgamer.rs.model.entity.mob.npc.loot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Represents a NPC definition's loot set
 *
 * @author netherfoam
 */
public class Loot {
    /**
     * The random item selector
     */
    private WeightedPicker<LootItem> picker = new WeightedPicker<LootItem>();
    /**
     * The items which are guaranteed to be chosen (Weight is >= 100)
     */
    private LinkedList<LootItem> guarantee = new LinkedList<LootItem>();

    /**
     * Adds the given item to this loot set. If the loots weight >= 100, then
     * this item will be put in the guaranteed items list instead of the chance
     * item set.
     *
     * @param loot The loot
     * @return The loot
     */
    public void add(LootItem loot, boolean guarantee) {
        if (guarantee) {
            this.guarantee.add(loot);
        } else {
            this.picker.add(loot);
        }
    }

    /**
     * Removes the given loot from this loot set
     *
     * @param loot The loot to remove
     */
    public void remove(LootItem loot, boolean guarantee) {
        if (guarantee) {
            this.guarantee.remove(loot);
        } else {
            this.picker.remove(loot);
        }
    }

    /**
     * A copy of all the possible LootSet's which could be chosen by this Loot
     * object.
     *
     * @return A copy of all the possible LootSet's which could be chosen by
     * this Loot object.
     */
    public ArrayList<LootItem> getOptions() {
        return picker.getOptions();
    }

    /**
     * The collection of loots which will always be dropped by this loot object.
     *
     * @return The collection of loots which will always be dropped by this loot
     * object.
     */
    public Collection<LootItem> getGuarantees() {
        return guarantee;
    }

    /**
     * A list of items which this NPC should drop.
     *
     * @return A list of items which this NPC should drop.
     */
    public LinkedList<LootItem> next() {
        LinkedList<LootItem> items = new LinkedList<LootItem>(guarantee);
        LootItem spec = random();
        if (spec != null && spec.getItemStack() != null) {
            items.add(0, spec); //Puts the special item on top
        }
        return items;
    }

    /**
     * Gets a random item which is not a 100% drop from this mob. May return
     * null.
     *
     * @return a random item which is not a 100% drop from this mob.
     */
    public LootItem random() {
        return picker.next();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("Possible: " + picker.toString());
        sb.append("Always: " + guarantee.toString());
        return sb.toString();
    }
}