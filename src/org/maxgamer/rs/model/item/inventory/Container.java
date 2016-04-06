package org.maxgamer.rs.model.item.inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import org.maxgamer.rs.lib.IntList;
import org.maxgamer.rs.lib.log.Log;
import org.maxgamer.rs.model.item.ItemDefinition;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.structure.YMLSerializable;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * @author Netherfoam
 */
public abstract class Container implements Cloneable, Iterable<ItemStack>, YMLSerializable {
	/**
	 * The stack type - Some containers (Banks) force all items to stack. Others
	 * force items to never stack
	 */
	private StackType stack;
	
	/**
	 * The ContainerListeners which want to be notified when something changes
	 * in this container.
	 */
	private LinkedList<ContainerListener> listeners = new LinkedList<ContainerListener>();
	
	/**
	 * Modification container. Incremented every time a modification is made to this container's contents.
	 * This is used to prevent concurrent modification by ContainerState's. Eg like Java Collections does.
	 */
	protected int modCount = 0;
	
	/**
	 * Constructors & Item Stacking
	 */
	
	/**
	 * Constructs a new Container which will try to stack items normally.
	 */
	public Container() {
		this(StackType.NORMAL);
	}
	
	/**
	 * Constructs a new container
	 * 
	 * @param stack The method of stacking this container will use.
	 */
	public Container(StackType stack) {
		if (stack == null) {
			throw new NullPointerException("StackType may not be null.");
		}
		this.stack = stack;
	}
	
	/**
	 * The method of stacking this container uses.
	 * 
	 * @return The stack type.
	 */
	public final StackType getStackType() {
		return stack;
	}
	
	/**
	 * Abstract methods
	 */
	
	/**
	 * Should set the item at the given slot to the given item. This should
	 * remove any items that used to be in the slot. Also, it should update any
	 * inventories (such as player inventories)
	 * 
	 * @param nextThreadId The item to set
	 * @param slot The slot to put the item.
	 */
	protected abstract void setItem(int slot, ItemStack item);
	
	/**
	 * Fetches the item at the given slot. This method should never return null.
	 * To return 'NO ITEM', you should return ItemStack.EMPTY_ITEM.
	 * 
	 * @param slot The slot to get the item from, guaranteed to 0 >= slot >=
	 *        this.getSize().
	 * @return The ItemStack at that slot, NEVER Null.
	 */
	public abstract ItemStack get(int slot);
	
	/**
	 * Alias of get()
	 * @param slot the slot
	 * @return the ItemStack at that slot, NEVER null
	 */
	public ItemStack getItem(int slot) {
		return get(slot);
	}
	
	/**
	 * Returns the size of this Container. Should be positive.
	 * 
	 * @return The size of this container.
	 */
	public abstract int getSize();
	
	@Override
	public ConfigSection serialize() {
		ConfigSection s = new ConfigSection();
		
		int size = getSize();
		s.set("size", size);
		
		ConfigSection t = new ConfigSection();
		
		for (int i = 0; i < size; i++) {
			ItemStack item = get(i);
			if (item == null) continue; // Don't set null items
			else {
				t.set(String.valueOf(i), item.serialize());
			}
		}
		
		s.set("contents", t);
		
		return s;
	}
	
	@Override
	public void deserialize(ConfigSection s) {
		int size = s.getInt("size");
		
		s = s.getSection("contents");
		
		for (int i = 0; i < size; i++) {
			ConfigSection t = s.getSection(String.valueOf(i), null);
			if (t == null) continue; // Null item
			
			ItemStack item = ItemStack.create(t);
			this.set(i, item);
		}
	}
	
	/**
	 * Returns a new ContainerState object which represents this container.
	 * Modifying the container state will not update this container, until the
	 * ContainerState's apply() method is called. This, the creating container,
	 * then receives all updates. The system allows for multi-part modifications
	 * to start, fail if necessary, cleanup and cancel properly instead of
	 * failing and leaving the inventory state in a mess.
	 * 
	 * @return a new state object
	 */
	public ContainerState getState() {
		return new ContainerState(this);
	}
	
