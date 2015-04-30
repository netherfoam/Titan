package org.maxgamer.rs.model.entity.mob.npc.loot;

import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public abstract class LootItem implements Weighted {
	/** The weighting/chance this item will be selected */
	private double chance;
	
	public LootItem(double chance) {
		this.chance = chance;
	}
	
	/**
	 * The chance this item will be selected from a pool.
	 */
	@Override
	public final double getWeight() {
		return chance;
	}
	
	/**
	 * Returns a copy of the item
	 * @return a copy of the item
	 */
	public abstract ItemStack getItemStack();
	
	@Override
	public String toString() {
		return getItemStack() + ": " + getWeight();
	}
}