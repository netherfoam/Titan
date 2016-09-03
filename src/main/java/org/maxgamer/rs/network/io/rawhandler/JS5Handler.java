package org.maxgamer.rs.network.io.rawhandler;

import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;

import java.nio.ByteBuffer;

/**
 * @author netherfoam
 */
public class JS5Handler extends RawHandler {
    /**
     * 27 integers that deal with loading required elements (Native libraries,
     * and files within certain index files that are necessary to run the game).
     * Further details at
     * http://www.rune-server.org/runescape-development/rs-503
     * -client-server/285259-new-js5-protocol-634-a.html
     */
    //These were found in Dementhium's source.
    /*
	 * public static int[] DATA = { 56, 79325, 55568, 46770, 24563, 299978,
	 * 44375, 0, 4176, 3589, 109125, 604031, 176138, 292288, 350498, 686783,
	 * 18008, 20836, 16339, 1244, 8142, 743, 119, 699632, 932831, 3931, 2974, };
	 */

    /**
     * Data sent for the 27 integers, TODO! find out were to get/generate this
     * data from the cache.
     */
    //These were found over here at
    //http://www.rune-server.org/runescape-development/rs-503-client-server/configuration/534619-793-js5-stuff-2.html
    //They do not appear to work for the 637 client.
	/*
	 * public static final int[] UPDATE_DATA = { 1582, 78700, 44880, 39771,
	 * 358716, 44375, 0, 16497, 9734, 408717, 894828, 276612, 426615, 538603,
	 * 776400, 24019, 17682, 1244, 20331, 1775, 119, 967757, 2188046, 4930, 3578
	 * };
	 */

    //These are found in the feather server.
	/*
	 * public static final int[] GRAB_SERVER_KEYS = { 1362, 77448, 44880, 39771,
	 * 24563, 363672, 44375, 0, 1614, 0, 5340, 142976, 741080, 188204, 358294,
	 * 416732, 828327, 19517, 22963, 16769, 1244, 11976, 10, 15, 119, 817677,
	 * 1624243};
	 */

    public static int[] DATA = {1362, 77448, 44880, 39771, 24563, 363672, 44375, 0, 1614, 0, 5340, 142976, 741080, 188204, 358294, 416732, 828327, 19517, 22963, 16769, 1244, 11976, 10, 15, 119, 817677, 1624243};

    public JS5Handler(Session s) {
        super(s);
    }

    @Override
    public void handle(RSByteBuffer in) {
        int revision = in.readInt();
        getSession().setRevision(revision);

        ByteBuffer b = ByteBuffer.allocate(1 + 27 * 4);
        b.put((byte) 0);

        int[] data;
        switch (revision) {
            case 637:
                data = new int[]{56, 79325, 55568, 46770, 24563, 299978, 44375, 0, 4176, 3589, 109125, 604031, 176138, 292288, 350498, 686783, 18008, 20836, 16339, 1244, 8142, 743, 119, 699632, 932831, 3931, 2974,};
                break;

            case 667:
                data = new int[]{1362, 77448, 44880, 39771, 24563, 363672, 44375, 0, 1614, 0, 5340, 142976, 741080, 188204, 358294, 416732, 828327, 19517, 22963, 16769, 1244, 11976, 10, 15, 119, 817677, 1624243};
                break;

            default:
                throw new RuntimeException("Unsupported revision: " + revision);
        }
        for (int i = 0; i < data.length; i++) {
            b.putInt(data[i]);
        }
        b.flip();
        getSession().write(b);

        getSession().setHandler(new CacheRequestHandler(getSession()));
    }

}