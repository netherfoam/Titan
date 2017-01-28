package org.maxgamer.rs.model.action;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public abstract class BackgroundAction extends Action {
    /* Still initializing */
    private static final int INIT = 0;

    /* Secondary thread is processing */
    private static final int PROCESSING = 1;

    /* Secondary thread has finished, we're ready to rumble */
    private static final int CALCULATED = 2;

    private boolean cancelRequested;
    private volatile int state = INIT;

    public BackgroundAction(Mob mob) {
        super(mob);
    }

    @Override
    protected final void run() throws SuspendExecution {
        state = INIT;
        Core.submit(new Runnable() {
            @Override
            public void run() {
                state = PROCESSING;
                calculate();

                if (!isCancelRequested()) {
                    state = CALCULATED;
                }
            }
        }, true);

        while (state != CALCULATED) {
            wait(1);
        }

        while (!actuate()) {
            wait(1);
        }
    }

    public abstract void calculate();

    public abstract boolean actuate();

    @Override
    protected void onCancel() {
        cancelRequested = true;
    }

    public boolean isCancelRequested() {
        return cancelRequested;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}