package org.maxgamer.rs.model.item.ground;

import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.persona.player.Viewport;

import java.util.ArrayList;

/**
 * @author netherfoam
 */
public class GroundItemManager extends Tickable {
    /**
     * The queue of items which are currently spawned. This queue is sorted based on which
     * item will change next.
     */
    private ArrayList<GroundItemStack> items = new ArrayList<>();

    /**
     * Constructs a new, empty ground item manager
     */
    public GroundItemManager() {

    }

    /**
     * Adds the given GroundItemStack to this GroundItemManager. This manager
     * then performs all necessary maintenance on the item.
     *
     * @param item the item to add, not null
     */
    protected void add(GroundItemStack item) {
        assert !items.contains(item) : "Attempted to add a GroundItemStack that was already in the queue";
        items.add(item);

        for (Viewport view : item.getLocation().getNearby(Viewport.class, 1, true)) {
            if (item.isPublic() || item.getOwner() == view.getOwner()) {
                view.getOwner().getProtocol().sendGroundItem(item.getLocation(), item.getItem());
            }
            //else, they can't see it yet.
        }
    }

    /**
     * Removes the given GroundItemStack from this GroundItemManager. This
     * manager will no longer perform all necessary maintenances on the item.
     *
     * @param item the item to remove, not null. No error is thrown if this item
     *             does not exist in it, this simply returns.
     */
    protected void remove(GroundItemStack item) {
        assert items.contains(item) : "Attempted to remove a GroundItemStack that was not in the queue.";
        items.remove(item);

        for (Viewport view : item.getLocation().getNearby(Viewport.class, 0)) {
            if (item.isPublic() || item.getOwner() == view.getOwner()) {
                view.getOwner().getProtocol().removeGroundItem(item.getLocation(), item.getItem());
            }
            //else, they can't see it yet.
        }
    }

    /**
     * Ticks all ground items controlled by this GroundItemManager, and then
     * resubmits this manager for the a future tick, based on when the next
     * ground item checkup is required.
     */
    @Override
    public void tick() {
        for (GroundItemStack g : new ArrayList<>(this.items)) {
            assert !g.isDestroyed() : "GroundItemStack is destroyed but was listed in GroundItemManager.";

            if (g.hasExpired()) {
                // This will also remove it from the queue.
                g.destroy();
            } else if (g.owner != null && g.isPublic()) {
                //Item needs to become public
                for (Viewport view : g.getLocation().getNearby(Viewport.class, 0)) {
                    if (g.getOwner() != view.getOwner()) {
                        //Without sending the item to the owner again, send it to everyone else.
                        view.getOwner().getProtocol().sendGroundItem(g.getLocation(), g.getItem());
                    }
                }
                g.owner = null;
            }
        }

        if (this.isQueued()) this.cancel();
        this.queue(1);
    }
}