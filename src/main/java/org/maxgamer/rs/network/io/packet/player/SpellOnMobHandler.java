package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.interfaces.Interface;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.util.Log;

/**
 * @author netherfoam
 */
public class SpellOnMobHandler implements PacketProcessor<Player> {
    public static final int ON_NPC = 14;
    public static final int ON_PERSONA = 78;

    @Override
    public void process(Player p, RSIncomingPacket in) throws Exception {
        Log.debug("SpellOnMobHandler");
        int buttonId;
        int interfaceId;
        int slot;
        Mob target;
        boolean running;
        int itemId;

        switch (in.getOpcode()) {
            case ON_NPC:
                buttonId = in.readShort();
                interfaceId = in.readShort();
                slot = in.readByte();
                in.readByte(); //Unknown

                target = Core.getServer().getNPCs().get((in.readShort() & 0xFFFF) - 1);
                running = in.readByteS() == 1;
                itemId = in.readLEShort();

                break;
            case ON_PERSONA:
                int index = (in.readLEShortA() & 0xFFFF) - 1;
                target = Core.getServer().getPersonas().get(index);
                in.readByte();
                slot = in.readByte();

                buttonId = in.readLEShort();
                interfaceId = in.readLEShort();
                running = in.readByteS() == 1;
                itemId = in.readShortA();
                break;
            default:
                throw new RuntimeException("Unhandled target type for opcode " + in.getOpcode());
        }

        if (target == null) {
            p.getCheats().log(5, "Player attempted to interface-use on a target which does not exist. ");
            return;
        }

        if (target.isDestroyed()) {
            p.getCheats().log(5, "Player attempted to interface-use on a target which has been destroyed");
            return;
        }

        if (p.getProtocol().getViewport().overlaps(target.getLocation()) == false) {
            p.getCheats().log(10, "Player attempted to interface-use on a target which is not inside their viewport");
            return;
        }

        Interface iface = p.getWindow().getInterface(interfaceId);
        if (iface == null) {
            p.getCheats().log(2, "Player attempted to interface-use via an interface that they do not have open. Interface ID: " + interfaceId);
            return;
        }

        iface.onClick(target, buttonId, slot, itemId, running);
    }
}