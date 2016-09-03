package org.maxgamer.rs.model.map;

import org.maxgamer.rs.cache.EncryptedException;
import org.maxgamer.rs.cache.MapCache;
import org.maxgamer.rs.cache.format.Landscape;
import org.maxgamer.rs.model.events.world.ChunkLoadEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class DynamicMap extends WorldMap {
    /**
     * Constructs a new WorldMap with the given array of chunks. The index
     * [0][0] of the supplied chunks is the southwestern corner. It would appear
     * that the array has to be a square for the client to display it correctly,
     * even if some of the tiles are empty. If you place null spots in the
     * array, there will be no chunk displayed at the location, with no issue.
     *
     * @param world the chunk data to load.
     * @throws EncryptedException
     */
    public DynamicMap(String name, Chunk[][][] world) throws EncryptedException { /* TODO: Is EncryptedException still necessary ? */
        super(name, world.length << WorldMap.CHUNK_BITS, world[0].length << WorldMap.CHUNK_BITS);

        for (int cx = 0; cx < world.length; cx++) {
            for (int cy = 0; cy < world[cx].length; cy++) {
                for (int z = 0; z < world[cx][cy].length && z < 4; z++) {
                    Chunk c = world[cx][cy][z];
                    super.setChunk(cx, cy, z, c);

                    if (c == null) continue;

                    int rx = c.getCacheX() >> 3;
                    int ry = c.getCacheY() >> 3;

                    ByteBuffer map;
                    ByteBuffer objects;

                    try {
                        map = MapCache.getMap(rx, ry);
                        objects = MapCache.getObjects(rx, ry);

                        int xOffset = (cx & ~0x7) << 3; // Real region X
                        int yOffset = (cy & ~0x7) << 3; // Real region Y

                        Landscape l = Landscape.parse(map, objects);
                        c.setLoaded(true);

                        int localChunkX = (c.getCacheX() & 0x7);
                        int localChunkY = (c.getCacheY() & 0x7);
                        l.apply(this, xOffset - (localChunkX << 3) + (cx << 3), yOffset - (localChunkY << 3) + (cy << 3), localChunkX << 3, localChunkY << 3, (localChunkX << 3) + 8, (localChunkY << 3) + 8, z - c.getCacheZ(), c.getCacheZ(), c.getCacheZ());
                    } catch (FileNotFoundException e) {
                        //That map literally doesn't exist.
                        continue;
                    } catch (IOException e) {
                        throw new EncryptedException("Map " + rx + "_" + ry + " failed: file is encrypted", e);
                    }

                    ChunkLoadEvent e = new ChunkLoadEvent(this, c, cx, cy, z);
                    e.call();
                }
            }
        }
    }

    @Override
    protected void fetch(int cx, int cy, int z) {
        //TODO: For very large dynamic maps this might be useful, but we preload
        //the map otherwise.
    }

    @Override
    protected Chunk constructChunk(int chunkX, int chunkY, int z) {
        // TODO Auto-generated method stub
        return null;
    }
}