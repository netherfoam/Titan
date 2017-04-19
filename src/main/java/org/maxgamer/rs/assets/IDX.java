package org.maxgamer.rs.assets;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * @author netherfoam
 */
public class IDX {
    /**
     * These have been taken from `Discarded2x over at
     * http://www.rune-server.org
     * /runescape-development/rs-503-client-server/downloads
     * /312510-openrs-cache-library-6.html
     */

    public static final int SKELETONS = 0;
    public static final int SKINS = 1;
    /**
     * Also contains quests?
     */
    public static final int GRAPHIC_ACCESSORIES = 2;
    public static final int INTERFACES = 3;
    /**
     * Like weapon on armour attacks, or crafting or death noises
     */
    public static final int SOUND_EFFECTS = 4;
    public static final int LANDSCAPES = 5;
    public static final int MUSIC = 6;
    public static final int MODELS = 7;
    public static final int SPRITES = 8;
    public static final int TEXTURES = 9;
    public static final int HUFFMAN = 10;
    public static final int MUSIC2 = 11;
    public static final int INTERFACE_SCRIPTS = 12;
    public static final int FONTS = 13;
    public static final int SOUND_EFFECTS_2 = 14;
    public static final int SOUND_EFFECTS_3 = 15;
    public static final int OBJECTS = 16;
    public static final int CLIENTSCRIPT_SETTINGS = 17;
    public static final int NPCS = 18;
    public static final int ITEMS = 19;
    public static final int ANIMATIONS = 20;
    public static final int GRAPHICS = 21;
    public static final int CONFIGS = 22;
    public static final int WORLD_MAP = 23;
    public static final int DEFAULTS = 28;
    public static final int THEORA = 35;
    public static final int VORBIS = 36;

    private static HashMap<String, Integer> names = new HashMap<>();

    static {
        for (Field f : IDX.class.getFields()) {
            try {
                if (int.class.isInstance(f.get(null))) {
                    names.put(f.getName(), (Integer) f.get(null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private IDX() {
        //Private constructor
    }

    /**
     * Fetches the string name of the given direction.
     *
     * @param d the direction
     * @return the name, upper case with underscores.
     */
    public static String getName(Integer idx) {
        for (Entry<String, Integer> e : names.entrySet()) {
            if (e.getValue().equals(idx)) return e.getKey();
        }
        throw new IllegalArgumentException();
    }
}