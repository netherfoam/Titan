package org.maxgamer.rs.model.entity.mob;

import org.maxgamer.rs.cache.Archive;
import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.cache.format.AnimationDefinition;
import org.maxgamer.rs.core.Core;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author netherfoam
 */
public class Animation {
    private static HashMap<Integer, AnimationDefinition> definitions = new HashMap<>();

    private AnimationDefinition def;
    private int id;
    private int delay;

    public Animation(int id) {
        this(id, 0);
    }

    public Animation(int id, int delay) {
        if (id > 0xFFFF) throw new IllegalArgumentException("Invalid animation ID " + id);
        if (delay > 0xFF) throw new IllegalArgumentException("Invalid animation delay " + delay);
        this.id = id;
        this.delay = delay;
        //Attempt to load the definition from the cache

        if (id >= 0) {
            this.def = definitions.get(id);
            if (this.def == null) {
                try {
                    Archive a = Core.getCache().getArchive(IDX.ANIMATIONS, id >> 7);
                    this.def = new AnimationDefinition(a.get(id & 0x7F));
                    definitions.put(id, def);
                } catch (IOException e) {
                    throw new IllegalArgumentException("No such animation found in the cache: " + id);
                }
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getDelay() {
        return delay;
    }

    public int getDuration(boolean ignoreLastFrame) {
        if (def != null) return def.getDuration(ignoreLastFrame);
        return 0;
    }
}