package org.maxgamer.rs.model.map.path;

import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Locatable;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.object.GameObject;

/**
 * @author netherfoam
 */
public class Move {
    private int buffer = 8;
    private int radius = 0;
    public Move() {

    }

    /**
     * Calculate the maximum coordinate that the entity resides on. Eg, if an entity is 2x3 tiles, and location is
     * at (3022, 3046), this will return (3023, 3048)
     *
     * @param entity
     * @return
     */
    public static Location getMax(Entity entity) {
        if (entity == null) throw new NullPointerException("Entity is null");
        return entity.getLocation().add(entity.getSizeX() - 1, entity.getSizeY() - 1);
    }

    /**
     * Sets the distance of the buffer around this movement. A buffer of zero means that the searched area is
     * the rectangle created by the walking mob, and the target object. A buffer of 10, means that the rectangle
     * is now 20 tiles longer and 20 tiles wider. (+10 to north/south/east/west directions).
     * <p>
     * TLDR: A higher buffer means slower pathfinding, but less likely to fail.
     *
     * @param buffer the buffer. Usually between 5-20
     * @return this
     */
    public Move buffer(int buffer) {
        this.buffer = buffer;

        return this;
    }

    /**
     * Sets the required radius around the target area, where zero indicates exactly the location
     *
     * @param radius the required radius
     * @return this
     */
    public Move radius(int radius) {
        this.radius = radius;

        return this;
    }

    protected PathFinder finder() {
        return new AStar(buffer);
    }

    /**
     * Plan a path for the mob to reach the given location
     *
     * @param mob
     * @param dest
     * @return
     */
    public Path build(Mob mob, Location dest) {
        return finder().findPath(mob.getLocation(), dest.add(-radius, -radius), dest.add(radius, radius), mob.getSizeX(), mob.getSizeY());
    }

    /**
     * Plan a path for the mob to reach the given game object. If the object is solid, this will return a successful path that
     * only reaches the edge of the object.
     *
     * @param mob
     * @param dest
     * @return
     */
    public Path build(Mob mob, GameObject dest) {
        if (dest.getActionCount() == 0 || dest.isSolid()) {
            // The destination is a solid game object
            AStar finder = (AStar) finder();
            Path path = finder.findPath(mob, dest.getLocation().add(-radius, -radius), getMax(dest).add(radius, radius), dest);
            if (path.isEmpty() == false && !path.hasFailed()) {
                path.removeLast();
            }
            return path;
        } else {
            // The destination can be walked on, so we don't modify the path if successful
            return finder().findPath(mob.getLocation(), dest.getLocation().add(-radius, radius), getMax(dest).add(radius, radius), mob.getSizeX(), mob.getSizeY());
        }
    }

    /**
     * Plan a path for the mob to reach the given locatable. If the locatable is a GameObject, this will invoke {@link Move#build(Mob, GameObject)}.
     * If the locatable is some other Entity, this will ensure the mob walks up to the entity. Else, this will walk directly to the location
     * of the given locatable.
     *
     * @param mob
     * @param dest
     * @return
     */
    public Path build(Mob mob, Locatable dest) {
        if (dest instanceof GameObject) {
            // These are clipped and are handled differently
            return build(mob, (GameObject) dest);
        }

        if (dest instanceof Entity) {
            return finder().findPath(mob.getLocation(), dest.getLocation().add(-radius, -radius), getMax((Entity) dest).add(radius, radius), mob.getSizeX(), mob.getSizeY());
        }

        return finder().findPath(mob.getLocation(), dest.getLocation().add(-radius, -radius), dest.getLocation().add(radius, radius), mob.getSizeX(), mob.getSizeY());
    }
}
