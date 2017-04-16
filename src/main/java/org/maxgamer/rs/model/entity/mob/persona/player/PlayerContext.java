package org.maxgamer.rs.model.entity.mob.persona.player;

import org.maxgamer.rs.model.entity.mob.persona.PersonaContext;

/**
 * @author netherfoam
 */
public class PlayerContext extends PersonaContext {
    private Player player;

    public PlayerContext(Player player) {
        this.player = player;
    }

    @Override
    public void set(int id, int value) {
        super.set(id, value);

        player.getProtocol().sendConfig(id, value);
    }

    @Override
    public void setBit(int bConfigId, int value) {
        super.setBit(bConfigId, value);

        player.getProtocol().sendBConfig(bConfigId, value);
    }
}
