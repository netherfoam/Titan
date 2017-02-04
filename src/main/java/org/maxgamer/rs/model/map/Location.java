package org.maxgamer.rs.model.map;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.map.path.Direction;
import org.maxgamer.rs.model.map.path.Directions;
import org.maxgamer.rs.structure.Filter;
import org.maxgamer.rs.structure.areagrid.Cube;
import org.maxgamer.rs.structure.areagrid.MBR;
import org.maxgamer.rs.structure.configs.ConfigSection;

import java.util.HashSet;

/**
 * Represents a location in game.
 *
 * @author netherfoam
 */
public class Location extends Position implements MBR, Locatable {
    /**
     * The map that this position is based on
     */
    public final WorldMap map;
    /**
     * The height plane for this location.
     */
    public final int z;

    /**
     * Constructs a new location with the given parameters
     *
     * @param map the map
     * @param x   the x coordinate (West[0] to East[MAX])
     * @param y   the y coordinate (South[0] to North[MAX])
     * @param z   the z or height coordinate
     * @throws IllegalArgumentException if the z value is < 0 or > 3.
     */
    public Location(WorldMap map, int x, int y, int z) {
        super(x, y);
        this.map = map;
        this.z = z;

        if (z < 0 || z > 3) {
            throw new IllegalArgumentException("Heights must be in the range of 0-3 inclusive. Given Z: " + z);
        }

        Position offset = map.offset();
        if (offset.x > x) {
            throw new IllegalArgumentException("Location is out of map. Requested x=" + x + ", but map goes as low as x=" + offset.x);
        }

        if (offset.y > y) {
            throw new IllegalArgumentException("Location is out of map. Requested y=" + y + ", but map goes as low as y=" + offset.y);
        }

        if (x >= offset.x + map.width()) {
            throw new IllegalArgumentException("Location is out of map. Requested x=" + x + ", but map goes as high as x=" + (offset.x + map.width()));
        }

        if (y >= offset.y + map.height()) {
            throw new IllegalArgumentException("Location is out of map. Requested y=" + y + ", but map goes as high as y=" + (offset.y + map.height()));
        }
    }

    public Location(int x, int y, int z) {
        this(Core.getServer().getMaps().mainland(), x, y, z);
    }

    public Location(WorldMap map, Position p, int z) {
        this(map, p.x, p.y, z);
    }

    public static Location deserialize(ConfigSection s, Location fallback) {
        if (s == null) {
            return fallback;
        }

        int x = s.getInt("x", -1);
        int y = s.getInt("y", -1);
        int z = s.getInt("z", 0);

        if (x == -1 || y == -1) {
            return fallback;
        }

        String mapName = s.getString("map", Core.getServer().getMaps().mainland().getName());
        WorldMap map = Core.getServer().getMaps().get(mapName);

        return new Location(map, x, y, z);
    }

    public static Location max(Location l, Location... locs) {
        int x = l.x;
        int y = l.y;
        int z = l.z;

        for (Location m : locs) {
            if (m.x > x) x = m.x;
            if (m.y > y) y = m.y;
            if (m.z > z) z = m.z;
            if (m.map != l.map) {
                throw new IllegalArgumentException("Given locations with two different maps. " + l + " versus " + m);
            }
        }

        return new Location(l.map, x, y, z);
    }

    public static Location min(Location l, Location... locs) {
        int x = l.x;
        int y = l.y;
        int z = l.z;

        for (Location m : locs) {
            if (m.x < x) x = m.x;
            if (m.y < y) y = m.y;
            if (m.z < z) z = m.z;
            if (m.map != l.map) {
                throw new IllegalArgumentException("Given locations with two different maps. " + l + " versus " + m);
            }
        }

        return new Location(l.map, x, y, z);
    }

    /**
     * The flags for this location. This will throw a
     * {@link NullPointerException} if the map is null. The flags are a bitset,
     * see Chunk.FLAG_* for values. These are flags like indoors and bridge
     * flags. If the area at this location is not loaded, this will return 0
     *
     * @return the flags for this location, 0 represents no special flags.
     */
    public int getFlags() {
        return map.getFlags(this.x, this.y, this.z);
    }

    /**
     * Returns true if this location has a roof over it.
     *
     * @return true if this location has a roof over it.
     */
    public boolean isIndoors() {
        return (getFlags() & Chunk.FLAG_ROOF) != 0;
    }

    /**
     * Returns true if this location is on a bridge
     *
     * @return true if this location is on a bridge
     */
    public boolean isBridge() {
        return (getFlags() & Chunk.FLAG_BRIDGE) != 0;
    }

    /**
     * The clip for this location. This will throw {@link NullPointerException}
     * if the map is null. The clip is a 32 bit int mask. See {@link ClipMasks}
     * for a list of clip masks by name. If this area of the map is not loaded,
     * this will return -1 (Equivilant to all flags)
     *
     * @return the clip for this location.
     */
    public int getClip() {
        return map.getClip(this.x, this.y, this.z);
    }

    /**
     * The map that this location is on
     *
     * @return the map
     */
    public WorldMap getMap() {
        return map;
    }

