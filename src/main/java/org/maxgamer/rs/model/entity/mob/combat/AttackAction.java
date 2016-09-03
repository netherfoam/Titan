package org.maxgamer.rs.model.entity.mob.combat;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.action.CombatFollow;
import org.maxgamer.rs.model.entity.mob.Mob;

public class AttackAction extends Action {
    private Mob target;

    public AttackAction(Mob mob, Mob target) {
        super(mob);
        this.target = target;
    }

    public Mob getTarget() {
        if (this.target != null) {
            if (this.target.isDead()) {
                this.target = null;
            } else if (this.target.isAttackable(getOwner()) == false) {
                this.target = null;
            } else if (this.target.isVisible(getOwner()) == false) {
                this.target = null;
            } else if (this.target.getLocation().z != getOwner().getLocation().z) {
                this.target = null;
            } else if (this.target.getLocation().near(getOwner().getLocation(), 25) == false) {
                this.target = null;
            }
        }
        return this.target;
    }

    public Attack getAttack() {
        return getOwner().nextAttack();
    }

    private boolean inRange() throws SuspendExecution {
        int range = getAttack().getMaxDistance();

        if (range == 1 || getTarget().getLocation().isDiagonal(getOwner().getLocation())) {
            return true; //Allow diagonal combat
        }

        if (getTarget().getLocation().near(getOwner().getLocation(), range) == false) {
            return false; //Couldn't reach the target yet. Continue the CombatFollow.
        }
        return true;
    }

    @Override
    public void run() throws SuspendExecution {
        long warmup = Core.getServer().getTicks() - getOwner().getDamage().getLastAttack();

        while (++warmup < getAttack().getWarmupTicks()) {
            if (getOwner().getActions().after(this) instanceof CombatFollow == false) {
                getOwner().face(getTarget() == null ? null : getTarget().getLocation());
                return;
            }
            //assert getOwner().getActions().after(this) instanceof CombatFollow || getOwner().getActions().after(this) == null : "AttackAction tried to yield to CombatFollow but instead tried to yield to " + getOwner().getActions().after(this);
            this.yield();
            wait(1);

            if (getTarget() == null) {
                return;
            }

            //If we're still warming up, allow the mob to move closer if necessary
            if (inRange() == false) {
                if (getOwner().getActions().after(this) instanceof CombatFollow == false) {
                    getOwner().face(getTarget() == null ? null : getTarget().getLocation());
                    return;
                }
                this.yield(); //Assumably, yield to CombatFollow
                wait(1);
                continue;
            }

            getOwner().face(getTarget());
        }

        //Now we have to wait until we're close enough!
        while (getTarget() != null && inRange() == false) {
            if (getOwner().getActions().after(this) instanceof CombatFollow == false) {
                getOwner().face(getTarget() == null ? null : getTarget().getLocation());
                return;
            }
            this.yield(); //Assumably, yield to CombatFollow
            wait(1);
        }

        Mob target = getTarget();
        if (target != null && getAttack().run(target)) {
            getOwner().getDamage().setLastAttack(Core.getServer().getTicks());
            target.getDamage().setLastAttacker(getOwner());
        }
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

}
