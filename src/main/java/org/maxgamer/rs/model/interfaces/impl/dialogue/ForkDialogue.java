package org.maxgamer.rs.model.interfaces.impl.dialogue;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

import java.util.ArrayList;

/**
 * @author netherfoam
 */
public abstract class ForkDialogue extends Dialogue {
    public static final int MAX_OPTIONS = 5;

    private ArrayList<String> options = new ArrayList<>();
    private String title;

    public ForkDialogue(Player p) {
        super(p);
    }

    public ForkDialogue(Player p, String title, String... options) {
        this(p);

        this.setTitle(title);
        for(String s : options) {
            this.add(s);
        }
    }

    /**
     * Adds the given option number to this ForkDialogue
     *
     * @param text the option to add, eg "option 1", "option 2"
     */
    public void add(String text) {
        if (this.isOpen()) {
            throw new IllegalStateException("Interface cannot have options added after opening");
        }
        if (this.options.size() >= MAX_OPTIONS) {
            throw new IllegalStateException("Interface may only have up to 5 options available");
        }
        this.options.add(text);

        setChildId(225 + options.size() * 2);
    }

    /**
     * Returns the name of this fork dialogue. This appears at the top of the
     * dialogue box.
     *
     * @return the name of this fork dialogue. This appears at the top of the
     * dialogue box.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the name of this fork dialogue. This appears at the top of the
     * dialogue box.
     *
     * @param title the name of this fork dialogue. This appears at the top of the
     *              dialogue box.
     */
    public void setTitle(String title) {
        this.title = title;

        if (isOpen() && title != null) {
            setString(1, title);
        }
    }

    /**
     * Removes the given option from this ForkDialogue
     *
     * @param text the option to remove
     */
    public void remove(String text) {
        if (this.isOpen()) {
            throw new IllegalStateException("Interface cannot have options added after opening");
        }

        this.options.remove(text);
        setChildId(225 + options.size() * 2);
    }

    /**
     * Fetches the option at the given position
     *
     * @param pos the position
     * @return the option
     * @throws IndexOutOfBoundsException if the given position is not available
     */
    public String get(int pos) {
        return options.get(pos);
    }

    @Override
    public void onOpen() {
        int size = this.options.size();
        if (size < 2 || size > MAX_OPTIONS) {
            throw new IllegalArgumentException("Options length must be between 2 and 5 inclusive. Given " + size + " options.");
        }

        for (int i = 0; i < options.size(); i++) {
            setString(2 + i, options.get(i));
        }

        if (title != null)
            setString(1, title);
    }

    @Override
    public boolean isMobile() {
        return false;
    }

    @Override
    public final void onClick(int option, int buttonId, int slotId, int itemId) {
        getPlayer().getWindow().close(this);
        onSelect(buttonId - 2);
    }

    /**
     * Invoked when this ForkDialogue is responded to by the player.
     *
     * @param option the option the player clicked
     */
    public abstract void onSelect(int option);
}