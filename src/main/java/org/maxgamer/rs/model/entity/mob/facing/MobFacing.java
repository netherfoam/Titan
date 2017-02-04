package org.maxgamer.rs.model.entity.mob.facing;

import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.map.Position;

import java.lang.ref.WeakReference;

/**
 * @author netherfoam
 */
public class MobFacing extends Facing {
    private final WeakReference<Mob> target;

    public MobFacing(Mob target) {
        if (target == null) {
            throw new NullPointerException("Target may not be null");
        }
        this.target = new WeakReference<>(target);
    }

    public Mob getTarget() {
        if (target == null) {
            return null;
        }

        return target.get(); //May still be null
    }

    @Override
    public Position getPosition() {
        Mob t = getTarget();
        if (t == null || t.isDestroyed()) return null;
        return t.getCenter();
    }
}