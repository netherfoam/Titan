package org.maxgamer.rs.model.map;

import org.maxgamer.rs.cache.EncryptedException;

import java.util.HashMap;

/**
 * @author netherfoam
 * @date 1 Dec 2015
 * <p>
 * A builder utility that allows the construction of a DynamicMap without having to
 * manually organise a 3D chunk array
 */
public class MapBuilder {
    /**
     * The chunks which have been added
     */
    private HashMap<Integer, Chunk> parts = new HashMap<>();

    /**
     * Adds the given chunk to the given coordinates. If the chunk is null,
     * this removes the chunk.
     *
     * @param chunkX the X coordinate of the chunk (Real coord divided by 8)
     * @param chunkY the Y coordinate of the chunk (Real coord divided by 8)
     * @param chunkZ the Z coordinate of the chunk (Real coord divided by 8)
     * @param c      the chunk
     */
    public void set(int chunkX, int chunkY, int chunkZ, Chunk c) {
        if (c == null) {
            /* Delete requested */
            parts.remove(key(chunkX, chunkY, chunkZ));
        } else {
            /* Add the chunk at the coordinates */
            parts.put(key(chunkX, chunkY, chunkZ), c);
        }
    }

    /**
     * Returns the chunk at the given coordinates. May be null.
     *
     * @param chunkX the chunk X coordinate
     * @param chunkY the chunk Y coordinate
     * @param chunkZ the chunk Z coordinate
     * @return the Chunk at the coordinates or null
     */
    public Chunk get(int chunkX, int chunkY, int chunkZ) {
        return parts.get(key(chunkX, chunkY, chunkZ));
    }

    /**
     * Exports this MapBuilder to a DynamicMap.
     *
     * @param name the name of the map
     * @return the map
     * @throws EncryptedException if part of the map can't be decrpyted
     */
    public DynamicMap create(String name) throws EncryptedException {
		/* The maximum of each coordinate we were given */
        int maxX = 0;
        int maxY = 0;
        int maxZ = 0;
		
		/* Extract our maximums */
        for (int i : parts.keySet()) {
            maxY = Math.max(maxY, i & 0x7FFF);
            i >>= 15;
            maxX = Math.max(maxX, i & 0x7FFF);
            i >>= 15;
            maxZ = Math.max(maxZ, i & 0x3);
        }
		
		/* Create our 3D chunk array */
        Chunk[][][] chunks = new Chunk[maxX + 1][maxY + 1][maxZ + 1];
        for (int i = 0; i <= maxX; i++) {
            for (int j = 0; j <= maxY; j++) {
                for (int k = 0; k <= maxZ; k++) {
					/* It's likely that a lot of these will be null if our map has holes */
                    chunks[i][j][k] = parts.get(key(i, j, k));
                }
            }
        }
		
		/* Construct the DynamicMap */
        return new DynamicMap(name, chunks);
    }

    /**
     * Converts the given trio of coordinates into an integer key.
     *
     * @param chunkX the chunk X coordinate
     * @param chunkY the chunk Y coordinate
     * @param chunkZ the chunk Z coordinate
     * @return an integer representing all of the coordinates
     */
    private int key(int chunkX, int chunkY, int chunkZ) {
        if (chunkX >= 2048 || chunkX < 0 || chunkY >= 2048 || chunkY < 0 || chunkZ >= 4 || chunkZ < 0) {
            throw new IllegalArgumentException("ChunkX and ChunkY must range from 0-2047 inclusive (given x:" + chunkX + " and y: " + chunkY + "), and chunkZ must range from 0-3 inclusive (Given z: " + chunkZ + ")");
        }
        return (chunkZ << 30) | (chunkX << 15) | (chunkY);
    }
}
