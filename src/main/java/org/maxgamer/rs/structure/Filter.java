package org.maxgamer.rs.structure;

public interface Filter<T> {
    boolean accept(T t);
}
