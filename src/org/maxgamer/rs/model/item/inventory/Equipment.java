package org.maxgamer.rs.model.item.inventory;

import org.maxgamer.rs.model.entity.mob.Bonus;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.item.WieldType;

/**
 * @author netherfoam
 */
public class Equipment extends Container {
	public static final int SIZE = 14;
	
	private Mob owner;
	private ItemStack[] items;
	
	private int[] bonus;
	
	public Equipment(Mob owner) {
		items = new ItemStack[SIZE];
		bonus = new int[Bonus.COUNT];
		this.owner = owner;
	}
	
	public Mob getOwner() {
		return owner;
	}
	
	/**
	 * Fetches the bonus for the given type given by all of the equipment
	 * represented by this inventory.
	 * @param type the bonus type
	 * @return the bonus, potentially negative as some items give negative
	 *         effects but usually positive
	 */
	public int getBonus(int type) {
		return bonus[type];
	}
	
	@Override
	protected void setItem(int slot, ItemStack item) {
		ItemStack old = items[slot];
		if (old != null && old.getWeapon() != null) {
			for (int i = 0; i < Bonus.COUNT; i++) {
				bonus[i] -= old.getWeapon().getBonus(i);
			}
		}
		items[slot] = item;
		
		if (item != null && item.getWeapon() != null) {
			for (int i = 0; i < Bonus.COUNT; i++) {
				bonus[i] += item.getWeapon().getBonus(i);
			}
		}
	}
	
	public void set(WieldType type, ItemStack item) {
		this.set(type.getSlot(), item);
	}
	
	public ItemStack get(WieldType type) {
		return this.get(type.getSlot());
	}
	
	@Override
	public ItemStack get(int slot) {
		return items[slot];
	}
	
	@Override
	public int getSize() {
		return items.length;
	}
}