package org.maxgamer.rs.model.events.mob;

import org.maxgamer.rs.event.Cancellable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Position;

/**
 * @author netherfoam
 */
public class MobMoveEvent extends MobEvent implements Cancellable {
    private boolean cancel;
    private Position to;
    private boolean run;

    public MobMoveEvent(Mob m, Position to, boolean run) {
        super(m);

        if (m == null || to == null) {
            throw new NullPointerException();
        }

        this.to = to;
        this.run = run;
    }

    public Position getTo() {
        return to;
    }

    public Position getFrom() {
        return getMob().getLocation();
    }

    public boolean isRunning() {
        return run;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}