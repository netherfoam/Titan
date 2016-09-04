package org.maxgamer.rs.model.skill;

import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.ItemType;
import org.maxgamer.rs.util.Prove;

import java.util.LinkedList;
import java.util.List;

/**
 * @author netherfoam
 */
public class CraftableBuilder {
    /**
    * The input items, these are used up during the crafting process. Eg a bronze bar
    */
    protected List<ItemStack> inputs = new LinkedList<>();

    /**
     * The catalyst items, these are required but aren't destroyed during the process. Eg a hammer
     */
    protected List<ItemStack> catalysts = new LinkedList<>();

    /**
     * The output items, these are given after a successful craft
     */
    protected List<ItemStack> outputs = new LinkedList<>();

    /**
     * The Skill to require a level for, and to grant exp to upon successful crafting.
     */
    protected SkillType skill;

    /**
     * The level that is required to craft this
     */
    protected int level;

    /**
     * The exp granted for crafting this.
     */
    protected double exp;

    /**
     * Adds the given catalyst item to this builder
     * @param item the item to add
     * @return this
     */
    public CraftableBuilder catalyst(ItemStack item) {
        Prove.isNotNull(item, "item may not be null");

        catalysts.add(item);

        return this;
    }

    /**
     * Adds the given catalyst item to this builder
     * @param type the item type to add
     * @return this
     */
    public CraftableBuilder catalyst(ItemType type) {
        Prove.isNotNull(type, "item may not be null");

        return catalyst(type.toItem());
    }

    /**
     * Adds the given input item to this builder
     * @param item the input item
     * @return this
     */
    public CraftableBuilder input(ItemStack item) {
        Prove.isNotNull(item, "item may not be null");

        inputs.add(item);

        return this;
    }

    /**
     * Adds the given input item to this builder
     * @param type the item type to add
     * @return this
     */
    public CraftableBuilder input(ItemType type, int n) {
        Prove.isNotNull(type, "item may not be null");

        return input(type.toItem(n));
    }

    /**
     * Adds the given output item to this builder
     * @param item the output item
     * @return this
     */
    public CraftableBuilder output(ItemStack item) {
        Prove.isNotNull(item, "item may not be null");

        outputs.add(item);

        return this;
    }

    /**
     * Adds the given output item to this builder
     * @param type the item type to add
     * @return this
     */
    public CraftableBuilder output(ItemType type, int n) {
        Prove.isNotNull(type, "item may not be null");

        return output(type.toItem(n));
    }

    /**
     * Sets the experience given for crafting this
     * @param exp the experience granted
     * @return this
     */
    public CraftableBuilder experience(double exp) {
        Prove.isTrue(exp >= 0, "experience must be positive");

        this.exp = exp;

        return this;
    }

    /**
     * Sets the skill and level required to craft this
     * @param type the skill type
     * @param level the level
     * @return this
     */
    public CraftableBuilder skill(SkillType type, int level) {
        Prove.isNotNull(type, "skill may not be null");
        Prove.isTrue(level > 0, "level must be > 0, or none at all");

        this.skill = type;
        this.level = level;

        return this;
    }

    /**
     * Transforms this into a Craftable
     * @return the craftable item
     */
    public Craftable build() {
        ItemStack[] catalysts = this.catalysts.toArray(new ItemStack[this.catalysts.size()]);
        ItemStack[] inputs = this.inputs.toArray(new ItemStack[this.inputs.size()]);
        ItemStack[] outputs = this.outputs.toArray(new ItemStack[this.outputs.size()]);

        return new Craftable(catalysts, inputs, outputs, skill, level, exp);
    }
}
