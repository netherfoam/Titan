package org.maxgamer.rs.model.entity;

public interface Interactable {
    public abstract boolean hasOption(String option);

    public abstract String[] getOptions();

    public abstract String getName();

    public abstract int getId();
}