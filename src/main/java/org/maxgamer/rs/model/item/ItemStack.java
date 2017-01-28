package org.maxgamer.rs.model.item;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.item.weapon.EquipmentType;
import org.maxgamer.rs.repository.ItemTypeRepository;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * @author netherfoam
 */
public class ItemStack implements Comparable<ItemStack>, YMLSerializable, Interactable {
    private static HashMap<String, WeakReference<ItemStack>> cache = new HashMap<>();
    /**
     * The generic currency in the game
     */
    public static final ItemStack COINS = ItemStack.create(995);

    private final int id;
    private final long amount;
    private final int health;
    private transient ItemType definition;

    protected ItemStack(int id, long amount, int health) {
        this.id = id;
        this.amount = amount;
        this.health = health;
        if (id < 0) {
            throw new RuntimeException("Item ID may not be <= 0. Given ID #" + id);
        }
        if (id >= 0 && getDefinition() == null) {
            throw new RuntimeException("No such item definition exists for Item with ID #" + id + "!");
        }
        if (amount < 0) {
            throw new RuntimeException("ID > 0 and Amount < 0 is an invalid item state. Given ID: " + id + " amount: " + amount);
        }
    }

    public static ItemStack create(ConfigSection s) {
        return ItemStack.create(s.getInt("id"), s.getLong("amount", 1), s.getInt("health", 0));
    }

    /**
     * Fetches a reference for an item with the given id, amount and health.
     * This uses a reference pool for items, so that memory is saved where
     * possible.
     *
     * @param id     The ID
     * @param amount the amount
     * @return The itemstack.
     */
    public static ItemStack create(int id, long amount, int health) {
        if (amount <= 0) {
            return null;
        }

        WeakReference<ItemStack> ref = cache.get(id + "-" + amount + "-" + health);
        ItemStack item;

        if (ref == null || (item = ref.get()) == null) {
            item = new ItemStack(id, amount, health);
            ref = new WeakReference<>(item);
            cache.put(id + "-" + amount + "-" + health, ref);
        }
        return item;
    }

    /**
     * Effecitvely returns create(ID, 0, health), but skips the safety check to
     * ensure that the amount is > 0. This creats an itemstack where getAmount()
     * == 0 but the itemstack is not null like the return value of create().
     *
     * @param id     the id of the item
     * @param health the health of the item
     * @return an itemstack representing the item with an amount of 0.
     */
    public static ItemStack createEmpty(int id, int health) {
        final int amount = 0;

        WeakReference<ItemStack> ref = cache.get(id + "-" + amount + "-" + health);

        ItemStack item;
        if (ref == null || (item = ref.get()) == null) {
            item = new ItemStack(id, amount, health);
            ref = new WeakReference<>(item);
            cache.put(id + "-" + amount + "-" + health, ref);
        }
        return item;
    }

    public static ItemStack create(int id, long amount) {
        return ItemStack.create(id, amount, 0);
    }

    public static ItemStack create(int id) {
        return ItemStack.create(id, 1);
    }

    public ItemType getDefinition() {
        if (this.definition == null) {
            this.definition = Core.getServer().getDatabase().getRepository(ItemTypeRepository.class).find(this.id);
        }

        return this.definition;
    }

    public int getCharges() {
        return getDefinition().getCharges();
    }

    public ItemStack setCharges(int charges) {
        return ItemStack.create(getDefinition().toCharges(charges).getId(), getAmount(), getHealth());
    }

    public EquipmentType getWeapon() {
        return getDefinition().getWeapon();
    }

    public boolean isNoteable() {
        ItemStack noted = this.getNoted();
        return noted != this;
    }

    public ItemStack getNoted() {
        ItemStack noted = ItemStack.create(this.getId() + 1, this.getAmount(), this.getHealth());
        if (noted.getDefinition().isNoted()) {
            return noted;
        }

        return this; //Not noteable.
    }

