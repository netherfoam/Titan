package org.maxgamer.rs.model.map;

import org.maxgamer.rs.cache.MapCache;
import org.maxgamer.rs.cache.format.Landscape;
import org.maxgamer.rs.model.events.world.ChunkLoadEvent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SubMap extends WorldMap {
    public SubMap(String name, Position offset, int width, int height) {
        super(name, offset, width, height);
    }

    @Override
    protected void fetch(int x, int y, int z) throws IOException {
        //Region x,y coordinates. These are tile# >> 6
        int rx = x >> 3;
        int ry = y >> 3;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 4; k++) {
                    //Chunk x,y coordinates. Thes are tile# >> 3
                    int cx = (rx << 3) + i;
                    int cy = (ry << 3) + j;
                    Chunk c = getChunk(cx, cy, k);
                    if (c != null) {
                        c.setLoaded(true);
                    }
                }
            }
        }

        ByteBuffer map;
        ByteBuffer objects;

        try {
            map = MapCache.getMap(rx, ry);
            objects = MapCache.getObjects(rx, ry);

            //Tile x,y coordinates. Each coordinate is unique for tiles.
            int tx = rx << (3 + WorldMap.CHUNK_BITS);
            int ty = ry << (3 + WorldMap.CHUNK_BITS);

            Landscape l = Landscape.parse(map, objects);
            l.apply(this, tx, ty, 0, 0, 64, 64, 0, 0, 3);

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    for (int k = 0; k < 4; k++) {
                        int cx = (rx << 3) + i;
                        int cy = (ry << 3) + j;
                        Chunk c = getChunk(cx, cy, k);
                        if (c != null) {
                            ChunkLoadEvent e = new ChunkLoadEvent(this, c, cx, cy, k);
                            e.call();
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            //That map literally doesn't exist.
        }
    }

    @Override
    protected Chunk constructChunk(int chunkX, int chunkY, int z) {
        return new Chunk(chunkX, chunkY, z);
    }
}
