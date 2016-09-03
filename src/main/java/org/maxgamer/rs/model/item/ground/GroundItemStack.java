package org.maxgamer.rs.model.item.ground;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.item.ItemStack;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class GroundItemStack extends Entity implements Interactable {
    /**
     * The ItemStack on the ground
     */
    protected ItemStack stack;

    /**
     * The tick when the privacy expires on this item and becomes public
     */
    protected int privacy;

    /**
     * The tick when this item expires and is removed entirely
     */
    protected int expires;

    /**
     * The player who owns this item stack, eg for privacy.
     */
    protected Mob owner;

    /**
     * Creates a new GroundItemStack. This item will not be spawned until the
     * location is set through {@link GroundItemStack#setLocation(Location)}.
     *
     * @param stack   the item this represents
     * @param owner   The owner of the items, null if none (Immediately public)
     * @param privacy The number of ticks this item will be private for.
     * @param expires The number of ticks this item will be valid for. When this
     *                tick number is reached, it will be removed from the world
     */
    public GroundItemStack(ItemStack stack, Mob owner, int privacy, int expires) {
        if (stack == null) throw new NullPointerException("ItemStack may not be null");
        if (owner == null && privacy > 0) throw new IllegalArgumentException("If owner is null, privacy may not be > 0. Given privacy " + privacy);
        if (owner != null && privacy < 0) throw new IllegalArgumentException("If owner != null, then privacy ticks must be >= 0");
        if (expires < 0) throw new IllegalArgumentException("Expires must be >= 0");

        this.stack = stack;
        this.owner = owner;
        this.privacy = privacy + Core.getServer().getTicks();
        this.expires = expires + Core.getServer().getTicks();
    }

    @Override
    public void setLocation(Location l) {
        if (getLocation() != null) {
            Core.getServer().getGroundItems().remove(this);
        }

        super.setLocation(l);

        if (getLocation() != null) {
            Core.getServer().getGroundItems().add(this);
        }
    }

    /**
     * The ID of the item represented by this GroundItemStack, shorthand for
     * getItem().getId()
     *
     * @return The ID of the item represented by this GroundItemStack
     */
    public int getId() {
        return stack.getId();
    }

    /**
     * The ItemStack available here
     *
     * @return
     */
    public ItemStack getItem() {
        return stack;
    }

    /**
     * Returns true if this {@link GroundItemStack} has the given interaction option
     *
     * @param name
     * @return
     */
    public boolean hasOption(String name) {
        return getItem().hasGroundOption(name);
    }

    /**
     * True if this item has expired and has been deleted
     *
     * @return true if this item has expired
     */
    public boolean hasExpired() {
        return Core.getServer().getTicks() >= expires;
    }

    /**
     * True if this item is currently public (privacy has expired or owner is
     * null)
     *
     * @return true if this item is currently public
     */
    public boolean isPublic() {
        return owner == null || Core.getServer().getTicks() >= privacy;
    }

    /**
     * Fetches the owner of this item, may be null if public
     *
     * @return the owner of this item
     */
    public Mob getOwner() {
        return owner;
    }

    @Override
    public boolean isVisible(Entity viewer) {
        if (viewer instanceof Mob) {
            if (!isPublic() && getOwner() != viewer) return false;
        }
        return super.isVisible(viewer);
    }

    @Override
    public void destroy() {
        //Worth noting that this calls setLocation(null), which will
        //remove the item from the GroundItemManager.
        super.destroy();
    }

    @Override
    public String[] getOptions() {
        return getItem().getGroundOptions();
    }

    @Override
    public String getName() {
        return getItem().getName();
    }
}