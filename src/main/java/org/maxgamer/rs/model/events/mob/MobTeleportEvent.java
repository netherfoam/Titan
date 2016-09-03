package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Location;

public class MobTeleportEvent extends MobEvent implements Cancellable {
    private final Location from;
    private Location to;
    private boolean cancel = false;
    private TeleportCause cause;
    public MobTeleportEvent(Mob mob, Location fromLocation, Location toLocation, TeleportCause cause) {
        super(mob);
        this.from = fromLocation;
        this.to = toLocation;
        this.cause = cause;
    }

    public TeleportCause getCause() {
        return cause;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        if (to == null) {
            throw new NullPointerException("Destination location may not be null!");
        }

        if (to.getMap() == null) {
            throw new NullPointerException("Destination map may not be null");
        }

        this.to = to;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public static enum TeleportCause {
        SPELL,
        SERVER
    }

}
