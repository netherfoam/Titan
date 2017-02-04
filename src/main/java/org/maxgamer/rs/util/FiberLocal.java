package org.maxgamer.rs.util;

import co.paralleluniverse.fibers.Fiber;

import java.util.WeakHashMap;

/**
 * @author netherfoam
 */
public class FiberLocal<T> {
    private WeakHashMap<Fiber, T> properties = new WeakHashMap<>();

    private static Fiber<?> getFiber() {
        Fiber f = Fiber.currentFiber();
        if (f == null) throw new IllegalStateException("Not a fiber");

        return f;
    }

    public synchronized T get() {
        return properties.get(getFiber());
    }

    public T set(T value) {
        return set(getFiber(), value);
    }

    public synchronized void remove() {
        remove(getFiber());
    }

    public synchronized void remove(Fiber<?> fiber) {
        properties.remove(fiber);
    }

    public synchronized T set(Fiber<?> fiber, T value) {
        if (fiber == null) throw new NullPointerException("Fiber may not be null");

        return properties.put(fiber, value);
    }
}
