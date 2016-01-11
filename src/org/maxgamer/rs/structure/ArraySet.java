package org.maxgamer.rs.structure;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>
 * This class is meant to efficiently handle unique elements of a set. Time to
 * add and remove elements compared to a regular {@link java.util.HashSet} is
 * much faster and less memory consuming. This class is suggested when an
 * unorganized set of unique elements with faster modification and lower memory
 * consumption is wanted.
 * <p>
 * {@code FastHashSet} will place in an unorganized manor for the indicies,
 * meaning all indicies are not sorted by the hash code of the object. Which is
 * unlike {@link java.util.HashSet}.
 * 
 * @author Albert Beaupre
 * 
 * @param <E>
 *            The element type to fill this set will be filled with
 */
@SuppressWarnings("unchecked")
public class ArraySet<E> implements Set<E> {

	private E[] elements;

	/**
	 * Constructs a new, empty {@code FastHashSet} with a specified
	 * {@code intialCapacity}.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the set
	 */
	public ArraySet(int initialCapacity) {
		this.elements = (E[]) new Object[initialCapacity];
	}

	/**
	 * Constructs a new {@code FastHashSet} filled with the specified
	 * {@code Collection} of elements.
	 * 
	 * @param c
	 *            the collect of elements to fill this set with
	 */
	public ArraySet(Collection<? extends E> c) {
		addAll(c);
	}

	public ArraySet(E... arr) {
		for (E e : arr)
			add(e);
	}

	/**
	 * Constructs a new, empty {@code FastHashSet} with a default initial
	 * capacity of 0.
	 */
	public ArraySet() {
		this(0);
	}

	@Override
	public int size() {
		return elements.length;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		return indexForHash(o.hashCode()) != -1;
	}

	@Override
	public Iterator<E> iterator() {
		return Arrays.asList(elements).iterator();
	}

	@Override
	public Object[] toArray() {
		return elements.clone();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size())
			return (T[]) Arrays.copyOf(elements, size(), a.getClass());
		System.arraycopy(elements, 0, a, 0, size());
		if (a.length > size())
			a[size()] = null;
		return a;
	}

	@Override
	public boolean add(E e) {
		int index = indexForHash(e); // retrieves the index of the hash for the
										// specified element
		if (index != -1) {
			elements[index] = e; // replace the old element with the new element
			return false;
		} else {
			/**
			 * adds a new element if an old element with a similar hash code is
			 * not present
			 */
			E[] result = Arrays.copyOf(elements, elements.length + 1);
			result[result.length - 1] = e;
			elements = result;
			return true;
		}
	}

	@Override
	public boolean remove(Object o) {
		int index = indexForHash(o);
		if (index != -1) {
			/**
			 * removes the element index from the array if the element is
			 * present
			 */
			E[] n = (E[]) Array.newInstance(elements[0].getClass(),
					elements.length - 1);
			System.arraycopy(elements, 0, n, 0, index);
			System.arraycopy(elements, index + 1, n, index, elements.length
					- index - 1);
			elements = n;
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Iterator<?> i = c.iterator(); i.hasNext();)
			if (!contains(i.next()))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (Iterator<?> i = c.iterator(); i.hasNext();)
			if (add((E) i.next()))
				changed = true;
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		for (Iterator<?> i = c.iterator(); i.hasNext();) {
			Object o = i.next();
			if (!c.contains(o)) {
				remove(o);
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Iterator<?> i = c.iterator(); i.hasNext();)
			if (remove(i.next()))
				changed = true;
		return changed;
	}

	@Override
	public void clear() {
		elements = (E[]) new Object[0];
	}

	private int indexForHash(Object o) {
		for (int i = 0; i < elements.length; i++) {
			E e = elements[i];
			if (e.hashCode() == o.hashCode())
				return i;
		}
		return -1;
	}
}
