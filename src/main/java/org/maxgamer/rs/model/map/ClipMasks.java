package org.maxgamer.rs.model.map;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * @author netherfoam
 */
public class ClipMasks {
    public static final int WALL_NORTH = 0x2;
    public static final int WALL_SOUTH = 0x20;
    public static final int WALL_EAST = 0x8;
    public static final int WALL_WEST = 0x80;
    public static final int WALL_NORTH_WEST = 0x1;
    public static final int WALL_SOUTH_EAST = 0x10;
    public static final int WALL_NORTH_EAST = 0x4;
    public static final int WALL_SOUTH_WEST = 0x40;
    /**
     * Represents the sum of all WALL_* values.
     */
    public static final int WALL_ALL = WALL_NORTH | WALL_SOUTH | WALL_EAST | WALL_WEST | WALL_NORTH_WEST | WALL_SOUTH_EAST | WALL_NORTH_EAST | WALL_SOUTH_WEST;
    public static final int OBJECT_TILE = 0x100;
    /**
     * BLOCKED_TILE can mean that a particular map tile was flagged as
     * unwalkable or that there is a floor decoration preventing movement on
     * that tile.
     */
    public static final int BLOCKED_TILE = 0x200000;
    public static final int UNLOADED_TILE = 0x80000; // was 0x1000000;
    public static final int OBJECT_BLOCK = 0x20000;

    /**
     * UNKNOWN is checked but never set, so perhaps it has some use on the
     * server.
     */
    //public static final int UNKNOWN         = 0x80000;
    public static final int OBJECT_ALLOW_RANGE = 0x40000000;
    public static final int BLOCKED_NORTH = WALL_NORTH << 9; //0x400
    public static final int BLOCKED_SOUTH = WALL_SOUTH << 9; //0x4000
    public static final int BLOCKED_EAST = WALL_EAST << 9; //0x1000
    public static final int BLOCKED_WEST = WALL_WEST << 9; //0x10000
    public static final int BLOCKED_NORTH_WEST = WALL_NORTH_WEST << 9; //0x200
    public static final int BLOCKED_NORTH_EAST = WALL_NORTH_EAST << 9; //0x800
    public static final int BLOCKED_SOUTH_EAST = WALL_SOUTH_EAST << 9; //0x2000
    public static final int BLOCKED_SOUTH_WEST = WALL_SOUTH_WEST << 9; //0x8000
    public static final int BLOCKED_ALL = BLOCKED_NORTH | BLOCKED_SOUTH | BLOCKED_EAST | BLOCKED_WEST | BLOCKED_NORTH_WEST | BLOCKED_NORTH_EAST | BLOCKED_SOUTH_EAST | BLOCKED_SOUTH_WEST;
    public static final int WALL_ALLOW_RANGE_NORTH = WALL_NORTH << 22; //0x800000
    public static final int WALL_ALLOW_RANGE_SOUTH = WALL_SOUTH << 22; //0x8000000
    public static final int WALL_ALLOW_RANGE_WEST = WALL_WEST << 22; //0x20000000
    public static final int WALL_ALLOW_RANGE_EAST = WALL_EAST << 22; //0x2000000
    public static final int WALL_ALLOW_RANGE_NORTH_WEST = WALL_NORTH_WEST << 22; //0x400000
    public static final int WALL_ALLOW_RANGE_NORTH_EAST = WALL_NORTH_EAST << 22; //0x1000000
    public static final int WALL_ALLOW_RANGE_SOUTH_EAST = WALL_SOUTH_EAST << 22; //0x4000000
    public static final int WALL_ALLOW_RANGE_SOUTH_WEST = WALL_SOUTH_WEST << 22; //0x10000000
    public static final int WALL_ALLOW_RANGE_ALL = WALL_ALLOW_RANGE_NORTH | WALL_ALLOW_RANGE_SOUTH | WALL_ALLOW_RANGE_EAST | WALL_ALLOW_RANGE_WEST | WALL_ALLOW_RANGE_NORTH_WEST | WALL_ALLOW_RANGE_NORTH_EAST | WALL_ALLOW_RANGE_SOUTH_EAST | WALL_ALLOW_RANGE_SOUTH_WEST;
    //Unknown
    public static final int DECORATION_BLOCK = 0x40000;
    private ClipMasks() {
        //Private constructor
    }

    /**
     * Some additional information available here (See first post too)
     * http://www
     * .rune-server.org/runescape-development/rs2-client/configuration/
     * 347961-tile-clipping-flags-5.html See also
     * http://www.powerbot.org/community
     * /topic/1181523-clipping-tilederive-and-more-help/ Mage/range:
     * "Those flags are 0x400000 to 0x40000000 (at least in the newer clients), so I don't think that's necessarily the case."
     */

    public static void main(String[] args) throws Exception {
        //Decompiles the given clip numbers into named clip values.
        //int[] lookups = new int[]{0x48240000, 0x40a40000, 0x60240000, 0x42240000, 0x78240000, 0x4e240000, 0x60e40000, 0x43a40000};
        //int[] lookups = new int[]{0x400200FF};
        int[] lookups = new int[]{0x1000804};

        for (int look : lookups) {
            System.out.println(String.format("0x%X: ", look));
            for (String clip : getClipNames(look)) {
                System.out.println(clip);
            }
            System.out.println();
        }
    }

    /**
     * Fetches the programmer's name of all clips that are required to make the
     * supplied value as a clip
     *
     * @param lookup the clip value to identify
     * @return the name of the clips applicable to the lookup value
     */
    public static String[] getClipNames(int lookup) {
        try {
            ArrayList<String> values = new ArrayList<String>();

            for (Field f : ClipMasks.class.getDeclaredFields()) {
                if (f.getType().isPrimitive()) {
                    int val = f.getInt(null);
                    if ((val & lookup) == val) {
                        values.add(f.getName() + " from " + String.format("0x%X", val));
                        //lookup = lookup & ~val;
                    }
                }
            }
            return values.toArray(new String[values.size()]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}