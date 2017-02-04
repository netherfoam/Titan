package org.maxgamer.rs.network.io.packet.player;

import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Rights;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.Position;
import org.maxgamer.rs.model.map.path.AStar;
import org.maxgamer.rs.network.io.packet.PacketProcessor;
import org.maxgamer.rs.network.io.packet.RSIncomingPacket;
import org.maxgamer.rs.util.Log;

/**
 * @author netherfoam
 */
public class MoveHandler implements PacketProcessor<Player> {
    /**
     * Maximum number of tiles a player can attempt to walk before being
     * declared a cheat
     */
    private static final int MAX_TILES = 50;

    @Override
    public void process(Player p, RSIncomingPacket in) throws Exception {
        if (p.isDestroyed()) {
            //This is very odd, but got triggered.
            Log.warning("Player " + p + " attempted to move but is destroyed?");
            return;
        }

        int x = in.readShort();
        int y = in.readShort();

        int dx = Math.abs(p.getLocation().x - x);
        int dy = Math.abs(p.getLocation().y - y);

        if (dx > MAX_TILES || dy > MAX_TILES) {
            p.getCheats().log(7, "Player attempted to walk to a position too far away (" + dx + ", " + dy + "). Max tiles is " + MAX_TILES);
            return;
        }

        boolean run = in.readByte() != 0;
        if (!run || p.getRights() < Rights.ADMIN) { //User holds CTRL + Click to "run once"
            if (p.getFacing() != null) {
                p.setFacing(null);
            }

            Position dest = new Position(x, y);
            p.move(dest, new AStar(20));
        } else if (p.getRights() >= Rights.ADMIN) {
            //CTRL + Click means teleport hack for us.
            p.getActions().clear();
            p.teleport(new Location(p.getLocation().getMap(), x, y, p.getLocation().z));
        }
    }
}
