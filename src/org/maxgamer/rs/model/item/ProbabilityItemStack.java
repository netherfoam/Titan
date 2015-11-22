package org.maxgamer.rs.model.item;

/**
 * The {@code ProbabilityItemStack} class is represented as an item 
 * obtainable by a probable chance.
 * 
 * @author Albert Beaupre
 */
public class ProbabilityItemStack extends ItemStack {

	protected final ItemStack item;
	protected final float probability; // 0.0 - 1.0

	/**
	 * Constructs a new {@code ProbabilityItemStack} from the specified 
	 * probable {@code item} and the specified {@code probability} for the item.
	 * 
	 * @param item
	 *            the item obtainable for the specified probability
	 * @param probability
	 *            the probability of obtaining the item
	 */
	public ProbabilityItemStack(ItemStack item, float probability) {
		super(item.getId(), item.getAmount(), item.getHealth());
		this.item = item;
		this.probability = probability;
	}
	
	public ProbabilityItemStack(int id, float probability) {
		this(ItemStack.create(id), probability);
	}
	
	public ProbabilityItemStack(int id, int amount, float probability) {
		this(ItemStack.create(id, amount), probability);
	}

	/**
	 * Returns a {@code ProbabilityItemStack} that was obtained from the 
	 * specified {@code items} array by their probabilities.
	 * 
	 * @param items
	 *            the array of probable items
	 * @return the item obtained from the array of items; return null otherwise
	 */
	public static ProbabilityItemStack getItemFromProbableStack(ProbabilityItemStack... items) {
		double chance = Math.random(), probability = 0.0;
		for (ProbabilityItemStack item : items) {
			probability += (double) item.probability;
			if (chance <= probability)
				return item;
		}
		return getItemClosestToChance(chance, items);
	}

	public static ProbabilityItemStack getHighestProbabilityItem(ProbabilityItemStack... items) {
		if (items.length == 0)
			throw new NullPointerException();
		ProbabilityItemStack highest = items[0];
		for (ProbabilityItemStack item : items) {
			if (item.probability > highest.probability)
				highest = item;
		}
		return items[0];
	}

	public static ProbabilityItemStack getItemClosestToChance(double probability, ProbabilityItemStack... items) {
		ProbabilityItemStack closest = items[0];
		for (ProbabilityItemStack item : items) {
			double diff = Math.abs(probability - item.probability);
			if (diff < Math.abs(probability - closest.probability))
				closest = item;
		}
		return closest;
	}

	/**
	 * @return the {@code ItemStack} of this {@code ProbabilityItemStack}
	 */
	public ItemStack getItem() {
		return item;
	}

	/**
	 * @return the probability of obtaining this {@code ProbabilityItemStack}
	 */
	public float getProbability() {
		return probability;
	}

}
