package org.maxgamer.rs.model.action;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.combat.AttackAction;
import org.maxgamer.rs.model.map.Location;
import org.maxgamer.rs.model.map.path.Path;
import org.maxgamer.rs.model.map.path.PathFinder;
import org.maxgamer.rs.model.map.path.ProjectilePathFinder;

/**
 * A class to handle one mob following another.
 *
 * @author netherfoam
 */
public class CombatFollow extends Follow {
    private AttackAction attack;

    /**
     * Constructs a new Follow object.
     *
     * @param owner the mob who is following another
     * @throws NullPointerException if the owner is null
     */
    public CombatFollow(Mob owner, Mob target, PathFinder pather) {
        super(owner, target, pather);
        if (pather == null) throw new NullPointerException("Pather may not be null");
    }

    @Override
    public int getBreakDistance() {
        return 10 + getAttack().getAttack().getMaxDistance();
    }

    public AttackAction getAttack() {
        if (this.attack == null) {
            this.attack = new AttackAction(this.getOwner(), this.getTarget());
        }

        return this.attack;
    }

    @Override
    public boolean isSatisfied() {
        int prefDistance = this.getAttack().getAttack().getMaxDistance();
        if (prefDistance <= 0) {
            throw new RuntimeException("PrefDistance shouldn't be " + prefDistance);
        }

        if (!getOwner().getLocation().near(getTarget().getLocation(), prefDistance)) {
            return false;
        }

        Mob t = getTarget();
        Mob m = getOwner();
        PathFinder finder = new ProjectilePathFinder();
        Path path = finder.findPath(m.getLocation(), t.getLocation(), t.getLocation(), m.getSizeX(), m.getSizeY());

        return !path.hasFailed();
    }

    @Override
    public void onWait() {
        if (getOwner().getActions().isQueued(this) && !(getOwner().getActions().before(this) instanceof AttackAction)) {
            //Our action can be cancelled during the waiting period due to getTarget()
            //which may invalidate our target at any point.
            AttackAction a = getAttack();
            getOwner().getActions().insertBefore(this, a);
            this.attack = null;
        }
    }

    @Override
    public void onCancel() {
        super.onCancel();
        if (getTarget() != null) {
            Location face = getTarget().getLocation();

            getOwner().setTarget(null);
            getOwner().face(face);
        }
    }
}