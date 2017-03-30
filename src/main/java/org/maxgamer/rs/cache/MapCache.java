package org.maxgamer.rs.cache;

import org.maxgamer.rs.assets.AssetStorage;
import org.maxgamer.rs.assets.codec.asset.Asset;
import org.maxgamer.rs.core.Core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * @author netherfoam
 */
public class MapCache {
    private static HashMap<String, SoftReference<ByteBuffer>> objects = new HashMap<>(400);

    /**
     * Fetches the objects at the given zoneX and zoneY
     *
     * @param zoneX the x >> 6
     * @param zoneY the y >> 6
     * @return the bytestream for objects, unencrypted, or null
     * @throws IOException
     */
    public static ByteBuffer getObjects(int zoneX, int zoneY) throws IOException {
        return getObjects(Core.getCache(), zoneX, zoneY);
    }

    /**
     * Fetches the objects at the given zoneX and zoneY
     *
     * @param cache where the data is located
     * @param zoneX the x >> 6
     * @param zoneY the y >> 6
     * @return the bytestream for objects, unencrypted, or null
     * @throws IOException
     */
    public static ByteBuffer getObjects(AssetStorage cache, int zoneX, int zoneY) throws IOException {
        //An example of this is performed over at
        //http://www.rune-server.org/runescape-development/rs-503-client-server/help/450588-openrs-map-decrypting.html
        String key = "l" + zoneX + "_" + zoneY;
        SoftReference<ByteBuffer> ref = objects.get(key);
        ByteBuffer bb = null;
        if (ref != null) {
            bb = ref.get();
        }
        if (bb != null) {
            bb = bb.asReadOnlyBuffer();
            return bb;
        }

        //We haven't got a previously decrypted map data.
        Asset a = cache.read(IDX.LANDSCAPES, key);

        bb = a.getPayload();
        objects.put(key, new SoftReference<>(bb));

        return bb.asReadOnlyBuffer(); //This is necessary, as we're storing the above bb in the objects map
    }

    /**
     * Fetches the RSInputStream for the map at the given coordinates. The
     * coordinates are chunk >> 3, for example posX = 3000 will return zone46
     * (3000 >> 6 == 46)
     *
     * @param zoneX The zoneX (x >> 6)
     * @param zoneY The zoneY (x >> 6)
     * @return The map input stream, unencrypted or null if no data available
     * @throws IOException
     */
    public static ByteBuffer getMap(int zoneX, int zoneY) throws IOException {
        try {
            Asset a = Core.getCache().read(IDX.LANDSCAPES, "m" + zoneX + "_" + zoneY);

            return a.getPayload();
        } catch (FileNotFoundException e) {
            //We return null on file not found.
            return null;
        }
    }
}