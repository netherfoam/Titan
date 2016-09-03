package org.maxgamer.rs.model.action;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;

/**
 * @author netherfoam
 */
public class AnimateAction extends Action {
    private boolean cancellable;
    private Animation anim;

    public AnimateAction(Mob mob, int anim, boolean cancellable) {
        super(mob);
        this.anim = new Animation(anim);
        this.cancellable = cancellable;
    }

    @Override
    protected void run() throws SuspendExecution {
        mob.getUpdateMask().setAnimation(anim, 20);
        wait(this.anim.getDuration(false));
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean isCancellable() {
        return cancellable;
    }
}