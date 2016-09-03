package org.maxgamer.rs.model.entity;

import org.maxgamer.rs.core.server.WorldFullException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents a list of entities, each of which have a unique ID.
 *
 * @param <E> the type of entity to store.
 * @author netherfoam
 */
public class EntityList<E extends Entity> implements Iterable<E> {
    /**
     * We do a circular lookup when finding free positions, this is the next
     * position we should search.
     */
    private int nextPos = 0; //Next position to search.

    /**
     * The array of entities
     */
    private E[] entities;

    /**
     * The number of entities which are not null
     */
    private int count = 0;

    /**
     * Constructs a new EntityList with the given maximum number of entities.
     * This is not a resizable list.
     *
     * @param max the max number of entities.
     */
    @SuppressWarnings("unchecked")
    public EntityList(int max) {
        this.entities = (E[]) new Entity[max];
    }

    /**
     * Returns the maximum number of entities storable here.
     *
     * @return the max number of entities
     */
    public int getMax() {
        return entities.length;
    }

    /**
     * Finds a free slot in this entity list. This is not threadsafe.
     *
     * @return a free slot or -1 if no free slots.
     */
    protected int freeSlot() {
        for (int i = nextPos; i < entities.length; i++) {
            if (entities[i] == null) {
                nextPos = i;
                return i;
            }
        }
        for (int i = 0; i < nextPos; i++) {
            if (entities[i] == null) {
                nextPos = i;
                return i;
            }
        }
        return -1; //No free slot.
    }

    /**
     * Adds the given entity to this list, and returns the index for the given
     * entity. This is threadsafe. It returns -1 if there is no space.
     *
     * @param e the entity to add
     * @return the index of the entity
     */
    public int add(E e) throws WorldFullException {
        synchronized (this) {
            int slot = freeSlot();
            if (slot == -1) {
                throw new WorldFullException();
            }

            this.set(slot, e);
            return slot;
        }
    }

    /**
     * Removes the entity from this list at the given index
     *
     * @param slot the slot to remove the entity from
     * @return true if success, false if it was null
     */
    public boolean remove(int slot) {
        //This is threadsafe because we don't lookup.
        if (set(slot, null) == null) {
            return false; //We didn't change anything
        }

        return true; //We removed an entity
    }

    /**
     * Sets the given index to the given entity. The entity may be null to
     * remove an existing entity. This is not thread-safe.
     *
     * @param slot the slot
     * @param e    the previous entity
     */
    protected E set(int slot, E e) {
        if (entities[slot] != null && e != null) {
            //We're replacing a valid entity with another entity. Probably should not do this.
            throw new RuntimeException("Entity ID appears to be in use. Bad ID given: " + slot);
        }

        if (entities[slot] != null) count--;
        if (e != null) count++;

        E old = entities[slot];
        entities[slot] = e;
        return old;
    }

    /**
     * Returns the number of entities stored in this list
     *
     * @return the number of entities. 0 <= count <= max
     */
    public int getCount() {
        return count;
    }

    /**
     * Fetches the entity at the given index position
     *
     * @param index the index
     * @return the entity or null
     */
    public E get(int index) {
        return entities[index];
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int next = 0;

            @Override
            public boolean hasNext() {
                if (next >= entities.length) return false;

                while (entities[next] == null) {
                    next++;
                    if (next >= entities.length) return false;
                }
                return true;
            }

            @Override
            public E next() {
                while (entities[next] == null) {
                    next++;
                    if (next >= entities.length) throw new NoSuchElementException("No more entities.");
                }
                return entities[next++];
            }

            @Override
            public void remove() {
                EntityList.this.remove(next - 1); //next-1 because the next() call increments next by 1
            }
        };
    }
}