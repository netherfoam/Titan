package org.maxgamer.rs.model.javascript;

import co.paralleluniverse.fibers.Fiber;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.core.server.ServerTicker;
import org.maxgamer.rs.util.Log;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.*;

/**
 * @author netherfoam
 */
public class StaticFunctions {
    private static final Random random = new Random();

    public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        for (int i = 0; i < args.length; i++) {
            Log.info(Context.toString(args[i]));
        }
    }

    public static void wait(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        int ticks = ((Number) args[0]).intValue();
        final JavaScriptCallFiber fiber = (JavaScriptCallFiber) Fiber.currentFiber();

        Core.submit(new Runnable() {
            @Override
            public void run() {
                fiber.resume(null);
            }
        }, ServerTicker.getTickDuration() * ticks, false);

        throw Context.getCurrentContext().captureContinuation();
    }

    public static Object random(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        if (args.length == 0) {
            // No args? Here's a random number
            return random.nextInt();
        } else if (args.length == 1) {
            Object arg = args[0];

            if (arg instanceof ScriptableObject) {
                arg = Context.jsToJava(arg, List.class);
            }

            if (arg instanceof Collection) {
                List<?> options = new ArrayList<>((Collection<?>) arg);
                return options.get(random.nextInt(options.size()));
            }
        }

        throw new IllegalArgumentException("Can't compute random from " + Arrays.toString(args));
    }

    public static void pause(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        throw Context.getCurrentContext().captureContinuation();
    }
}
