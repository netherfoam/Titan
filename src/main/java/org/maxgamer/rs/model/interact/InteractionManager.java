package org.maxgamer.rs.model.interact;

import co.paralleluniverse.fibers.SuspendExecution;
import org.maxgamer.rs.model.action.Action;
import org.maxgamer.rs.model.entity.Entity;
import org.maxgamer.rs.model.entity.Interactable;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.model.entity.mob.facing.Facing;
import org.maxgamer.rs.model.interact.use.Use;
import org.maxgamer.rs.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The public InteractionManager API. This allows developers to write code that is executed inside of Actions using
 * a convenient @Interact annotation.
 *
 * @author netherfoam
 */
public class InteractionManager {
    /**
     * A list of all of the handlers that are registered with us.
     */
    private ArrayList<InteractionHandlerMethod> handlers = new ArrayList<>();

    /**
     * Constructs a new InteractionManager
     */
    public InteractionManager() {
        this.register(new JavaScriptInteractHandler());
    }

    /**
     * Clears the given mob's actions, and queues a new Action that will execute the required InteractionHandler methods
     * applicable for the arguments. The arguments do not have to be in a particular order unless there are duplicates
     * of types.  For example, run(Player, ItemStack, [int, String]) would call the method
     * public void run(Player p, ItemStack i, String option, int id), or
     * public void run(Player p, ItemStack i, int id, String option).
     *
     * @param source the mob that is interacting
     * @param target the target that is being interacted with
     * @param usage  the arguments for the interaction.
     */
    public void interact(final Mob source, final Interactable target, final Use usage) {
        // TODO: There's got to be a way of adding @Interact(instant=true|false), so that the server appears more responsive

        if (target instanceof Entity) {
            if (((Entity) target).isDestroyed()) {
                // A sanity check to ensure that nobody makes interactions with removed entities
                throw new IllegalArgumentException("Attempted to interact " + source + " with " + target + " usage is " + usage.toString() + ", but target is destroyed.");
            } else {
                source.setFacing(Facing.face((Entity) target));
            }
        }

        // Invoke this inside an Action, so that it may throw SuspendExecution
        source.getActions().clear();
        source.getActions().queue(new Action(source) {
            private InteractionHandlerMethod method;

            @Override
            protected void run() throws SuspendExecution {
                ArrayList<InteractionHandlerMethod> successes = new ArrayList<>(1);

                if (target instanceof Entity && ((Entity) target).isDestroyed()) {
                    // A sanity check to ensure that nobody makes interactions with removed entities
                    // This may happen if our action took too long to run, or we were beaten to it.
                    // We don't raise an exception, because it could have been valid at the time of request.
                    return;
                }

                // We iterate over a copy in-case another handler is added
                for (InteractionHandlerMethod h : handlers) {
                    if (h.isDebug()) {
                        Log.info("Trying " + h.getMethod().getName() + " from " + h.getHandler().getClass());
                    }
                    try {
                        this.method = h;
                        // We try and run the interaction
                        h.run(source, target, usage);
                        // If we're successful, great, the interaction was handled
                        // but, we cannot return in-case anyone else wants to handle the event
                        successes.add(h);
                    } catch (NotHandledException e) {
                        if (h.isDebug()) {
                            Log.info(h.toString() + " refused to handle interaction between " + source + " => " + target + " with usage " + usage + ".");
                        }
                        // That handler didn't want to handle that interaction, so we try again with any others!
                    } finally {
                        this.method = null;
                    }
                }

                if (successes.size() > 1) {
                    Log.warning("There were multiple handlers that accepted the interaction between " + source + " => " + target + " with usage " + usage);
                    Log.warning("They were " + successes.toString() + ". This would indicate that one of them should throw a NotHandledException.");
                } else if (successes.isEmpty()) {
                    Log.debug("Unhandled interaction between " + source + " => " + target + " with usage " + usage + ".");
                }
            }

            @Override
            protected void onCancel() {

            }

            @Override
            protected boolean isCancellable() {
                return method == null || method.isCancellable();
            }
        });
    }

    /**
     * Register the given {@link InteractionHandler} with this {@link InteractionManager}. Any methods with @Interact will be invoked if possible,
     * when an interaction is requested.
     *
     * @param handler the handler
     */
    public void register(InteractionHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Handler may not be null");
        }

        boolean foundAny = false;

        for (Method m : handler.getClass().getDeclaredMethods()) {
            Interact annot = m.getAnnotation(Interact.class);
            if (annot == null) continue;
            foundAny = true;

            if ((m.getModifiers() & Modifier.PUBLIC) == 0) {
                Log.warning("Class " + handler + " has annotation @Interact on method " + m.getName() + "(), but is not public. It should be public, or have no @Interact annotation.");
                continue;
            }

            if (!m.isAccessible()) {
                // This could occur if the method is in an inner class
                m.setAccessible(true);
            }

            boolean throwsNotHandled = false;
            for (Class<?> exception : m.getExceptionTypes()) {
                if (NotHandledException.class.isAssignableFrom(exception)) {
                    throwsNotHandled = true;
                    break;
                }
            }

            InteractionHandlerMethod method = new InteractionHandlerMethod(handler, m, annot.debug(), annot.cancellable());

            if (!throwsNotHandled) {
                Log.warning("The method " + method + " should raise a NotHandledException instead of simply returning from the method call. Please do so, or this may lead to odd behaviour");
            }

            this.handlers.add(method);
        }

        if (!foundAny) {
            Log.warning("Class " + handler.getClass() + " has no declared methods with @Interact annotation, but implements InteractionHandler.");
            Log.warning("Did you forget to register a handler with @Interact?");
        }
    }

    /**
     * Unregisters the given {@link InteractionHandler}, so that it will no longer be requested to process any Interactions
     *
     * @param handler the handler
     */
    public void unregister(InteractionHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Handler may not be null");
        }

        Iterator<InteractionHandlerMethod> hit = this.handlers.iterator();
        while (hit.hasNext()) {
            InteractionHandlerMethod method = hit.next();
            if (method.getHandler() == handler) {
                hit.remove();
            }
        }
    }
}