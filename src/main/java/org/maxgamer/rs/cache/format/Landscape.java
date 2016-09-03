package org.maxgamer.rs.cache.format;

import org.maxgamer.rs.model.map.*;
import org.maxgamer.rs.model.map.object.GameObject;
import org.maxgamer.rs.model.map.object.StaticGameObject;
import org.maxgamer.rs.model.map.path.Directions;
import org.maxgamer.rs.model.map.path.SimpleDirection;
import org.maxgamer.rs.util.BufferUtils;
import org.maxgamer.rs.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * @author netherfoam
 */
public class Landscape {
    /**
     * The maximum X axis length of a Landscape "region".
     */
    private static final int MAX_X = 64;

    /**
     * The maximum Y axis length of a Landscape "region".
     */
    private static final int MAX_Y = 64;

    /**
     * The maximum height axis length of a Landscape "region".
     */
    private static final int MAX_HEIGHT = 4;
    /**
     * The flags for this landscape at each tile
     */
    private byte[][][] flags = new byte[MAX_HEIGHT][MAX_X][MAX_Y];
    /**
     * Contains an ID that is used to determine what type of terrain is
     * on the first layer. Eg grass.
     */
    private byte[][][] underlays = new byte[MAX_HEIGHT][MAX_X][MAX_Y];
    private byte[][][] overlays = new byte[MAX_HEIGHT][MAX_X][MAX_Y];
    /**
     * The list of objects in this landscape in no particular order
     */
    private ArrayList<ObjectData> objects = new ArrayList<ObjectData>(4096);
    private Landscape() {
        //Private constructor
    }

    /**
     * Parses the given files into a Landscape object
     *
     * @param landscape the landscape data, eg. Terrain
     * @param objects   the game objects data, eg. Walls, Houses, Trees. This must
     *                  be decrypted prior to use
     * @return the Landscape, not null
     */
    public static Landscape parse(ByteBuffer landscape, ByteBuffer objects) {
        Landscape l = new Landscape();

        //First, we parse the landscape flag data
        for (int z = 0; z < MAX_HEIGHT; z++) {
            for (int localX = 0; localX < MAX_X; localX++) {
                for (int localY = 0; localY < MAX_Y; localY++) {
                    while (true) {
                        int v = landscape.get() & 0xff;
                        if (v == 0) {
                            //End of tile description
                            break;
                        } else if (v == 1) {
                            //Tile data unused, possibly elevation or colour?
                            landscape.get();
                            break;
                        } else if (v <= 49) {
                            //Tile data unused, possibly elevation or colour?
                            //tile_layer1_type[plane][localX][localY] = stream.readSignedByte();
                            //tile_layer1_shape[plane][localX][localY] = (byte) ((value - 2) / 4);
                            //tile_layer1_orientation[plane][localX][localY] = (byte) ((value - 2) + i1 & 3);
                            l.overlays[z][localX][localY] = landscape.get();
                            //tile(Shape|type)[z][x][y] = (v - 2) / 4
                            //tileDirection[z][x][y] = (v something..)

                        } else if (v <= 81) {
                            l.flags[z][localX][localY] = (byte) (v - 49);
                        } else {
                            // Describes the ID for the tile underlay (Eg grass)
                            l.underlays[z][localX][localY] = (byte) (v - 81);
                        }
                    }
                }
            }
        }

        //Next, we parse the game objects data
        int objectId = -1;
        int incr;

        while ((incr = BufferUtils.readExtendedSmart(objects)) != 0) {
            objectId += incr;
            int location = 0;
            int incr2;
            while ((incr2 = BufferUtils.readSmart(objects)) != 0) {
                location += incr2 - 1;

                ObjectData d = new ObjectData();
                d.objectId = objectId & 0xFFFF;
                d.localX = location >> 6 & 0x3f;
                d.localY = location & 0x3f;
                d.height = location >> 12;
                int objectData = objects.get() & 0xFF;
                d.type = objectData >> 2;
                d.rotation = objectData & 0x3;

                if ((l.flags[1][d.localX][d.localY] & Chunk.FLAG_BRIDGE) == Chunk.FLAG_BRIDGE) {
                    d.height--;
                }

                if (d.height < 0) continue; //I don't understand why this is even in the cache unless we're doing something wrong?

                l.objects.add(d);
            }
        }

        return l;
    }

    public byte getUnderlay(int x, int y, int z) {
        return underlays[z][x][y];
    }

    public byte getOverlay(int x, int y, int z) {
        return overlays[z][x][y];
    }

