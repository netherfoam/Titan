package org.maxgamer.rs.network.io.rawhandler;

import org.maxgamer.rs.network.LobbyPlayer;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;
import org.maxgamer.rs.util.Log;

import java.io.IOException;
import java.nio.BufferUnderflowException;

/**
 * @author netherfoam
 */
public class LobbyHandler extends RawHandler {
    private LobbyPlayer c;

    public LobbyHandler(Session s, LobbyPlayer c) {
        super(s);
        this.c = c;
    }

    @Override
    public void handle(RSByteBuffer b) {
        RSIncomingPacket p;
        try {
            p = RSIncomingPacket.parse(b);
        } catch (IOException e) {
            throw new BufferUnderflowException();
        }

        if (c.getProtocol().getPacketManager().handle(c, p) == false) {
            Log.debug("Unhandled lobby OPCode: " + p.getOpcode() + ": " + p.toString());
        }
    }
}