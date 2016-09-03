package org.maxgamer.rs.model.entity.mob;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.combat.Damage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author netherfoam
 */
public class UpdateMask implements Mask {
    private Graphics graphics;

    private Animation anim;

    /**
     * Hashmap of hits taken, mob is the dealer
     */
    private HashMap<Mob, ArrayList<Damage>> hits;

    private boolean facing;

    /**
     * The changes in movement
     */
    private MovementUpdate movement;

    private int animPriority = 0;
    private int animEnd = 0;
    private String say;

    /**
     * The owner of this mask, never null.
     */
    private Mob mob;

    /**
     * Constructs a new UpdateMask for the given mob and the given movement mask
     *
     * @param owner        the owner of this update mask
     * @param movementMask the movement mask for the owner
     * @throws NullPointerException if either owner or movementMask are null
     */
    public UpdateMask(Mob owner, MovementUpdate movementMask) {
        if (owner == null) throw new NullPointerException("Owner of an UpdateMask should not be null");
        if (movementMask == null) throw new NullPointerException("UpdatMask does not accept a null MovementUpdate mask");
        this.movement = movementMask;
        this.mob = owner;
        this.reset();
    }

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";
        this.say = say;
    }

    /**
     * The owner of this update mask, never null.
     *
     * @return The owner of this update mask
     */
    public Mob getOwner() {
        return mob;
    }

    /**
     * Adds the given hit to this update mask. This does not actually damage the
     * player, and is not validated. Eg you could technically tell the client a
     * miss did 6 damage, or a hit did 0 damage.
     *
     * @param from the mob that hit the owner
     * @param d    the damage that was dealt
     */
    public void addHit(Mob from, Damage d) {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";

        if (hits == null) hits = new HashMap<Mob, ArrayList<Damage>>(1);
        ArrayList<Damage> list = hits.get(from);
        if (list == null) {
            list = new ArrayList<Damage>(1);
            hits.put(from, list);
        }
        list.add(d);
    }

    /**
     * Hashmap of hits dealt to this mob since the last mask reset The key is
     * the damage dealing mob, the value is the damages they have dealt, last in
     * list is the most recent.
     *
     * @return the hits. This is a pointer, not a copy.
     */
    public HashMap<Mob, ArrayList<Damage>> getHits() {
        return hits;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public void setAnimation(Animation a, int priority) {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";

        if (a == null) {
            //Stop the current animation
            this.anim = new Animation(-1);
            return;
        }

        if (isAnimating() && animPriority > priority) {
            //We're already busy with a more important animation.
            return;
        }

        this.anim = a;
        this.animEnd = Core.getServer().getTicks() + a.getDuration(true);
        this.animPriority = priority;
    }

    /**
     * Returns true if this mob is currently performing an animation.
     *
     * @return true if this mob is currently performing an animation.
     */
    public boolean isAnimating() {
        return animEnd > Core.getServer().getTicks();
    }

    public Animation getAnimation() {
        return anim;
    }

    public MovementUpdate getMovement() {
        return movement;
    }

    public boolean hasFacingChanged() {
        return facing;
    }

    public void setFacing(boolean changed) {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";

        this.facing = changed;
    }

    public void reset() {
        assert Core.getServer().getThread().isServerThread() : "Current thread is " + Thread.currentThread() + ", must be server thread.";

        graphics = null;
        anim = null;
        say = null;

        hits = null;

        movement.reset();
        facing = false;
    }

    public boolean hasChanged() {
        if (graphics != null) {
            return true;
        }
        if (anim != null) {
            return true;
        }

        if (movement.hasChanged()) {
            return true;
        }
        if (hits != null) {
            return true;
        }
        if (facing) {
            return true;
        }
        if (say != null) {
            return true;
        }

        return false;
    }
}