    /**
     * Fetches the flags for the given coordinates
     *
     * @param x the x coordinate, 0-63
     * @param y the y coordinate, 0-63
     * @param z the z coordinate, 0-3
     * @return the flags, 0-31 corresponding to Landscape.FLAG_*
     */
    public int getFlags(int x, int y, int z) {
        return flags[z][x][y];
    }

    /**
     * Applies this Landscape to the given map, adding all game objects in the
     * given area to the map and any clip flags required by the landscape. This
     * method allows the application of only a section of the map if desired
     * (say for dynamic maps) or the whole lot (say for standard maps). Standard
     * map application will be a call of Landscape.apply(map, xOffset, yOffset,
     * 0, 0, 64, 64);.
     *
     * @param map          the map to apply it to
     * @param xOffset      the corner tileX value of this landscape
     * @param yOffset      the corner tileY value of this landscape
     * @param localXOffset the xOffset to start at, this is within the
     *                     Landscape, most commonly 0
     * @param localYOffset the yOffset to start at, this is within the
     *                     Landscape, most commonly 0
     * @param localXMax    the xOffset to end at, this is within the Landscape,
     *                     most commonly 64
     * @param localYMax    the yOffset to end at, this is within the Landscape,
     *                     most commonly 64
     * @param minZ         same as above, but for Z value. Min 0.
     * @param maxZ         same as above, but for Z value. Max 3.
     * @throws IOException if an object definition cannot be loaded
     */
    public void apply(WorldMap map, int xOffset, int yOffset, int localXOffset, int localYOffset, int localXMax, int localYMax, int zOffset, int minZ, int maxZ) throws IOException {
        try {
            for (int z = minZ; z <= maxZ; z++) {
                for (int localX = localXOffset; localX < localXMax; localX++) {
                    for (int localY = localYOffset; localY < localYMax; localY++) {

                        //The special flags for this tile
                        int flags = this.flags[z][localX][localY];

                        Chunk c = map.getChunk((xOffset + localX) >> WorldMap.CHUNK_BITS, (yOffset + localY) >> WorldMap.CHUNK_BITS, zOffset + z);
                        if (c != null) {
                            c.setFlag((xOffset + localX) & 7, (yOffset + localY) & 7, flags);
                        }

                        int height = z;
                        if ((flags & Chunk.FLAG_CLIP) == Chunk.FLAG_CLIP) {
                            if ((this.flags[1][localX][localY] & Chunk.FLAG_BRIDGE) == Chunk.FLAG_BRIDGE) {
                                height--;
                            }
                        }
                        height += zOffset;
                        if ((flags & StandardMap.FLAG_CLIP) == StandardMap.FLAG_CLIP) {
                            map.addClip(xOffset + localX, yOffset + localY, height, ClipMasks.BLOCKED_TILE);
                        }

                        if ((flags & StandardMap.FLAG_BRIDGE) == StandardMap.FLAG_BRIDGE) {
                            map.addClip(xOffset + localX, yOffset + localY, height, ClipMasks.BLOCKED_TILE);
                        }

						/*
						 * if((flags & FLAG_ROOF) == FLAG_ROOF){ addClip(xOffset
						 * + localX, yOffset + localY, z,
						 * ClipMasks.BLOCKED_TILE); }
						 */

                        if ((flags & WorldMap.FLAG_UNKNOWN) == WorldMap.FLAG_UNKNOWN) {
                            map.addClip(xOffset + localX, yOffset + localY, height, ClipMasks.BLOCKED_TILE);
                        }

                        if ((flags & WorldMap.FLAG_UNKNOWN2) == WorldMap.FLAG_UNKNOWN2) {
                            map.addClip(xOffset + localX, yOffset + localY, height, ClipMasks.BLOCKED_TILE);
                        }

                        flags = flags & ~(WorldMap.FLAG_CLIP | WorldMap.FLAG_BRIDGE | WorldMap.FLAG_ROOF | WorldMap.FLAG_UNKNOWN | WorldMap.FLAG_UNKNOWN2);
                        if (flags != 0) {
                            Log.debug("Leftover flag at " + (xOffset + localX) + ", " + (yOffset + localY) + ", " + height + ", Flag remains: " + flags);
                        }
                    }
                }
            }

            Position min = map.offset();

            for (ObjectData d : this.objects) {
                if (d.localX < localXOffset || d.localX >= localXMax) continue; //Out of bounds given
                if (d.localY < localYOffset || d.localY >= localYMax) continue; //Out of bounds given
                if (d.height < minZ || d.height > maxZ) continue;

                // Ensure the object is within the map's bounds
                if (xOffset + d.localX < min.x) continue;
                if (xOffset + d.localX >= min.x + map.width()) continue;
                if (yOffset + d.localY < min.y) continue;
                if (yOffset + d.localY >= min.y + map.height()) continue;

                d.toObject(map, xOffset, yOffset, zOffset);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    //May prove useful:
    /*
	 * int flags = 0;
	 * 
	 * while (true) { int value = buffer.get() & 0xFF;;
	 * 
	 * if (value == 0) { TileObject tile = null; Position position;
	 * 
	 * if (plane == 0) { position = new Position(x + localX, y + localY, plane);
	 * } else { position = new Position(x + localX, y + localY, plane - 1); }
	 * 
	 * if ((flags & BRIDGE_FLAG) != 0) { tile = new
	 * TileObject(TileObject.Type.BRIDGE, position); }
	 * 
	 * if ((flags & FLAG_CLIP) != 0) { tile = new
	 * TileObject(TileObject.Type.CLIPPED, position); } tiles.add(tile); break;
	 * } if (value == 1) { int derp1 = buffer.get() & 0xFF;
	 * //System.out.println("Derp1: " + derp1);
	 * 
	 * TileObject tile = null; Position position;
	 * 
	 * if (plane == 0) { position = new Position(x + localX, y + localY, plane);
	 * } else { position = new Position(x + localX, y + localY, plane - 1); } if
	 * ((flags & BRIDGE_FLAG) != 0) { tile = new
	 * TileObject(TileObject.Type.BRIDGE, position); }
	 * 
	 * if ((flags & FLAG_CLIP) != 0) { tile = new
	 * TileObject(TileObject.Type.CLIPPED, position); } tiles.add(tile); break;
	 * } if (value <= 49) { int derp2 = buffer.get() & 0xFF;
	 * //System.out.println("Derp2: " + derp2);
	 * //tile_layer1_type[plane][localX][localY] = stream.readSignedByte();
	 * //tile_layer1_shape[plane][localX][localY] = (byte) ((value - 2) / 4);
	 * //tile_layer1_orientation[plane][localX][localY] = (byte) ((value - 2) +
	 * i1 & 3); } else if (value <= 81) { flags = value - 49;
	 * //System.out.println("Flag: " + flags);
	 * //tile_flags[plane][localX][localY] = (byte) (value - 49); } else { //
	 * flags = value - 81; //tile_layer0_type[plane][localX][localY] = (byte)
	 * (value - 81); }
	 */

    /**
     * Represents an object which has not yet been instantiated, but is inside a
     * parsed Landscape
     *
     * @author netherfoam
     */
    private static class ObjectData {
        /**
         * The ID of the object
         */
        int objectId;

        /**
         * The localX position of the object inside the Landscape, this varies
         * from 0 to 63
         */
        int localX;

        /**
         * The localY position of the object inside the Landscape, this varies
         * from 0 to 63
         */
        int localY;

        /**
         * The z axis (Plane) used for this object, varies from 0 to 3
         */
        int height;

        /**
         * The gameobject type, see {@link GameObject#getType()} for
         * descriptions of each.
         */
        int type;

        /**
         * The rotation of this object, 0-3. Represents NORTH, EAST, SOUTH, WEST
         * in order of 0, 1, 2, 3.
         */
        int rotation;

        private ObjectData() {
            //Private Constructor
        }

        /**
         * Creates a new StaticGameObject and spawns it into the given map.
         *
         * @param map     the map to spawn the object to
         * @param xOffset the xOffset (this.localX is added to it)
         * @param yOffset the yOffset (this.localY is added to it)
         * @return the object, not null
         * @throws IOException if there is an IOException constructing the
         *                     StaticGameObject (No definition found)
         */
        public StaticGameObject toObject(WorldMap map, int xOffset, int yOffset, int zOffset) throws IOException {
            SimpleDirection d = Directions.NORTH;
            switch (rotation) {
                case 0:
                    d = Directions.NORTH;
                    break;
                case 1:
                    d = Directions.EAST;
                    break;
                case 2:
                    d = Directions.SOUTH;
                    break;
                case 3:
                    d = Directions.WEST;
                    break;
            }
            StaticGameObject g = new StaticGameObject(objectId, type, new Location(map, xOffset + localX, yOffset + localY, zOffset + height), d);
            return g;
        }
    }
}