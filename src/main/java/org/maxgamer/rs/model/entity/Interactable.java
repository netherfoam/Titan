package org.maxgamer.rs.model.entity;

public interface Interactable {
    boolean hasOption(String option);

    String[] getOptions();

    String getName();

    int getId();
}