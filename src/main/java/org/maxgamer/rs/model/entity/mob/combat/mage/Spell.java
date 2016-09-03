package org.maxgamer.rs.model.entity.mob.combat.mage;

import org.maxgamer.rs.model.entity.mob.*;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.model.item.inventory.Container;
import org.maxgamer.rs.model.item.inventory.ContainerException;
import org.maxgamer.rs.model.item.inventory.ContainerState;
import org.maxgamer.rs.model.skill.SkillType;

import java.util.ArrayList;

/**
 * Represents some kind of spell that a player or mob may cast
 *
 * @author netherfoam
 */
public abstract class Spell {
    private ItemStack[] runes;
    private int level;
    private Graphics gfx;
    private Animation anim;
    private int castTime;

    public Spell(int level, int gfx, int anim, int castTime, ItemStack... runes) {
        for (ItemStack rune : runes) {
            if (rune == null) throw new IllegalArgumentException("Spell may not have a NULL rune requirement");
        }

        if (castTime <= 0) {
            throw new IllegalArgumentException("CastTime must be > 0");
        }

        this.level = level;
        if (gfx >= 0) this.gfx = new Graphics(gfx);
        if (anim >= 0) this.anim = new Animation(anim);
        this.runes = runes;
        this.castTime = castTime;
    }

    public boolean hasRequirements(Mob mob) {
        if (mob.getSkills().getLevel(SkillType.MAGIC, true) < this.getLevel()) {
            return false;
        }

        ArrayList<ItemStack> runes = new ArrayList<ItemStack>(this.runes.length);
        for (ItemStack rune : this.runes) {
            runes.add(rune);
        }

        if (mob instanceof InventoryHolder) {
            if (mob instanceof EquipmentHolder) {
                Container equip = ((EquipmentHolder) mob).getEquipment();

                ItemStack wep = equip.get(WieldType.WEAPON.getSlot());
                if (wep != null) {
                    String name = wep.getName().toLowerCase();
                    if (name.contains("staff")) {
                        ItemStack free = null;
                        if (name.contains("air")) {
                            free = Spellbook.AIR_RUNE;
                        } else if (name.contains("water")) {
                            free = Spellbook.WATER_RUNE;
                        } else if (name.contains("earth")) {
                            free = Spellbook.EARTH_RUNE;
                        } else if (name.contains("fire")) {
                            free = Spellbook.FIRE_RUNE;
                        }

                        if (free != null) {
                            for (int i = 0; i < runes.size(); i++) {
                                if (runes.get(i).matches(free)) {
                                    runes.remove(i);
                                    i--;
                                }
                            }
                        }
                    }
                }
            }

            Container inv = ((InventoryHolder) mob).getInventory();
            ContainerState state = inv.getState();
            for (ItemStack rune : runes) {
                try {
                    state.remove(rune);
                } catch (ContainerException e) {
                    //TODO: Message?
                    return false;
                }
            }
            //We just discard our state. Do not apply it :)
        }

        return true;
    }

    public boolean takeConsumables(Mob mob) {
        ArrayList<ItemStack> runes = new ArrayList<ItemStack>(this.runes.length);
        for (ItemStack rune : this.runes) {
            runes.add(rune);
        }

        if (mob instanceof InventoryHolder) {
            if (mob instanceof EquipmentHolder) {
                Container equip = ((EquipmentHolder) mob).getEquipment();

                ItemStack wep = equip.get(WieldType.WEAPON.getSlot());
                if (wep != null) {
                    String name = wep.getName().toLowerCase();
                    if (name.contains("staff")) {
                        ItemStack free = null;
                        if (name.contains("air")) {
                            free = Spellbook.AIR_RUNE;
                        } else if (name.contains("water")) {
                            free = Spellbook.WATER_RUNE;
                        } else if (name.contains("earth")) {
                            free = Spellbook.EARTH_RUNE;
                        } else if (name.contains("fire")) {
                            free = Spellbook.FIRE_RUNE;
                        }

                        if (free != null) {
                            for (int i = 0; i < runes.size(); i++) {
                                if (runes.get(i).matches(free)) {
                                    runes.remove(i);
                                    i--;
                                }
                            }
                        }
                    }
                }
            }

            Container inv = ((InventoryHolder) mob).getInventory();
            ContainerState state = inv.getState();
            for (ItemStack rune : runes) {
                try {
                    state.remove(rune);
                } catch (ContainerException e) {
                    //TODO: Message?
                    return false;
                }
            }
            state.apply(); //Actually removes the runes
        }
        return true;
    }

    public ItemStack[] getRunes() {
        return runes.clone(); //Deep copy
    }

    public int getLevel() {
        return level;
    }

    public Graphics getGraphics() {
        return gfx;
    }

    public Animation getAnimation() {
        return anim;
    }

    /**
     * Animates the given mob and makes them perform the graphics of this Spell,
     * as if they were casting it.
     *
     * @param mob the mob who is to enact casting the spell
     */
    public void displayCast(Mob mob) {
        if (gfx != null) mob.getUpdateMask().setGraphics(gfx);
        if (anim != null) mob.getUpdateMask().setAnimation(anim, 25);
    }

    /**
     * The time taken to "warmup" for this spell to be cast.
     *
     * @return The time taken to "warmup" for this spell to be cast.
     */
    public int getCastTime() {
        return castTime;
    }
}