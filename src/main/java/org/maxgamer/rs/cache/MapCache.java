package org.maxgamer.rs.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.maxgamer.rs.core.Core;

/**
 * @author netherfoam
 */
public class MapCache {
	private static HashMap<String, SoftReference<ByteBuffer>> objects = new HashMap<String, SoftReference<ByteBuffer>>(400);
	
	/**
	 * Fetches the objects at the given zoneX
	 * @param zoneX the x >> 6
	 * @param zoneY the y >> 6
	 * @return the bytestream for objects, unencrypted, or null
	 * @throws IOException
	 */
	public static ByteBuffer getObjects(int zoneX, int zoneY) throws IOException {
		//An example of this is performed over at 
		//http://www.rune-server.org/runescape-development/rs-503-client-server/help/450588-openrs-map-decrypting.html
		String key = "l" + zoneX + "_" + zoneY;
		SoftReference<ByteBuffer> ref = objects.get(key);
		ByteBuffer bb =  null;
		if(ref != null){
			bb = ref.get();
		}
		if (bb != null) {
			bb = bb.asReadOnlyBuffer();
			return bb;
		}
		
		//We haven't got a previously decrypted map data.
		try {
			int fileId = Core.getCache().getFileId(IDX.LANDSCAPES, key);
			CacheFile c = Core.getCache().getFile(IDX.LANDSCAPES, fileId);
			
			bb = c.getData();
			objects.put(key, new SoftReference<ByteBuffer>(bb));
			return bb.asReadOnlyBuffer(); //This is necessary, as we're storing the above bb in the objects map
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Fetches the RSInputStream for the map at the given coordinates. The
	 * coordinates are chunk >> 3, for example posX = 3000 will return zone46
	 * (3000 >> 6 == 46)
	 * @param zoneX The zoneX (x >> 6)
	 * @param zoneY The zoneY (x >> 6)
	 * @return The map input stream, unencrypted or null if no data available
	 * @throws IOException
	 */
	public static ByteBuffer getMap(int zoneX, int zoneY) throws IOException {
		try {
			int fileId = Core.getCache().getFileId(IDX.LANDSCAPES, "m" + zoneX + "_" + zoneY);
			CacheFile f = Core.getCache().getFile(IDX.LANDSCAPES, fileId);
			
			ByteBuffer bb = f.getData();
			return bb;
		}
		catch (FileNotFoundException e) {
			//We return null on file not found.
			return null;
		}
	}
}