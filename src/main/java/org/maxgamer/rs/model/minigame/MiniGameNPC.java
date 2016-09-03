package org.maxgamer.rs.model.minigame;

import org.maxgamer.rs.core.server.WorldFullException;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.structure.configs.ConfigSection;

/**
 * @param <M> the type of minigame this npc is associated with
 * @author Albert Beaupre
 */
public class MiniGameNPC<M extends MiniGame> extends NPC {

    /**
     * The type of {@code MiniGame} this {@code MiniGameNPC} is associated with.
     */
    protected final M minigame;
    private final Tickable tickable;
    /**
     * This holds a various amount of configurations for this
     * {@code MiniGameNPC}.
     */
    protected ConfigSection configs;

    /**
     * Constructs a new {@code MiniGameNPC} from the specified arguments.
     *
     * @param minigame the minigame this npc is associated with
     * @param defId    the id of this npc
     * @param l        the location to set the npc at
     * @throws WorldFullException
     */
    public MiniGameNPC(M minigame, int defId, Location l) throws WorldFullException {
        super(defId, new Location(minigame.getAssociatedMap(), l.x, l.y, l.z));
        this.minigame = minigame;
        this.configs = new ConfigSection();
        this.tickable = new Tickable() {
            @Override
            public void tick() {
                int delay = MiniGameNPC.this.tick();
                if (delay != -1) {
                    if (!isQueued()) queue(delay);
                } else {
                    cancel();
                }
            }
        };
    }

    /**
     * Constructs a new {@code MiniGameNPC} from the specified arguments.
     *
     * @param minigame the minigame this npc is associated with
     * @param defId    the id of this npc
     * @throws WorldFullException
     */
    public MiniGameNPC(M minigame, int defId) throws WorldFullException {
        this(minigame, defId, null);
    }

    /**
     * This method is executed every game tick * the value returned. If the
     * return value is -1, this {@code PestControlNPC} will stop ticking. The
     * default return value is set to -1.
     *
     * @return the tick delay before this method is executed again
     */
    public int tick() {
        return -1;
    }

    /**
     * Transforms this {@code MiniGameNPC} into a new {@code MiniGameNPC} and
     * returns the transformed npc.
     *
     * @param npcId the id of the npc to transform into
     */
    public MiniGameNPC<M> transform(int npcId) {
        try {
            MiniGameNPC<M> replacement = new MiniGameNPC<M>(minigame, npcId, getLocation());
            replacement.setSpawn(getLocation().add(0, 0));
            replacement.configs = configs;
            this.destroy();
            minigame.removeMob(this);
            minigame.addMob(replacement);
            replacement.respawn();
            return replacement;
        } catch (WorldFullException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        tickable.cancel();
    }

    @Override
    public Mob respawn() {
        if (!tickable.isQueued()) tickable.queue(1);
        return super.respawn();
    }

    public ConfigSection getConfigs() {
        return configs;
    }

}
