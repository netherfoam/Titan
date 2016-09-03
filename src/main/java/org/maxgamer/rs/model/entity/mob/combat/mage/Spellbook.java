package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public abstract class Spellbook {
    public static final ItemStack FIRE_RUNE = ItemStack.create(554), //Fire rune
            WATER_RUNE = ItemStack.create(555), //Water rune
            AIR_RUNE = ItemStack.create(556), //Air rune
            EARTH_RUNE = ItemStack.create(557), //Earth rune
            MIND_RUNE = ItemStack.create(558), //Mind rune
            BODY_RUNE = ItemStack.create(559), //Body rune
            DEATH_RUNE = ItemStack.create(560), //Death rune
            NATURE_RUNE = ItemStack.create(561), //Nature rune
            CHAOS_RUNE = ItemStack.create(562), //Chaos rune
            LAW_RUNE = ItemStack.create(563), //Law rune
            COSMIC_RUNE = ItemStack.create(564), //Cosmic rune
            BLOOD_RUNE = ItemStack.create(565), //Blood rune
            SOUL_RUNE = ItemStack.create(566), //Soul rune
            STEAM_RUNE = ItemStack.create(4694), //Steam rune
            MIST_RUNE = ItemStack.create(4695), //Mist rune
            DUST_RUNE = ItemStack.create(4696), //Dust rune
            SMOKE_RUNE = ItemStack.create(4697), //Smoke rune
            MUD_RUNE = ItemStack.create(4698), //Mud rune
            LAVA_RUNE = ItemStack.create(4699), //Lava rune
            ASTRAL_RUNE = ItemStack.create(9075); //Astral rune

    public static final ModernBook MODERN = new ModernBook();
    public static final AncientBook ANCIENT = new AncientBook();
    public static final LunarBook LUNAR = new LunarBook();
    private int childId;

    protected Spellbook(int childId) {
        this.childId = childId;
    }

    /**
     * An array of all of the spellbooks available, this is a deep copy
     *
     * @return An array of all of the spellbooks available
     */
    public static final Spellbook[] getBooks() {
        return new Spellbook[]{MODERN, ANCIENT, LUNAR};
    }

    /**
     * Fetches the spellbook for the given book interface id (192, 193 or 430)
     *
     * @param childId the child interface ID for the spellbook interface (eg
     *                modern = 192, ancient = 193)
     * @return the book or null if the childId is not found
     */
    public static Spellbook getBook(int childId) {
        for (Spellbook b : getBooks()) {
            if (b.childId == childId) return b;
        }
        return null;
    }

    /**
     * The unique interface ID for this spellbook. Modern is 192 This is used in
     * the constructor of a SideInterface class.
     *
     * @return The unique interface ID for this spellbook.
     */
    public int getChildId() {
        return childId;
    }

    public abstract Spell getSpell(int id);

    public abstract Spell getSpell(String name);

    public abstract Spell[] getSpells();
}