    @Override
    public int getMin(int axis) {
        switch (axis) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
        }
        throw new IllegalArgumentException("Invalid axis requested. Given " + axis);
    }

    @Override
    public int getDimension(int axis) {
        return 1;
    }

    @Override
    public int getDimensions() {
        return 3;
    }

    /**
     * Fetches all entities of a given type in a square radius of this location.
     *
     * @param clazz  the type to search for, eg use Player.class to get players.
     * @param radius the radius, this is square and not circular.
     * @return the entities nearby not null.
     */
    public <T extends MBR> HashSet<T> getNearby(Class<T> clazz, int radius) {
        return getNearby(clazz, radius, false);
    }

    /**
     * Fetches all entities of a given type in a square radius of this location.
     *
     * @param clazz  the type to search for, eg use Player.class to get players.
     * @param radius the radius, this is square and not circular.
     * @return the entities nearby not null.
     */
    public <T extends MBR> HashSet<T> getNearby(Class<T> clazz, int radius, boolean allHeights) {
        MBR q = new Cube(new int[]{this.x - radius, this.y - radius, allHeights ? 0 : this.z}, new int[]{radius * 2 + 1, radius * 2 + 1, allHeights ? 3 : 0});

        return getMap().getEntities(q, radius * radius + 4, clazz);
    }

    /**
     * Fetches all entities of a given type in a square radius of this location.
     *
     * @param clazz the type to search for, eg use Player.class to get players.
     * @param radX  the radius for the x coordinate
     * @param radY  the radius for the y coordinate
     * @return the entities nearby not null.
     */
    public <T extends MBR> HashSet<T> getNearby(Class<T> clazz, int radX, int radY) {
        MBR q = new Cube(new int[]{this.x - radX, this.y - radY, this.z}, new int[]{radX * 2, radY * 2, 0});
        return getMap().getEntities(q, radX * radY + 4, clazz);
    }

    /**
     * Constructs a new location by adding the given values to this existing
     * location's coordinates. This does not modify this location
     *
     * @param x the coordinate
     * @param y the coordinate
     * @return the new location
     */
    public Location add(int x, int y) {
        return add(x, y, 0);
    }

    /**
     * Constructs a new location by adding the given values to this existing
     * location's coordinates. This does not modify this location
     *
     * @param x the coordinate
     * @param y the coordinate
     * @return the new location
     */
    public Location add(int x, int y, int z) {
        return new Location(getMap(), this.x + x, this.y + y, this.z + z);
    }

    /**
     * Returns the chunk coordinate of this location by shifting its x
     * coordinate right
     *
     * @return the chunk coordinate
     */
    public int getChunkX() {
        return x >> WorldMap.CHUNK_BITS;
    }

    /**
     * Returns the chunk coordinate of this location by shifting its y
     * coordinate right
     *
     * @return the chunk coordinate
     */
    public int getChunkY() {
        return y >> WorldMap.CHUNK_BITS;
    }

    /**
     * Returns the chunk coordinate of this location by shifting its chunkX
     * right
     *
     * @return the region coordinate
     */
    public int getRegionX() {
        return getChunkX() >> 3;
    }

    /**
     * Returns the chunk coordinate of this location by shifting its chunkY
     * right
     *
     * @return the region coordinate
     */
    public int getRegionY() {
        return getChunkY() >> 3;
    }

    /**
     * Returns the Direction required to move to the given tile from this tile.
     * This method only works for tiles which are next to (diagonal or
     * cardinally) this Location.
     *
     * @param to the target position
     * @return the Direction, not null
     * @throws IllegalArgumentException if the two locations are not next to
     *                                  each other.
     */
    public Direction getDirection(Position to) {
        int dx = to.x - this.x;
        int dy = to.y - this.y;
        return Directions.get(dx, dy);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")@" + map.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        Location p = (Location) o;
        if (p.x != this.x) return false;
        if (p.y != this.y) return false;
        return p.z == this.z && p.map == this.map;

    }

    @Override
    public int hashCode() {
        //If we had negative values for y, then
        //this may cause lots of hash collisions.
        //But, as a rule of thumb, we don't.
        return ((z << 30) | (x << 15) | (y)) + map.hashCode();
    }

    /**
     * Finds the closest Entity of the given type to this location.
     *
     * @param <T>    the entity type to search for (Eg GameObject.class)
     * @param type   the type of entity
     * @param radius the radius to search
     * @param filter the filter for accepting / declining objects
     * @return the closest object that was accepted by the filter
     */
    public <T extends Entity> T getClosest(Class<T> type, int radius, Filter<T> filter) {
        T closest = null;
        int distSq = 0;

        for (T object : getNearby(type, radius)) {
            /* Ask if the object is valid to the filter */
            if (!filter.accept(object)) continue;

            if (closest == null || distSq > object.getLocation().distanceSq(this)) {
                closest = object;
                distSq = object.getLocation().distanceSq(this);
            }
        }
        return closest;
    }

    /**
     * Finds the closest Entity of the given type to this location.
     *
     * @param <T>    the entity type to search for (Eg GameObject.class)
     * @param type   the type of entity
     * @param radius the radius to search
     * @return the closest object of the given type
     */
    public <T extends Entity> T getClosest(Class<T> type, int radius) {
        T closest = null;
        int distSq = 0;

        for (T object : getNearby(type, radius)) {
            if (closest == null || distSq > object.getLocation().distanceSq(this)) {
                closest = object;
                distSq = object.getLocation().distanceSq(this);
            }
        }
        return closest;
    }

    @Override
    public ConfigSection serialize() {
        ConfigSection map = super.serialize();
        map.set("z", this.z);

        if (map != null) {
            map.set("map", this.map.getName());
        }
        return map;
    }

    @Override
    public Location getLocation() {
        return this;
    }

    public boolean near(Location l, int distance) {
        return super.near(l, distance) && l.z == this.z && this.map == l.map;
    }
}