package org.maxgamer.rs.network.io.rawhandler;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.network.Session;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.network.io.stream.RSByteBuffer;
import org.maxgamer.rs.util.Log;

import java.nio.BufferUnderflowException;

/**
 * @author netherfoam
 */
public class GamePacketHandler extends RawHandler {
    private Player p;

    public GamePacketHandler(Session s, Player p) {
        super(s);
        this.p = p;
    }

    @Override
    public void handle(RSByteBuffer b) {
        while (!b.isEmpty()) {
            //Throws IOException if only a partial packet
            //has been received. This means the buffer is
            //reset until more data is available.
            final RSIncomingPacket in;
            try {
                in = RSIncomingPacket.parse(b);
            } catch (Exception e) {
                throw new BufferUnderflowException();
            }

            final PacketProcessor<Player> p = this.p.getProtocol().getPacketManager().getHandler(in.getOpcode());

            if (p == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Unhandled opcode: ").append(in.getOpcode()).append(", Size: ").append(in.available());
                if (!in.isEmpty() && in.available() <= 20) {
                    sb.append(String.format(", Data: 0x%X", in.readByte()));
                    while (!in.isEmpty()) {
                        sb.append(String.format(" %X", in.readByte()));
                    }
                }

                Log.debug(sb.toString());

                return;
            }

            Core.getServer().getThread().submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        p.process(GamePacketHandler.this.p, in);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.warning("Error handling opcode " + in.getOpcode());
                    }
                }
            });
        }
    }
}