    public ItemStack getUnnoted() {
        if (!getDefinition().isNoted()) return this;
        if (this.getId() == 10828) {
            return ItemStack.create(10843, this.getAmount(), this.getHealth());
        } else {
            return ItemStack.create(this.getId() - 1, this.getAmount(), this.getHealth());
        }
    }

    public long getAmount() {
        return amount;
    }

    public ItemStack setAmount(long amount) {
        return ItemStack.create(this.id, amount, this.health);
    }

    public int getHealth() {
        return health;
    }

    public ItemStack setHealth(int health) {
        return ItemStack.create(this.id, this.amount, health);
    }

    public int getId() {
        return id;
    }

    public boolean matches(ItemStack i) {
        return i != null && (i.id == this.id || getDefinition().stacksWith(i.id) || i.getDefinition().stacksWith(this.id)) && i.health == this.health;
    }

    @Override
    public String toString() {
        return getDefinition().getName() + "(" + getId() + ") x" + getAmount() + (health == 0 ? "" : " health=" + health);
    }

    @Override
    public int compareTo(ItemStack i) {
        long n = i.getId() - this.getId();
        if (n != 0) {
            return (int) n;
        }
        n = i.getAmount() - this.getAmount();
        if (n != 0) {
            return (int) Math.min(n, Integer.MAX_VALUE);
        }
        n = i.getHealth() - this.getHealth();
        return (int) Math.min(n, Integer.MAX_VALUE);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof ItemStack)) return false;
        ItemStack i = (ItemStack) o;
        if (i.id != this.id) return false;
        return i.amount == this.amount && i.health == this.health;

    }

    public String getName() {
        return getDefinition().getName();
    }

    public String getExamine() {
        return getDefinition().getName();
    }

    public long getStackSize() {
        return getDefinition().getMaxStack();
    }

    @Override
    public ConfigSection serialize() {
        ConfigSection s = new ConfigSection();
        s.set("id", this.id);
        s.set("amount", this.amount);
        if (this.health != 0) s.set("health", this.health);
        return s;
    }

    @Override
    public void deserialize(ConfigSection map) {
        throw new RuntimeException("ItemStacks must be deserialized with the constructor.");
    }

    /**
     * the model used for rendering this equipment piece
     *
     * @return
     */
    /*
     * public int getEquipId(boolean male) { ItemProto p = getDefinition();
	 * 
	 * int wornId; if(male) wornId = p.maleWornModelId2; else wornId =
	 * p.femaleWornModelId2; //if(male) return getDefinition().maleWornModelId1;
	 * //else return getDefinition().femaleWornModelId1;
	 * System.out.println(getDefinition().getId() + ": " + getName() +
	 * " worn ID: " + wornId); System.out.println(getDefinition().toString());
	 * return wornId; }
	 */
    public String[] getInventoryOptions() {
        return getDefinition().getInventoryOptions();
    }

    public String getGroundOption(int id) {
        return getDefinition().getGroundOptions()[id];
    }

    public boolean hasGroundOption(String name) {
        for (String s : getGroundOptions()) {
            if (s == null) continue;
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getInventoryOption(int id) {
        return getDefinition().getInventoryOptions()[id];
    }

    public String[] getGroundOptions() {
        return getDefinition().getGroundOptions();
    }

    public boolean isNoted() {
        return getDefinition().isNoted();
    }

    public boolean hasOption(String name) {
        for (String s : getDefinition().getInventoryOptions()) {
            if (s == null) continue;
            if (name.equals(s)) return true;
        }
        return false;
    }

    @Override
    public String[] getOptions() {
        return getDefinition().getInventoryOptions();
    }

    /**
     * The total value of this ItemStack - The amount multiplied by the value
     *
     * @return The total value of this ItemStack - The amount multiplied by the value
     */
    public long getValue() {
        return this.getAmount() * getDefinition().getValue();
    }
}
