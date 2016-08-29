package org.maxgamer.rs.model.action;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.ServerTicker;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.structure.timings.StopWatch;
import org.maxgamer.rs.util.Log;

/**
 * @author netherfoam
 */
public class ActionFiber extends Fiber<Void> {
    private static final long serialVersionUID = 1842342854418180882L;

    /**
     * There's a bug in Quasar, which prevents us from calling Fiber.park() when inside a subclass of Fiber,
     * such as this class. This method rectifies that. The symptom of the bug is that, when Fiber.park()
     * is called, no message is generated and park() simply fails to park the fiber.
     *
     * @throws SuspendExecution
     */
    public static void park() throws SuspendExecution {
        Fiber.park();
    }

    private Action action;
    private StopWatch watch;

    public ActionFiber(Action action) {
        super(action.toString() + "-fiber", Core.getServer().getThread().getFiberScheduler());
        this.action = action;
        this.watch = Core.getTimings().start(getAction().getClass().getName());
        this.watch.pause();
    }

    public Action getAction() {
        return action;
    }

    public Mob getOwner() {
        return getAction().getOwner();
    }

    @Override
    public Void run() throws SuspendExecution {
        this.watch.resume();
        try {
            getAction().run();
        }
        catch (Throwable t) {
            Log.warning("There was an Exception thrown while running an Action. Details:");
            Log.warning("Mob: " + getOwner() + ", Action: " + getAction());
            t.printStackTrace();
        }
        if(!watch.isStopped()) {
            watch.stop();
        }

        Action next = getOwner().getActions().after(getAction());

        //Notify the action queue this action has ended
        getOwner().getActions().end(getAction());

        if(next != null) {
            // We finished without pausing, pass the turn on to the next Action that's queued
            next.tick();
        }

        return null;
    }

    @Override
    public void onResume() throws InterruptedException, SuspendExecution {
        super.onResume();

        if(this.watch.isPaused()) {
            this.watch.resume();
        }
    }

    @Override
    public void onParked() {
        if(getOwner().getActions().isEmpty() == false) {
            if(getOwner().getActions().isQueued() == false) {
                // We're part way through an Action, so we want to continue it when possible.
                getOwner().getActions().queue(ServerTicker.getTickDuration());
            }
            this.watch.pause();
        }
        else {
            this.cancel(true);
            if(!this.watch.isStopped()) {
                this.watch.stop();
            }
        }

        super.onParked();
    }
}
