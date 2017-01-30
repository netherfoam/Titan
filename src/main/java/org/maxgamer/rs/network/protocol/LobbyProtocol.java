package org.maxgamer.rs.network.protocol;

import org.maxgamer.rs.network.AuthResult;
import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.network.io.packet.PacketManager;

/**
 * @author netherfoam
 */
public abstract class LobbyProtocol extends ProtocolHandler<LobbyPlayer> {

    public LobbyProtocol(LobbyPlayer p) {
        super(p);
    }

    public abstract PacketManager<LobbyPlayer> getPacketManager();

    public abstract void sendAuth(AuthResult result, String lastIp, long lastSeen);
    
    public abstract void sendWorldData();
}