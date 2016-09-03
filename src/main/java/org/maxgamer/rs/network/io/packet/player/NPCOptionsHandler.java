package org.maxgamer.rs.network.io.packet.player;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.npc.NPCType;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.repository.NPCTypeRepository;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author netherfoam
 */
public class NPCOptionsHandler implements PacketProcessor<Player> {
    // Not sure what order these should be in.
    public static final int FIRST_OPTION = 28;
    public static final int SECOND_OPTION = 18;
    public static final int THIRD_OPTION = 33;
    public static final int FOURTH_OPTION = 31;
    public static final int FIFTH_OPTION = 72;
    public static final int EXAMINE = 5;

    @Override
    public void process(final Player player, RSIncomingPacket packet) throws Exception {
        final NPC target;
        int option = -1;
        int index;

        @SuppressWarnings("unused")
        boolean run = false; // If the user held CTRL while clicking, this will
        // be true.
        switch (packet.getOpcode()) {
            case FIRST_OPTION:
                // Usually attack
                run = packet.readByteS() == 1;
                index = packet.readLEShort() - 1;

                option = 0;
                break;
            case SECOND_OPTION:
                index = packet.readShort() - 1;
                run = packet.readByte() == 1;
                option = 1;
                break;
            case THIRD_OPTION:
                index = packet.readShort() - 1;
                run = packet.readByteA() == 1;
                option = 2;
                break;
            case FOURTH_OPTION:
                index = packet.readShort() - 1;
                run = packet.readByteS() == 1;
                option = 3;
                break;
            case FIFTH_OPTION:
                index = packet.readShort() - 1;
                run = packet.readByte() == 1;
                option = 4;
                break;
            case EXAMINE:
                int npcId = packet.readShort();
                try {
                    NPCType d = Core.getServer().getDatabase().getRepository(NPCTypeRepository.class).find(npcId);
                    player.sendMessage(d.getExamine());

                    // TODO DEBUG: Send the player the options they can click on the NPC
                    String[] options = new String[]{d.getOption(0), d.getOption(1), d.getOption(2), d.getOption(3), d.getOption(4)};
                    player.sendMessage("NPCID " + npcId + " Options: " + Arrays.toString(options) + ", Clicked " + option);
                    HashSet<NPC> nearby = player.getLocation().getNearby(NPC.class, 5);
                    NPC closest = null;
                    for (NPC npc : nearby) {
                        if (closest == null || closest.getLocation().distanceSq(player.getLocation()) > npc.getLocation().distanceSq(player.getLocation())) {
                            closest = npc;
                        }
                    }
                    if (closest != null) {
                        player.sendMessage("Nearest: " + closest);
                        player.sendMessage("Actions: " + closest.getActions());
                        player.sendMessage("Actions.isEmpty(): " + closest.getActions().isEmpty());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return; // Return, not break
            default:
                return;
        }
        target = Core.getServer().getNPCs().get(index);
        if (target == null) {
            player.getCheats().log(10, "Player attempted to interact with a NULL NPC");
            return;
        }

        final String s = target.getOption(option);
        if (s == null) {
            player.getCheats().log(20, "Player attempted to use a NPC option that doesn't exist.  Target: " + target + ", option: " + option);
            return;
        }

        if (player.getProtocol().isVisible(target) == false) {
            player.getCheats().log(30, "Player attempted to interact with an NPC that wasn't on screen. Target: " + target + ", given index: " + index + " + 1");
            return;
        }

        if (s.equalsIgnoreCase("Attack") == false && player.getLocation().distanceSq(target.getLocation()) > 1) {
            AStar finder = new AStar(5);
            Path p = finder.findPath(player, target.getLocation(), 1);
            WalkAction walk = new WalkAction(player, p) {
                @Override
                public void run() throws SuspendExecution {
                    super.run();
                    player.use(target, s);
                }
            };
            player.getActions().clear();
            player.getActions().queue(walk);
        } else {
            player.use(target, s);
        }

    }
}