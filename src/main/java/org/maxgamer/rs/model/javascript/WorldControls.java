package org.maxgamer.rs.model.javascript;

import co.paralleluniverse.fibers.Fiber;
import org.maxgamer.rs.core.tick.Tickable;
import org.maxgamer.rs.model.entity.mob.Animation;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * @author netherfoam
 */
public class WorldControls {
    private static <E> E cast(Object[] args, int index, Class<E> type) {
        return cast(args, index, type, null);
    }

    private static <E> E cast(Object[] args, int index, Class<E> type, E fallback) {
        // Safely handle omitted values
        if(index >= args.length) return fallback;

        Object arg = args[index];

        if(arg instanceof ScriptableObject) {
            return (E) Context.jsToJava(arg, type);
        }

        return type.cast(arg);
    }

    /**
     * Animates the given mob and blocks until the animation is complete
     * @param cx
     * @param thisObj
     * @param args the mob, animation id and priority (optional)
     * @param funObj
     */
    public static void animate(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        Mob mob = cast(args, 0, Mob.class);
        Number animation = cast(args, 1, Number.class);
        Number priority = cast(args, 2, Number.class);

        Animation a = new Animation(animation.intValue());

        if(priority != null) {
            mob.animate(a, priority.intValue());
        } else {
            mob.animate(a);
        }

        final Fiber<?> currentFiber = Fiber.currentFiber();

        new Tickable() {
            @Override
            public void tick() {
                currentFiber.unpark();
            }
        }.queue(a.getDuration(true));

        throw cx.captureContinuation();
    }
}
