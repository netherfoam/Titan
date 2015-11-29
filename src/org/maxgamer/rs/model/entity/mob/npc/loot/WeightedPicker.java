package org.maxgamer.rs.model.entity.mob.npc.loot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author netherfoam
 */
public class WeightedPicker<T extends Weighted> {
	/** Our random number picker */
	private static Random rand = new Random();
	/** All the possible weighted objects which can be chosen */
	private LinkedList<T> options = new LinkedList<T>();
	/** The combined weight of all options */
	private int totalWeight = 0;
	
	/**
	 * Creates a new weighted picker.
	 */
	public WeightedPicker() {
	}
	
	/**
	 * Creates a new weighted picker.
	 * @param options The predefined set of Weighted objects which can be
	 *        selected.
	 */
	@SafeVarargs
	public WeightedPicker(T... options){
		this();
		for(T w : options){
			this.add(w);
		}
	}
	
	/**
	 * The weight of all items in the collection
	 * @return The weight of all items in the collection
	 */
	public int getTotalWeight() {
		return totalWeight;
	}
	
	/**
	 * The number of items in the collection.
	 * @return The number of items in the collection.
	 */
	public int getTotalSize() {
		return options.size();
	}
	
	/**
	 * Selects a random item from the collection.
	 * @return a random item from the collection.
	 */
	public T next() {
		if (getTotalWeight() <= 0) {
			return null; //No items to choose from.
		}
		
		double n = rand.nextDouble() * getTotalWeight();
		
		for (T option : options) {
			n -= option.getWeight();
			if (n <= 0) {
				return option;
			}
		}
		throw new RuntimeException("Failed to fetch next item in WeightedPicker!");
	}
	
	/**
	 * Returns a <b>copy</b> of the collection holding the items.
	 * @return a <b>copy</b> of the collection holding the items.
	 */
	public ArrayList<T> getOptions() {
		return new ArrayList<T>(options);
	}
	
	/**
	 * Adds the given option to the collection
	 * @param option The option to add to the collection.
	 */
	public void add(T option) {
		if (option.getWeight() <= 0) {
			throw new IllegalArgumentException("Weighted item " + option + " has a weight <= 0. It would never be chosen.");
		}
		if (options.add(option)) {
			//Added successfully!
			totalWeight += option.getWeight();
		}
	}
	
	/**
	 * Removes the given option from the collection
	 * @param option The option to remove from the collection.
	 */
	public void remove(T option) {
		if (options.remove(option)) {
			//Removal success!
			totalWeight -= option.getWeight();
		}
	}
	
	@Override
	public String toString() {
		return options.toString();
	}
}