	/**
	 * Adds the given ContainerListener so that it will receive updates when
	 * this container changes states.
	 * 
	 * @param l the listener to add
	 * @throws NullPointerException if the given listener is null
	 */
	public void addListener(ContainerListener l) {
		if (l == null) {
			throw new NullPointerException("ContainerListener may not be null");
		}
		this.listeners.add(l);
	}
	
	/**
	 * Removes the given ContainerListener so that it will no longer receive
	 * updates when this container changes states.
	 * 
	 * @param l the container listener to remove
	 */
	public void removeListener(ContainerListener l) {
		this.listeners.remove(l);
	}
	
	public boolean hasListener(ContainerListener l) {
		return this.listeners.contains(l);
	}
	
	/**
	 * Sets the item at the given slot to the given item. This should be used
	 * instead of setItem (which is protected), due to the transaction API which
	 * may record this edit. If you use SetItem, it will be unreversable which
	 * is an undesireable effect.
	 * 
	 * @param slot The slot to set
	 * @param item The itemstack to put there.
	 */
	public synchronized void set(int slot, ItemStack item) {
		ItemStack old = this.get(slot);
		
		if (old == item) {
			return; // No change.
		}
		
		this.setItem(slot, item);
		modCount++;
		
		for (ContainerListener l : listeners) {
			try {
				l.onSet(this, slot, old);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Methods which add/remove items.
	 */
	
	public final synchronized boolean transferTo(Container to, ItemStack item) {
		return this.transferTo(to, new ItemStack[] { item });
	}
	
	/**
	 * Transfers the given items from this container to the given container.
	 * 
	 * @param to The target container
	 * @param items The items to transfer to the target container.
	 * @return True for success, false for failure. If false, both inventories
	 *         shall remain unmodified.
	 */
	public final synchronized boolean transferTo(Container to, ItemStack... items) {
		// It's much faster to do it with two proto sets, than check, remove,
		// add, fail, re-add - Even if code is shorter!
		ItemStack[] proto1 = this.getItems();
		IntList updates1 = new IntList(items.length);
		for (ItemStack iStack : items) {
			if (Container.remove(iStack, proto1, updates1, -1) == false) {
				return false; // Failed to remove it.
			}
		}
		
		ItemStack[] proto2 = to.getItems();
		IntList updates2 = new IntList(items.length);
		for (ItemStack iStack : items) {
			if (Container.add(iStack, proto2, to.getStackType(), updates2, -1) == false) {
				return false; // Failed to add it.
			}
		}
		
		while (updates1.isEmpty() == false) {
			int slot = updates1.pop();
			this.set(slot, proto1[slot]);
		}
		while (updates2.isEmpty() == false) {
			int slot = updates2.pop();
			to.set(slot, proto2[slot]);
		}
		return true;
	}
	
	public final synchronized void add(int preferredSlot, ItemStack item) throws ContainerException {
		this.add(preferredSlot, new ItemStack[] { item });
	}
	
	/**
	 * Adds the given list of items to this container.
	 * 
	 * @param l The list to add
	 * @return true for success, false if not enough room. If false is returned,
	 *         this inventory will remain unmodified.
	 */
	public final synchronized void add(int preferredSlot, ItemStack... list) throws ContainerException {
		if (list == null) {
			throw new IllegalArgumentException("May not add a null item[] to an inventory.");
		}
		
		ItemStack[] proto = this.getItems().clone();
		IntList updates = new IntList(list.length);
		
		for (int a = 0; a < list.length; a++) {
			ItemStack add = list[a];
			if (Container.add(add, proto, getStackType(), updates, preferredSlot) == false) {
				throw new ContainerException();
			}
		}
		
		// Now we change the actual inventory.
		while (updates.isEmpty() == false) {
			int slot = updates.pop();
			this.set(slot, proto[slot]);
		}
	}
	
	public final synchronized void add(ItemStack item) throws ContainerException {
		this.add(new ItemStack[] { item });
	}
	
	public final synchronized void add(ItemStack... list) throws ContainerException {
		this.add(-1, list);
	}
	
	/**
	 * Adds the contents of the given container to this container. Does not
	 * modify the supplied containers contents.
	 * 
	 * @param c The container to copy items from
	 * @return True for success false for failure. If returning false, this
	 *         inventory will not be modified.
	 */
	public final void addAll(Container c) throws ContainerException {
		if (c == null) {
			throw new IllegalArgumentException("May not add a null container to an inventory.");
		}
		ItemStack[] items = new ItemStack[c.getTakenSlots()];
		int slot = 0;
		for (int i = 0; i < c.getSize(); i++) {
			if (c.get(i) != null) {
				items[slot++] = c.get(i);
			}
		}
		
		this.add(items);
	}
	
	/**
	 * Removes the contents of the given container from this container. Does not
	 * modify the supplied containers contents.
	 * 
	 * @param c The container with a list of items to remove.
	 * @return True for success false for failure. If returning false, this
	 *         inventory will not be modified.
	 */
	public final void removeAll(Container c) throws ContainerException {
		if (c == null) {
			throw new IllegalArgumentException("May not remove a null container to an inventory.");
		}
		ItemStack[] items = new ItemStack[c.getTakenSlots()];
		int slot = 0;
		for (int i = 0; i < c.getSize(); i++) {
			if (c.get(i) != null) {
				items[slot++] = c.get(i);
			}
		}
		
		this.remove(-1, items);
	}
	
	public final synchronized void remove(int preferredSlot, ItemStack item) throws ContainerException {
		this.remove(preferredSlot, new ItemStack[] { item });
	}
	
	/**
	 * Removes the given items from the players inventory.
	 * 
	 * @param list The list of items to remove
	 * @return True for success, false for failure. On failure, no items will be
	 *         modified.
	 */
	public final synchronized void remove(int preferredSlot, ItemStack... list) throws ContainerException {
		if (list == null) {
			throw new IllegalArgumentException("May not add a null item[] to an inventory.");
		}
		IntList updates = new IntList(list.length);
		ItemStack[] proto = this.getItems();
		for (int i = 0; i < list.length; i++) {
			ItemStack item = list[i];
			if (Container.remove(item, proto, updates, preferredSlot) == false) {
				throw new ContainerException();
			}
		}
		
		while (updates.isEmpty() == false) {
			int slot = updates.pop();
			this.set(slot, proto[slot]);
		}
	}
	
	public final synchronized void remove(ItemStack item) throws ContainerException {
		this.remove(new ItemStack[] { item });
	}
	
	public final synchronized void remove(ItemStack... list) throws ContainerException {
		this.remove(-1, list);
	}
	
	/**
	 * Removes all of the items which have the same ID as the supplied one.
	 * 
	 * @param iStack The item to search for and delete all of.
	 * @return The number of items deleted.
	 */
	public final int removeAll(ItemStack iStack) {
		if (iStack == null) {
			throw new IllegalArgumentException("May not remove a null item to an inventory.");
		}
		
		int n = 0;
		for (int i = 0; i < this.getSize(); i++) {
			ItemStack item = this.get(i);
			if (item != null && item.matches(iStack)) {
				n += item.getAmount();
				this.set(i, null);
			}
		}
		return n;
	}
	
	/**
	 * Deletes all items in this inventory.
	 */
	public final synchronized void clear() {
		for (int slot = this.getSize() - 1; slot >= 0; slot--) {
			ItemStack it = this.get(slot);
			if (it != null) {
				this.set(slot, null);
			}
		}
	}
	
	/**
	 * Methods which rearrange the inventory without adding/removing
	 */
	
	/**
	 * Shuffles all items to the start of the container.
	 */
	public void shift() {
		// The slot to put the next item into.
		int slot = 0;
		int i;
		
		// Find the first empty spot.
		for (i = 0; i < getSize(); i++) {
			if (get(i) == null) {
				slot = i;
				break;
			}
		}
		
		// Starting at the first empty slot, we want to move all items back
		// down.
		for (i = slot; i < this.getSize(); i++) {
			if (this.get(i) == null) {
				continue;
			}
			this.set(slot++, this.get(i));
		}
		
		// All of the items above 'slot' have now been moved, so set them all to null
		for (i = slot; i < this.getSize(); i++) {
			this.set(i, null);
		}
	}
	
	/**
	 * Inserts the given item at the given location in the inventory.
	 * 
	 * @param slot The slot to change the item at.
	 * @param item The item to place at the slot.
	 * @param reverse True if you want to shuffle items up to make space. False
	 *        if you want to shuffle them down to make space.
	 * @throws IllegalArgumentException If the item will not fit (AKA, shuffle
	 *         in that direction)
	 */
	public final void insert(int slot, ItemStack item, boolean reverse) {
		// This is the first free spot, after or before (reverse applies) the
		// given slot.
		int freeSpot = -1;
		
		if (reverse == false) {
			slot++;
			for (int i = slot; i >= 0; i--) {
				if (this.get(i) == null) {
					freeSpot = i;
					break;
				}
			}
			
			if (freeSpot < 0) {
				throw new IllegalArgumentException("May not insert there (There is no space behind the item to shuffle!)");
			}
			
			for (int i = freeSpot; i < slot; i++) {
				this.set(i, this.get(i + 1));
			}
			this.set(slot, item);
		}
		else {
			for (int i = slot; i < this.getSize(); i++) {
				if (this.get(i) == null) {
					freeSpot = i;
					break;
				}
			}
			
			if (freeSpot >= this.getSize()) {
				throw new IllegalArgumentException("May not insert there (There is no space after the item to shuffle!)");
			}
			
			for (int i = freeSpot; i > slot; i--) {
				this.set(i, this.get(i - 1));
			}
			this.set(slot, item);
		}
	}
	
	/**
	 * Inserts the item at slotFrom into the slot at slotTo.
	 * 
	 * @param slotFrom The item to insert from
	 * @param slotTo The item to insert to.
	 */
	public final void insert(int slotFrom, int slotTo) {
		boolean reverse;
		if (slotFrom > slotTo) {
			reverse = true;
		}
		else {
			reverse = false;
		}
		
		ItemStack item = this.get(slotFrom);
		// Delete it from it's old position.
		this.set(slotFrom, null);
		
		// Insert the item
		this.insert(slotTo, item, reverse);
		
	}
	
	/**
	 * Methods which lookup info on the container
	 */
	
	/**
	 * Fetches all items and converts them to an array. You may safely modify
	 * this array without modifying the contents.
	 * 
	 * @return An array representing the items.
	 */
	public final synchronized ItemStack[] getItems() {
		ItemStack[] items = new ItemStack[getSize()];
		for (int i = 0; i < items.length; i++) {
			items[i] = get(i);
		}
		return items;
	}
	
	/**
	 * Fetches an array of all the items in this players inventory. This is
	 * guaranteed not to have null items in the array, but the array indexes
	 * (slot numbers) will be wrong.
	 * 
	 * @return The contents, minus null items.
	 */
	public final synchronized ItemStack[] getContents() {
		ItemStack[] data = new ItemStack[getTakenSlots()];
		int offset = 0;
		for (int i = 0; i < this.getSize(); i++) {
			ItemStack iStack = this.get(i);
			if (iStack != null) {
				data[offset++] = iStack;
			}
		}
		return data;
	}
	
	public final synchronized boolean containsAll(ItemStack item) {
		return this.containsAll(new ItemStack[] { item });
	}
	
	/**
	 * Checks that this Container contains all the given items (Including their
	 * amounts).
	 * 
	 * @param needles The items to check it contains.
	 * @return True if it does, false if it doesn't.
	 */
	public final synchronized boolean containsAll(ItemStack... needles) {
		if (needles == null) {
			throw new IllegalArgumentException("May not search a container for a null item[].");
		}
		IntList updates = new IntList(needles.length); // Not actually used.
		
		ItemStack[] proto = this.getItems();
		for (int i = 0; i < needles.length; i++) {
			ItemStack needle = needles[i];
			
			if (Container.remove(needle, proto, updates, -1) == false) {
				return false; // Failed to remove, does not contain enough.
			}
		}
		
		return true;
	}
	
	/**
	 * Returns the number of filled slots.
	 * 
	 * @return The number of filled slots.
	 */
	public final synchronized int getTakenSlots() {
		int taken = 0;
		for (int i = 0; i < getSize(); i++) {
			ItemStack item = this.get(i);
			if (item != null && item.getId() >= 0) {
				taken++;
			}
		}
		return taken;
	}
	
	/**
	 * Returns the number of items with a matching ID in this inventory.
	 * 
	 * @param item The item to search with
	 * @return The number of items.
	 */
	public final synchronized int getNumberOf(ItemStack item) {
		if (item == null) {
			throw new IllegalArgumentException("May not search a container for a null item.");
		}
		long n = 0;
		for (ItemStack i : getItems()) {
			if (i != null && i.matches(item)) {
				n += i.getAmount();
			}
		}
		return (int) Math.min(n, Integer.MAX_VALUE);
	}
	
	public final synchronized long getSpaceFor(ItemStack item) {
		if (item == null) {
			throw new IllegalArgumentException("May not search a container for a null item.");
		}
		long space = 0;
		
		if ((item.getStackSize() > 1 && this.getStackType() == StackType.NORMAL) || this.getStackType() == StackType.ALWAYS) {
			for (ItemStack iStack : this) {
				if (iStack == null) {
					space += item.getStackSize();
				}
				else if (item.matches(iStack)) {
					space += (item.getStackSize() - iStack.getAmount());
				}
			}
		}
		else {
			for (ItemStack iStack : this) {
				if (iStack == null) {
					space++;
				}
			}
		}
		
		return space;
	}
	
	public final synchronized boolean hasRoom(ItemStack item) {
		return this.hasRoom(new ItemStack[] { item });
	}
	
	/**
	 * Returns true if this container has room for the given items
	 * 
	 * @param list The items to add
	 * @return True if the container has room, false otherwise.
	 */
	public final synchronized boolean hasRoom(ItemStack... list) {
		if (list == null) {
			throw new IllegalArgumentException("May not check room for a null item[].");
		}
		
		ItemStack[] proto = this.getItems();
		IntList updates = new IntList(list.length);
		
		for (ItemStack item : list) {
			if (Container.add(item, proto, getStackType(), updates, -1) == false) {
				Log.debug("Failed to remove " + item + " from " + this + " for hasRoom() check");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Finds a free slot for this container (Ie, one that is empty.)
	 * 
	 * @return The slot, or -1 if none found.
	 */
	public final synchronized int getFreeSlot() {
		for (int i = 0; i < this.getSize(); i++) {
			ItemStack iStack = this.get(i);
			if (iStack == null || iStack.getId() < 0) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Shorthand method to check if this Container has room for the given items.
	 * 
	 * @param c The container to check to add items from.
	 * @return True for yes, false if it cannot fit all the items.
	 */
	public final synchronized boolean hasSpaceFor(Container c) {
		if (c == null) {
			throw new IllegalArgumentException("May not add a null container to another container.");
		}
		
		ItemStack[] items = new ItemStack[c.getTakenSlots()];
		int slot = 0;
		for (int i = 0; i < c.getSize(); i++) {
			if (c.get(i) != null) {
				items[slot++] = c.get(i);
			}
		}
		
		return this.hasRoom(items);
	}
	
	/**
	 * Returns true if this contains the given search.
	 * 
	 * @param needle The Item to search for. The quantity of this item is used.
	 * @return True if this inventory has the item, false otherwise.
	 */
	public final synchronized boolean contains(ItemStack needle) {
		if (needle == null) {
			throw new IllegalArgumentException("May not search a container for a null item.");
		}
		long n = needle.getAmount();
		
		for (ItemStack i : this.getItems()) {
			if (i != null && i.matches(needle)) {
				n -= i.getAmount();
				if (n <= 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the number of empty slots, the opposite of getTakenSlots().
	 * 
	 * @return The number of empty slots.
	 */
	public final synchronized int getFreeSlots() {
		int n = 0;
		for (int i = 0; i < this.getSize(); i++) {
			ItemStack iStack = this.get(i);
			if (iStack == null || iStack.getId() < 0) {
				n++;
			}
		}
		return n;
	}
	
	/**
	 * Fetches the first slot where the given item is seen.
	 * 
	 * @param item The item to search for, only the ID is used
	 * @return The slot, or -1 if no matching item is found.
	 */
	public final synchronized int getSlotOf(ItemStack item) {
		if (item == null) {
			throw new IllegalArgumentException("May not search a container for a null item.");
		}
		return getSlotOf(item, this.getItems());
	}
	
	/**
	 * Returns true if the inventory contains the given Item ID of at least the
	 * given amount
	 * 
	 * @param id The ID to search for
	 * @param amt The minimum amount
	 * @return True if >= amt of the given items were found.
	 */
	public synchronized final boolean contains(int id, long amt) {
		if (id < 0) {
			throw new IllegalArgumentException("May not search a container for a null item.");
		}
		long maxSize = ItemDefinition.getDefinition(id).getMaxStack();
		
		int numStacks = (int) (amt / maxSize);
		if (amt % maxSize > 0) numStacks++; // Leftovers stack.
		
		ItemStack[] list = new ItemStack[numStacks];
		for (int i = 0; i < numStacks; i++) {
			list[i] = ItemStack.create(id, (int) (Math.min(amt, maxSize)));
			amt -= list[i].getAmount();
		}
		
		return this.containsAll(list);
	}
	
	public final synchronized boolean containsAny(ItemStack item) {
		return this.containsAny(new ItemStack[] { item });
	}
	
	public synchronized final boolean containsAny(ItemStack... items) {
		for (ItemStack item : items)
			if (item != null && contains(item)) return true;
		return false;
	}
	
	public synchronized final void sort(Comparator<ItemStack> comparator) {
		this.shift();
		int size = this.getTakenSlots();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (comparator.compare(this.get(i), this.get(j)) < 0) {
					// Swap I and J around
					ItemStack tmp = this.get(j);
					this.set(j, this.get(i));
					this.set(i, tmp);
				}
			}
		}
	}
	
	/**
	 * Returns true if this inventory contains nothing.
	 * 
	 * @return True if this inventory contains nothing.
	 */
	public final synchronized boolean isEmpty() {
		for (int i = this.getSize() - 1; i >= 0; i--) {
			if (this.get(i) != null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if this inventory has at least one completely empty slot
	 * 
	 * @return True if this inventory has at least one completely empty slot
	 */
	public final synchronized boolean isFull() {
		for (int i = this.getSize() - 1; i >= 0; i--) {
			if (this.get(i) == null) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * The total grand exchange value for this container.
	 * @return The total grand exchange value for this container.
	 */
	public final int value(){
		int worth = 0;
		for(ItemStack item : this){
			if(item == null) continue;
			worth += item.getDefinition().getValue();
		}
		return worth;
	}
	
	/**
	 * Static methods for containers
	 */
	
	/**
	 * Fetches the first slot where the given item is seen.
	 * 
	 * @param item The item to search for, only the ID is used
	 * @param items The items to search through
	 * @return The slot, or -1 if no matching item is found.
	 */
	private static int getSlotOf(ItemStack item, ItemStack[] items) {
		int i;
		for (i = 0; i < items.length; i++) {
			if (items[i] != null && items[i].matches(item)) {
				return i;
			}
		}
		return -1;
	}
	
	private static boolean add(ItemStack add, ItemStack[] proto, StackType type, IntList updates, int preferredSlot) {
		if (add == null) {
			throw new IllegalArgumentException("May not add a null item to an inventory.");
		}
		
		long remaining = add.getAmount();
		
		// Step 1: Find an existing stack, if possible.
		for (int i = (preferredSlot >= 0 ? -1 : 0); i < proto.length; i++) {
			int index = i;
			if (i == -1) index = preferredSlot;
			
			ItemStack stack = proto[index];
			if (stack == null) continue; // Skip over empty spaces 'til later.
			
			if (stack.matches(add) && (stack.getAmount() < stack.getStackSize() || type == StackType.ALWAYS)) {
				if (stack.getAmount() + remaining > stack.getStackSize() && type != StackType.ALWAYS) {
					// We cannot fit all of the remaining items onto this stack.
					remaining -= stack.getStackSize() - stack.getAmount();
					proto[index] = stack.setAmount(stack.getStackSize());
					updates.add(index);
				}
				else {
					// We can fit all of the remaining items here.
					proto[index] = stack.setAmount(stack.getAmount() + remaining);
					remaining = 0;
					updates.add(index);
					return true;
				}
			}
		}
		
		// Step 2: No existing stacks are available, place in empty slots.
		for (int i = (preferredSlot >= 0 ? -1 : 0); i < proto.length; i++) {
			int index = i;
			if (i == -1) index = preferredSlot;
			
			ItemStack stack = proto[index];
			if (stack != null) continue; // We've already checked filled spaces.
			
			if (remaining > add.getStackSize() && type != StackType.ALWAYS) {
				// We cannot fit all of the remaining items in this slot
				remaining -= add.getStackSize();
				proto[index] = add.setAmount(add.getStackSize());
				updates.add(index);
			}
			else {
				// We can fit all remaining items here
				proto[index] = add.setAmount(remaining);
				remaining = 0;
				updates.add(index);
				return true;
			}
		}
		
		// We haven't got the space.
		return false;
	}
	
	private static boolean remove(ItemStack item, ItemStack[] proto, IntList updates, int preferredSlot) {
		if (item == null) {
			throw new NullPointerException("May not remove a null itemstack!");
		}
		
		long remaining = item.getAmount();
		
		for (int i = (preferredSlot >= 0 ? -1 : 0); i < proto.length; i++) {
			int index = i;
			if (i == -1) index = preferredSlot;
			
			ItemStack stack = proto[index];
			if (stack == null) continue; // No items here.
			
			if (stack.matches(item)) {
				if (remaining > stack.getAmount()) {
					proto[index] = null;
					remaining -= stack.getAmount();
					updates.add(index);
				}
				else {
					proto[index] = stack.setAmount(stack.getAmount() - remaining);
					remaining = 0;
					updates.add(index);
					return true;
				}
			}
		}
		
		// We haven't got enough of those.
		return false;
	}
	
	/**
	 * Iterates through all slots in the container, including slots which have
	 * null items.
	 */
	public final Iterator<ItemStack> iterator() {
		return new Iterator<ItemStack>() {
			int pos = 0;
			
			@Override
			public boolean hasNext() {
				return pos < getSize();
			}
			
			@Override
			public ItemStack next() {
				return get(pos++);
			}
			
			@Override
			public void remove() {
				throw new RuntimeException("May not remove elements from a container through an iterator.");
			}
		};
	}
	
	/**
	 * Returns a new list which contains the contents of this container. This
	 * list will not contain any null items.
	 * @return a list of items in this container.
	 */
	public ArrayList<ItemStack> toList() {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>(this.getSize());
		for (ItemStack item : this.getContents()) {
			list.add(item);
		}
		return list;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName() + ": {");
		for (int i = 0; i < getSize(); i++) {
			sb.append("[" + i + "]: " + get(i) + "\n");
		}
		sb.append("}");
		return sb.toString();
	}
}
