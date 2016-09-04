package org.maxgamer.rs.model.skill;

import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerState;

/**
 * Completely optional API class to help with the nuances of crafting recipes.
 *
 * @author netherfoam
 */
public class Craftable {
    /**
     * The input items, these are used up during the crafting process. Eg a bronze bar
     */
    protected ItemStack[] inputs;

    /**
     * The catalyst items, these are required but aren't destroyed during the process. Eg a hammer
     */
    protected ItemStack[] catalysts;

    /**
     * The output items, these are given after a successful craft
     */
    protected ItemStack[] outputs;

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

    protected Craftable() {
        super();
    }

    /**
     * Creates a new Craftable
     *
     * @param catalysts the items which are required but not used, eg Hammer. Nullable.
     * @param input     the items which are required and consumed, eg. Bar. Nullable.
     * @param output    the items which are given, eg Bronze full helm. Not nullable.
     * @param skill     the SkillType that is used
     * @param level     the level in the skill to require
     * @param exp       the exp in the skill to grant after a craft
     */
    public Craftable(ItemStack[] catalysts, ItemStack[] input, ItemStack[] output, SkillType skill, int level, double exp) {
        this();

        if (catalysts == null) catalysts = new ItemStack[0];
        if (input == null) input = new ItemStack[0];
        if (output == null) throw new NullPointerException("Output items may not be null.. If you want no output, use an ItemStack[0]");

        if (skill == null && ((level != 1 && level != 0) || exp != 0)) {
            throw new IllegalArgumentException("If SkillType is null, then level must be 0 (Gave " + level + ") and exp must be 0 (Gave " + exp + ")");
        }

        this.catalysts = catalysts;
        this.inputs = input;
        this.outputs = output;
        this.skill = skill;
        this.level = level;
        this.exp = exp;
    }

    /**
     * The input items that are consumed. Never null.
     *
     * @return The input items that are consumed
     */
    public ItemStack[] getInputs() {
        return inputs.clone();
    }

    /**
     * The output items. Never null.
     *
     * @return The output items
     */
    public ItemStack[] getOutputs() {
        return outputs.clone();
    }

    /**
     * The input items which are not consumed. Never null
     *
     * @return The input items which are not consumed
     */
    public ItemStack[] getCatalysts() {
        return catalysts.clone();
    }

    /**
     * The SkillType that is used for this craft. Nullable.
     *
     * @return The SkillType that is used for this craft
     */
    public SkillType getSkill() {
        return this.skill;
    }

    /**
     * The level required, if a SkillType is assigned
     *
     * @return The level required
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * The experience granted on a successful craft
     *
     * @return The experience granted
     */
    public double getExperience() {
        return this.exp;
    }

    /**
     * Overridable when a Mob successfully crafts.
     *
     * @param mob the mob that crafted
     */
    public void onCraft(Mob mob) {
        // Overrideable
    }

    /**
     * Returns true if the given mob can craft this recipe, after checking skills and inventory.
     *
     * @param mob the crafter
     * @return Returns true if the given mob can craft this recipe
     */
    public boolean craftable(Mob mob) {
        try {
            runSkills(mob);

            if (mob instanceof InventoryHolder) {
                Container c = ((InventoryHolder) mob).getInventory().getState();
                runItems(c);
            }
        } catch (CraftFail f) {
            return false;
        }

        return true;
    }

    /**
     * Crafts this.
     *
     * @param mob The crafter
     * @throws CraftFail if the crafting fails. The message in the exception can be used to tell the player why it failed.
     */
    public void craft(Mob mob) throws CraftFail {
        runSkills(mob);

        if (mob instanceof InventoryHolder) {
            ContainerState c = ((InventoryHolder) mob).getInventory().getState();
            runItems(c);
            c.apply();
        }

        if (this.skill != null) {
            mob.getSkills().addExp(this.skill, exp);
        }

        onCraft(mob);
    }

    private void runSkills(Mob mob) throws CraftFail {
        if (this.skill == null) return;

        if (mob.getSkills().getLevel(this.skill, true) < level) {
            throw new CraftFail("You need a " + this.skill.getName() + " level of " + level + " to craft that.");
        }
    }

    private void runItems(Container c) throws CraftFail {
        for (ItemStack catalyst : this.catalysts) {
            if (c.contains(catalyst) == false) {
                if (catalyst.getAmount() == 1) {
                    throw new CraftFail("You need a " + catalyst.getName() + " to craft that.");
                } else {
                    throw new CraftFail("You need " + catalyst.getAmount() + " " + catalyst.getName() + " to craft that.");
                }
            }
        }

        for (ItemStack input : this.inputs) {
            try {
                c.remove(input);
            } catch (ContainerException e) {
                if (input.getAmount() == 1) {
                    throw new CraftFail("You need a " + input.getName() + " to craft that.");
                } else {
                    throw new CraftFail("You need " + input.getAmount() + " " + input.getName() + " to craft that.");
                }
            }
        }

        for (ItemStack output : outputs) {
            try {
                c.add(output);
            } catch (ContainerException e) {
                throw new CraftFail("You need more space to craft that.");
            }
        }
    }
}