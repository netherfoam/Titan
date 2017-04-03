package org.maxgamer.rs.network.io.packet.player;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.action.WalkAction;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.object.GameObject;
import org.maxgamer.rs.assets.formats.GameObjectFormat;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.util.Log;

/**
 * @author netherfoam
 */
public class GameObjectHandler implements PacketProcessor<Player> {
    public static final int FIRST_OPTION = 76;
    public static final int SECOND_OPTION = 55;
    public static final int THIRD_OPTION = 60;
    public static final int FOURTH_OPTION = 81;
    public static final int FIFTH_OPTION = 25;
    public static final int EXAMINE = 48;

    @Override
    public void process(final Player p, RSIncomingPacket in) throws Exception {
        int id;
        int x;
        int y;
        @SuppressWarnings("unused")
        boolean run;
        int option = -1;

        switch (in.getOpcode()) {
            case FIRST_OPTION:
                id = in.readLEShort();
                run = in.readByte() != 0;
                x = in.readLEShortA();
                y = in.readShortA();
                option = 0;
                break;
            case SECOND_OPTION:
                y = in.readShort();
                id = in.readLEShort();
                x = in.readShort();
                run = in.readByteS() != 0;
                option = 1;
                break;
            case THIRD_OPTION:
                x = in.readShortA();
                id = in.readLEShortA();
                run = (in.readByte() + 128) != 0;
                y = in.readLEShortA();

                option = 2;
                break;
            case FOURTH_OPTION:
                x = in.readLEShort();
                y = in.readLEShort();
                id = in.readShortA();
                run = (in.readByte() + 128) != 0;
                option = 3;
                break;
            case FIFTH_OPTION:
                x = in.readShortA();
                id = in.readLEShortA();
                run = in.readByteA() != 0;
                y = in.readLEShortA();
                option = 4;
                break;
            case EXAMINE:
                id = in.readShort();
                id = id & 0xFFFF;
                try {
                    GameObjectFormat def = GameObject.getDefinition(id);

                    if (p.getRights() >= Rights.MOD) {
                        p.sendMessage("ID: " + id);
                    }

                    if (def == null) {
                        p.getCheats().log(5, "Player attempted to examine a NULL gameobject.");
                        return;
                    }
                    p.sendMessage(def.getExamine());
                } catch (Exception e) {
                    p.getCheats().log(5, "Player attempted to examine a bad gameobject.");
                    return;
                }

                return;
            default:
                return;
        }

        if (p.getRights() >= Rights.MOD) {
            Log.debug("ID: " + id + ", X: " + x + ", Y: " + y);
        }
        id = id & 0xFFFF; // Signed
        final int opt = option;

        Location l = new Location(p.getLocation().getMap(), x, y, p.getLocation().z);
        for (final GameObject g : l.getNearby(GameObject.class, 0)) {
            if (g.getId() == id && !g.isHidden()) {
                String s = g.getDefiniton().getOption(option); // Becomes zero-based
                if (s == null) {
                    p.getCheats().log(10, "Player attempted to use a NULL option on a gameobject. Gameobject: " + g + ", option: " + option + "/5");
                    return;
                }

                if (s.isEmpty()) {
                    Log.info(p + " attempted to use option#" + opt + " on " + g + ". Unfortunately, I couldn't find the name of the option!");
                    return;
                }

                AStar finder = new AStar(20);
                Path path = finder.findPath(p, g);

                if (path.hasFailed()) {
                    return;
                }

                p.getActions().clear();
                if (!path.isEmpty()) {
                    WalkAction walk = new WalkAction(p, path) {
                        @Override
                        public void run() throws SuspendExecution {
                            super.run();

                            // Then use the object
                            p.use(g, g.getOptions()[opt]);
                        }
                    };
                    p.getActions().queue(walk);
                } else {
                    p.use(g, g.getOptions()[opt]);
                }

                return;
            }
        }

        p.getCheats().log(10, "Player attempted to use a NULL gameobject. ID: " + id + ", option: " + option + "/5");
    }
}