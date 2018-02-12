package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.entity.mob.Bonuses;
import org.maxgamer.rs.model.entity.mob.InventoryHolder;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.mob.MobEquipEvent;
import org.maxgamer.rs.model.item.EquipmentSet;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;
import org.maxgamer.rs.structure.configs.ConfigSection;
import org.maxgamer.rs.structure.configs.FileConfig;
import org.maxgamer.rs.structure.configs.YamlConfig;
import org.maxgamer.rs.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author netherfoam
 */
public class Equipment extends Container {
    public static final int SIZE = 14;
    private static final HashMap<String, EquipmentSet> SETS = new HashMap<>();
    private EquipmentSet currentSet;
    private ItemStack[] items;
    private Mob owner;
    private int[] bonus;

    /**
     * Creates a new Equipment set
     *
     * @param owner the owner of the set
     */
    public Equipment(Mob owner) {
        this.items = new ItemStack[SIZE];
        this.bonus = new int[Bonuses.COUNT];
        this.owner = owner;

        this.addListener(new ContainerListener() {
            @Override
            public void onSet(Container c, int slot, ItemStack old) {
                for (EquipmentSet set : SETS.values()) {
                    if (set.isWearingSet(Equipment.this)) {
                        currentSet = set;
                        break;
                    } else currentSet = null;
                }
            }
        });
    }

    public static void load() throws Exception {
        Log.info("Loading Equipment Sets...");
        FileConfig f = new YamlConfig(new File("./config/equipment_sets.yml"));
        f.reload();

        for (String name : f.keys()) {
            EquipmentSet set = new EquipmentSet(name);

            ConfigSection setConfig = f.getSection(name);
            for (String type : setConfig.keys()) {
                WieldType w;
                try {
                    w = WieldType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    Log.warning("WieldType " + type + " is not found. Please use one of " + Arrays.toString(WieldType.values()));
                    continue;
                }

                List<Integer> options = setConfig.getList(type, Integer.class, new LinkedList<Integer>());
                ItemStack[] items = new ItemStack[options.size()];
                for (int i = 0; i < options.size(); i++) {
                    items[i] = ItemStack.create(options.get(i));
                }
                set.setStack(w, items);
            }

            SETS.put(name, set);
        }
        Log.info("Loaded " + SETS.size() + " Equipment Sets.");
    }

    /**
     * The owner of the Equipment set
     *
     * @return The owner of the Equipment set
     */
    public Mob getOwner() {
        return owner;
    }

    /**
     * Fetches the bonus for the given type given by all of the equipment
     * represented by this inventory.
     *
     * @param type the bonus type
     * @return the bonus, potentially negative as some items give negative
     * effects but usually positive
     */
    public int getBonus(int type) {
        return bonus[type];
    }

    @Override
    protected void setItem(int slot, ItemStack item) {
        ItemStack old = items[slot];
        if (old != null && old.getWeapon() != null) {
            for (int i = 0; i < Bonuses.COUNT; i++) {
                bonus[i] -= old.getWeapon().getBonus(i);
            }
        }
        items[slot] = item;

        if (item != null && item.getWeapon() != null) {
            for (int i = 0; i < Bonuses.COUNT; i++) {
                bonus[i] += item.getWeapon().getBonus(i);
            }
        }
    }

    /**
     * Alias for set(int, ItemStack)
     *
     * @param type the WieldType
     * @param item the ItemStack
     */
    public void set(WieldType type, ItemStack item) {
        if (type == null) throw new NullPointerException("WieldType may not be null");

        this.set(type.getSlot(), item);
    }

    public ItemStack get(WieldType type) {
        if (type == null) throw new NullPointerException("WieldType may not be null");

        return this.get(type.getSlot());
    }

    /**
     * Returns true if the given set name is being worn. Case insensitive.
     *
     * @param setName the name of the set
     * @return true if it is worn, false if it is not.
     */
    public boolean isWearingSet(String setName) {
        if (currentSet == null) {
            EquipmentSet set = SETS.get(setName);
            if (set == null) {
                return false;
            }

            if (set.isWearingSet(this)) {
                this.currentSet = set;
                return true;
            }

            return false;
        }
        return currentSet.getName().equalsIgnoreCase(setName);
    }

