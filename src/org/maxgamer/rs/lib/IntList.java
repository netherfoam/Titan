package org.maxgamer.rs.lib;

/**
 * @author netherfoam
 */
public class IntList {
	/**
	 * The number of ints we have in the array which are valid
	 */
	private int size = 0;
	/**
	 * The values for this list. Only values ((0 [Exclusive]) to (size - 1
	 * [Inclusive])) are valid.
	 */
	private int[] data;
	
	/**
	 * Constructs a new int list of the given size.
	 * @param initSize The size for the list.
	 */
	public IntList(int initSize) {
		data = new int[initSize];
	}
	
	/**
	 * Constructs a new int list of size 16.
	 */
	public IntList() {
		this(16);
	}
	
	/**
	 * Appends the given int to the end of the list.
	 * @param i
	 */
	public void add(int i) {
		checkSize();
		data[size++] = i;
	}
	
	/**
	 * Deletes and returns the value at the end of the list.
	 * @return
	 */
	public int remove() {
		if (size <= 0) {
			throw new IndexOutOfBoundsException("No elements in IntList!");
		}
		return data[--size];
	}
	
	/**
	 * Appends the given int to the end of the list.
	 * @param i
	 */
	public void push(int i) {
		add(i);
	}
	
	/**
	 * Deletes and returns the value at the end of the list.
	 * @return
	 */
	public int pop() {
		return remove();
	}
	
	/**
	 * The number of ints in this list.
	 * @return
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Returns true if there are no ints in this list.
	 * @return
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	
	/**
	 * Resizes the array to be the perfect size for this list. Saves on memory
	 * if large amounts of values are stored, and then removed.
	 */
	public void condense() {
		if (size - 1 == this.data.length) return;
		int[] vals = new int[size];
		for (int i = 0; i < this.data.length; i++) {
			vals[i] = this.data[i];
		}
		this.data = vals;
	}
	
	/**
	 * Checks our size, and if we're full, it doubles the list size of the array
	 * and copies old values.
	 */
	private void checkSize() {
		if (size >= this.data.length) { // We're full
			// The new values list
			int[] vals = new int[data.length * 2];
			// Copy the old values
			for (int i = 0; i < data.length; i++) {
				vals[i] = this.data[i];
			}
			this.data = vals;
		}
	}
	
	public int get(int i) {
		return data[i];
	}
	
	public void set(int index, int n) {
		data[index] = n;
	}
}