package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.model.interfaces.impl.chat.IntInputInterface;
import org.maxgamer.rs.model.interfaces.impl.chat.StringInputInterface;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;

/**
 * @author netherfoam
 */
public class InputHandler implements PacketProcessor<Player> {
    public static final int STRING_OPTION_1 = 37;
    public static final int STRING_OPTION_2 = 63;
    public static final int INT_OPTION = 34;

    @Override
    public void process(Player p, RSIncomingPacket in) throws Exception {
        Interface interf;

        switch (in.getOpcode()) {
            case STRING_OPTION_1:
            case STRING_OPTION_2:
                interf = p.getWindow().getInterface(StringInputInterface.CHILD_ID);

                if (interf == null) {
                    p.getCheats().log(10, "Player attempted to input String to an interface they don't have access to");
                    return;
                }

                String s = in.readPJStr1();

                ((StringInputInterface) interf).onInput(s);
                p.getWindow().close(interf);

                break;

            case INT_OPTION:

                interf = p.getWindow().getInterface(IntInputInterface.CHILD_ID);

                if (interf == null) {
                    p.getCheats().log(10, "Player attempted to input String to an interface they don't have access to");
                    return;
                }
                // Client cannot input negative numbers
                ((IntInputInterface) interf).onInput(in.readInt() & 0xFFFFFFFF);

                // As a result of handling onInput() the interface may have been closed
                if (interf.isOpen()) p.getWindow().close(interf);
                break;
        }
    }

}