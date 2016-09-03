package org.maxgamer.rs.core.tick;

import org.maxgamer.rs.core.Core;

/**
 * Represents an interface which can have a tick() method called. This is used
 * for updates and periodic checks, such as movement updates for mobs. Ticks
 * will be roughly 600ms apart.
 * <p>
 * An object must individually subscribe to ticks by notifying the core class,
 * Core.submitTickable(Tickable t).
 *
 * @author netherfoam
 */
public abstract class Tickable {
    private long lastTick = -1;
    private RunRequest req;
    public Tickable() {

    }

    public boolean isQueued() {
        return req != null && req.cancel == false;
    }

    public void queue(int delay) {
        if (isQueued()) {
            throw new IllegalStateException("Cannot queue() " + this.getClass().getSimpleName() + " because it is already queued.");
        }

        req = new RunRequest(this);
        Core.getServer().getTicker().submit(delay, req);
    }

    public void cancel() {
        if (req == null) return;
        req.cancel = true;
        req = null;
    }

    public final void run() {
        long now = Core.getServer().getTicks();
        if (now == lastTick) {
            throw new RuntimeException("May not run Tickable, as the Tickable has already been ticked for this tick.");
        }

        this.lastTick = now;
        try {
            tick();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Ticks this entity.
     */
    public abstract void tick();

    private static class RunRequest implements Runnable {
        private boolean cancel = false;
        private Tickable tick;

        private RunRequest(Tickable tick) {
            this.tick = tick;
        }

        @Override
        public void run() {
            if (cancel) return;
            else {
                tick.req = null;
                tick.run();
            }
        }
    }
}