package org.maxgamer.rs.network.protocol;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;

/**
 * @author netherfoam
 */
public abstract class GameProtocol extends ProtocolHandler<Player> {
	public GameProtocol(Player p) {
		super(p);
	}
	
	public abstract void sendRunEnergy(int val);
}