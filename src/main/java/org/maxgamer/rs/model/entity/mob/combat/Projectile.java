package org.maxgamer.rs.model.entity.mob.combat;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.npc.NPC;
import org.maxgamer.rs.model.entity.mob.persona.Persona;
import org.maxgamer.rs.model.entity.mob.persona.player.Player;
import org.maxgamer.rs.model.entity.mob.persona.player.Viewport;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.network.io.packet.RSOutgoingPacket;

/**
 * @author netherfoam
 */
public class Projectile {
    private Location start;
    private Location end;
    private Mob target;
    private int projectileId;
    private int startHeight;
    private int endHeight;
    private int delay;
    private int duration;
    private int curve;

    private Projectile() {

    }

    public static Projectile create(int projectileId, Location start, Location end) {
        if (start.z != end.z || start.getMap() != end.getMap()) {
            throw new IllegalArgumentException("Projectile start and end must be on the same plane and map");
        }

        Projectile p = new Projectile();
        p.start = start;
        p.end = end;
        p.projectileId = projectileId;

        p.startHeight = 46;
        p.endHeight = 35;
        p.delay = 15;
        p.curve = 16;

        int dist = (int) Math.sqrt(start.distanceSq(end));
        p.duration = dist * 30 / 5 + p.delay;

        return p;
    }

    public static Projectile create(int projectileId, Location start, Mob target) {
        Projectile p = create(projectileId, start, target.getLocation());
        p.target = target;
        return p;
    }

    public void launch() {
        for (Player player : end.getNearby(Player.class, 16)) {
            RSOutgoingPacket bldr = new RSOutgoingPacket(15);
            Viewport viewport = player.getProtocol().getViewport();
            int chunkRadius = player.getViewDistance().getChunkRadius();
            int x = start.x - ((viewport.getCenter().x >> 3) - chunkRadius) * 8;
            int y = start.y - ((viewport.getCenter().y >> 3) - chunkRadius) * 8;

            bldr.writeByte(start.z);
            bldr.writeByteC(y >> 3);
            bldr.writeByteA(x >> 3);

            bldr.writeByte(13); // projectile subopcode
            x = start.x & 0x7;
            y = start.y & 0x7;
            bldr.writeByte((x & 0x7) << 3 | y & 0x7);
            bldr.writeByte((start.x - end.x) * -1);
            bldr.writeByte((start.y - end.y) * -1);

            //TODO: Check that target is in the viewer (player)'s viewport
            if (target == null) {
                bldr.writeShort(-1);
            } else {
                //This is peculiar and I'm not sure why it's necessary.
                if (target instanceof NPC) {
                    bldr.writeShort(target.getClientIndex() + 1);
                } else if (target instanceof Persona) {
                    //TODO: This appears to be wrong for players?
                    //bldr.writeShort(target.getClientIndex() + 1);
                    bldr.writeShort(-1); //So we write -1 instead (uses location)
                }

            }
            bldr.writeShort(projectileId);
            bldr.writeByte(startHeight);
            bldr.writeByte(endHeight);
            bldr.writeShort(delay);
            bldr.writeShort(duration);
            bldr.writeByte(curve);

            //Offset from where the projectile starts, multiplied by 64 (eg 64 = 1 tile?)
            bldr.writeShort(64 + 0 * 64);
            player.write(bldr);
        }
    }
}