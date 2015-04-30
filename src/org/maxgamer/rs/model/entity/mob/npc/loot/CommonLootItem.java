package org.maxgamer.rs.model.entity.mob.npc.loot;

import java.util.Random;

import org.maxgamer.rs.model.item.ItemStack;

/**
 * @author netherfoam
 */
public class CommonLootItem extends LootItem {
	private static Random rand = new Random();
	/** The item stack this represents */
	private int itemId = -1;
	/** The minimum amount of items to spawn */
	private int min;
	/** The variance in the number of items to spawn. Eg, max = min + spread */
	private int spread;
	
	/**
	 * Generates a LootItem with the given stats
	 * @param iStack The ItemStack
	 * @param weight The chance the item will be picked by a WeightedPicker
	 * @param min The minimum amount to return
	 * @param max The maximum amount to return The amount of items returned will
	 *        be uniformly distributed between min and max.
	 */
	public CommonLootItem(ItemStack iStack, double weight, int min, int max) {
		this(iStack.getId(), weight, min, max);
	}
	
	public CommonLootItem(int itemId, double weight, int min, int max) {
		super(weight);
		this.itemId = itemId;
		this.min = min;
		this.spread = max - min;
		if (spread < 0) {
			throw new IllegalArgumentException("Min must be <= max chance for loot.");
		}
		if (min < 0) {
			throw new IllegalArgumentException("Min amount must be > 0 for loot!");
		}
		//Because, if spread is 2, Random.nextInt(2) will return 0 or 1
		//Not 0, 1 or 2 as it should. Incrementing it fixes this.
		this.spread++;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return spread + min - 1;
	}
	
	/**
	 * Returns a copy of the item
	 * @return a copy of the item
	 */
	@Override
	public ItemStack getItemStack() {
		if (itemId < 0) {
			return null;
		}
		return ItemStack.create(itemId, rand.nextInt(spread) + min);
	}
}