    @Override
    public ItemStack get(int slot) {
        return items[slot];
    }

    @Override
    public int getSize() {
        return items.length;
    }

    public ItemStack getHat() {
        return get(WieldType.HAT);
    }

    public ItemStack getBody() {
        return get(WieldType.BODY);
    }

    public ItemStack getBoots() {
        return get(WieldType.BOOTS);
    }

    public ItemStack getCape() {
        return get(WieldType.CAPE);
    }

    public ItemStack getAmulet() {
        return get(WieldType.AMULET);
    }

    public ItemStack getShield() {
        return get(WieldType.SHIELD);
    }

    public ItemStack getLegs() {
        return get(WieldType.LEGS);
    }

    public ItemStack getRing() {
        return get(WieldType.RING);
    }

    public ItemStack getArrows() {
        return get(WieldType.ARROWS);
    }

    public ItemStack getWeapon() {
        return get(WieldType.WEAPON);
    }

    public ItemStack getGloves() {
        return get(WieldType.GLOVES);
    }

    /**
     * Attempts to equip the given item
     *
     * @param item          the item to equip from inventory (if applicable)
     * @param inventorySlot the slot, may be -1 for any
     * @throws ContainerException if the equip could not be performed
     */
    public void wear(ItemStack item, int inventorySlot) throws ContainerException {
        Container equip = this;
        Container inv = null;
        if (getOwner() instanceof InventoryHolder) {
            inv = ((InventoryHolder) getOwner()).getInventory();
            if (inventorySlot == -1 || !item.matches(inv.get(inventorySlot))) {
                inventorySlot = inv.getSlotOf(item);
            }
        }

        WieldType target = item.getWeapon().getSlot();
        ItemStack old = equip.get(target.getSlot());

        if (old != null) {
            if (old.matches(item)) {
                if (old.getAmount() < old.getStackSize()) {
                    // Add the stacks together

                    if (old.getAmount() + item.getAmount() > old.getStackSize()) {
                        // Not all of the items can be added to the slot, but
                        // some can->
                        long swap = old.getStackSize() - old.getAmount();
                        old = old.setAmount(old.getStackSize());
                        item = item.setAmount(item.getAmount() - swap);

                        if (inv != null) {
                            inv.set(inventorySlot, item);
                        }
                        equip.set(target.getSlot(), old);
                    } else {
                        // All of the items can be added to the slot->
                        inv.set(inventorySlot, null);
                        equip.set(target.getSlot(), old.setAmount(old.getAmount() + item.getAmount()));
                    }
                } else {
                    // We're already at the max stack size->
                    // Nothing will be accomplished by equipping this->
                    return;
                }
            } else {
                // The two items do not match-> Remove the old one, equip the
                // new one->
                MobEquipEvent event = new MobEquipEvent(getOwner(), old, item);
                event.call();
                if (event.isCancelled()) {
                    return;
                }
                inv.set(inventorySlot, old);
                equip.set(target.getSlot(), item);
            }
        } else {
            MobEquipEvent event = new MobEquipEvent(getOwner(), old, item);
            event.call();
            if (event.isCancelled()) {
                return;
            }
            // There is currently no other item equipped in the slot->
            inv.set(inventorySlot, null);

            equip.set(target.getSlot(), item);
        }

        ItemStack weapon = equip.get(WieldType.WEAPON.getSlot());
        ItemStack gloves = equip.get(WieldType.GLOVES.getSlot());
        if (weapon != null) {
            getOwner().getModel().setRenderAnimationId(weapon.getDefinition().getRenderAnimation());
        } else if (gloves != null) {
            getOwner().getModel().setRenderAnimationId(gloves.getDefinition().getRenderAnimation());
        } else {
            getOwner().getModel().setRenderAnimationId(1426);
        }
    }
}