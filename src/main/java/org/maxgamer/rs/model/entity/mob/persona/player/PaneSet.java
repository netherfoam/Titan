package org.maxgamer.rs.model.entity.mob.persona.player;

import org.maxgamer.rs.model.interfaces.Pane;
import org.maxgamer.rs.model.interfaces.Window;

import java.util.ArrayList;

/**
 * Represents all of the currently active panes that the owner of this PaneSet
 * has open.
 *
 * @author netherfoam
 */
public class PaneSet {
    /**
     * The owner of this PaneSet
     */
    private final Player player;

    /**
     * The currently open panes, where the last one in the list is the currently
     * active one. The rest are idle and inaccessible by ordinary clients.
     */
    private ArrayList<Pane> panes = new ArrayList<Pane>(1);

    /**
     * Constructs a new PaneSet for the given player
     *
     * @param player the player to construct the PaneSet for
     */
    public PaneSet(Player player) {
        if (player == null) {
            throw new NullPointerException("Player may not be null");
        }
        this.player = player;
    }

    /**
     * The player given in the constructor
     *
     * @return The player given in the constructor
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Adds the given Pane to this PaneSet. The given Pane is added to the top
     * of the list of panes, and the player is informed of the new pane. If the
     * pane is not yet visible, pane.open() is called on it.
     *
     * @param pane the pane to open
     */
    public void add(Pane pane) {
        panes.add(pane); //This is our new Active pane

        if (pane.isOpen() == false) {
            pane.onOpen();
        }

        getPlayer().getProtocol().sendWindowsPane(pane.getChildId(), false);
    }

    /**
     * Removes the given Pane from this PaneSet. If the Pane is visible,
     * pane.close() is called on it. If the given Pane is the last Pane in this
     * PaneSet, an IllegalStateException is thrown, as a client must always have
     * a Pane open. This method notifies the player through the protocol
     *
     * @param pane the Pane to remove
     */
    public void remove(Pane pane) {
        if (panes.size() == 1 && panes.contains(pane)) {
            throw new IllegalStateException("A player must always have an active pane after being instantiated. Removing the pane " + pane + " would mean they have no active panes.");
        }

        panes.remove(pane);

        if (pane.isOpen()) {
            pane.onClose();
        }

        getPlayer().getProtocol().sendWindowsPane(getActive().getChildId(), true);
    }

    /**
     * Fetches the currently active Pane to the player. A player may only have
     * one Pane active at a time.
     *
     * @return The currently active Pane, or null if no pane has been added yet.
     */
    public Pane getActive() {
        if (panes.isEmpty()) return null;
        //The last pane is regarded as the active one.
        return panes.get(panes.size() - 1);
    }

    public Window get(int id) {
        Pane active = getActive();
        if (active == null) return null;

        if (active.getChildId() == id) return active;
        return active.getInterface(id);
    }
}