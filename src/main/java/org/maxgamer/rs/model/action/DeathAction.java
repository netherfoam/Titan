package org.maxgamer.rs.model.action;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.events.mob.MobDeathEvent;
import org.maxgamer.rs.model.map.Location;

/**
 * @author netherfoam
 */
public class DeathAction extends Action {
    private Animation anim;

    public DeathAction(Mob mob) {
        super(mob);
        if (getOwner().getCombatStats().getDeathAnimation() >= 0) {
            anim = new Animation(getOwner().getCombatStats().getDeathAnimation());
        }
    }

    @Override
    protected void run() throws SuspendExecution {
        getOwner().animate(anim, 50);

        wait(anim == null ? 3 : anim.getDuration(true));

        MobDeathEvent e = new MobDeathEvent(getOwner());
        e.call();
        getOwner().onDeath();
        getOwner().hide();

        wait(getOwner().getRespawnTicks());

        /* Teleport the mob back to their spawn, if one exists */
        Location spawn = e.getSpawn();
        if (spawn != null) {
            /* A mob could potentially have no spawn point defined */
            getOwner().teleport(e.getSpawn());
        }

        getOwner().respawn();
    }

    @Override
    protected void onCancel() {
        //Difficult task to cancel, really.
        getOwner().setHealth(1);
        getOwner().getUpdateMask().setAnimation(new Animation(-1), 50);
    }

    @Override
    protected boolean isCancellable() {
        return false; //We really don't want this one cancelled.
    }
}