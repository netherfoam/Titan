package org.maxgamer.rs.model.action;

import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.model.entity.mob.Mob;
import org.maxgamer.rs.util.Log;

import java.util.*;
import java.util.concurrent.Future;

/**
 * A non trivial class which handles a list of actions a Mob wishes to execute
 * in sequence. Actions in the list may be added/removed/swapped around in the
 * process of running them. This class also manages the Mob's tickable section,
 * when necessary.
 *
 * @author netherfoam
 */
public class ActionQueue {
    private long lastRun = 0;
    private RunRequest req;
    /**
     * The mob that this action queue is for
     */
    private Mob owner;
    /**
     * The list of actions the owner wishes to execute in order
     */
    private LinkedList<Action> queue = new LinkedList<>(); //FIFO queue
    private Action current;

    /**
     * Constructs a new ActionQueue for the given mob.
     *
     * @param owner the mob who this queue will belong to.
     * @throws NullPointerException if owner is null
     */
    public ActionQueue(Mob owner) {
        super();
        if (owner == null) throw new NullPointerException("ActionQueue owner mob may not be null.");
        this.owner = owner;
    }

    protected boolean isQueued() {
        return req != null && !req.cancel;
    }

    protected Future<Void> queue(int delayMs) {
        if (isQueued()) {
            throw new IllegalStateException("Cannot queue() " + this.getClass().getSimpleName() + " because it is already queued.");
        }
        this.req = new RunRequest(this);
        return Core.submit(this.req, delayMs, false);
    }

    protected void cancel() {
        if (req == null) return;
        req.cancel = true;
        req = null;
    }

    /**
     * The mob who owns this action queue, not null. This is the one supplied in
     * the constructor.
     *
     * @return the mob who owns this action queue.
     */
    public Mob getOwner() {
        return owner;
    }

    /**
     * The currently executing Action, may be null.
     *
     * @return The currently executing Action, may be null.
     */
    public Action current() {
        return this.current;
    }

    /**
     * Queues the given action at the end of the queue. If there's nothing else
     * in the queue, this removes the owner from the server's tick and submits
     * them for the immediate next tick.
     *
     * @param w the Action to queue for the Mob
     * @throws IllegalArgumentException if the given action is already in the
     *                                  queue
     * @throws NullPointerException     if the given task is null
     */
    public void queue(Action w) {
        if (w == null) {
            throw new NullPointerException();
        }
        if (isQueued(w)) {
            throw new IllegalArgumentException("That Action is already queued.");
        }

        synchronized (queue) {
            if (queue.isEmpty()) {
                queue.add(w);
                //We can assume that if the queue is not empty, we are currently
                //subscribed to the server's ticker for the next tick.

                if (!isQueued()) {
                    this.queue(0);
                }

                assert isQueued(w) : "Queued task but task is not queued";
            } else {
                //If the new action is not cancellable (A foreground'ish action, like eating)
                //then it should be prioritised over actions which are cancellable (background'ish
                //actions, like walking & combat)
                if (!w.isCancellable()) {
                    for (Action a : this.queue) {
                        if (a.isCancellable()) {
                            this.insertBefore(a, w);
                            return;
                        } else if (!a.isCancellable()) {
                            //This action shouldn't be run, we're busy!
                            return;
                        }
                    }
                }

                queue.add(w);
            }


        }
    }

    /**
     * Fetches the first action in this ActionQueue that is a class or subclass of the given type.
     *
     * @param <T>  the Action type, eg CombatFollow.class
     * @param type the Action type, eg CombatFollow.class
     * @return the Action or null if none match.
     */
    @SuppressWarnings("unchecked")
    public <T extends Action> T first(Class<T> type) {
        for (Action a : this.queue) {
            if (type.isInstance(a)) return ((T) a);
        }
        return null;
    }

    /**
     * Cancels all actions which an instance of or a subclass of the given type.
     *
     * @param type the type to cancel, eg WalkAction
     * @return the number of cancelled actions or 0 if none were cancelled.
     */
    public int cancel(Class<? extends Action> type) {
        int n = queue.size();
        synchronized (queue) {
            for (int i = 0; i < queue.size(); i++) {
                if (type.isInstance(queue.get(i))) {
                    cancel(queue.get(i));
                    i--;
                }
            }

            return n - queue.size();
        }
    }

    /**
     * Inserts the given action before the given marker action. Eg if you wish
     * to insert a follow before a combat action.
     *
     * @param marker the action to look for
     * @param insert the action to place immediately before the marker
     * @throws IllegalArgumentException if the insert Action is already queued
     * @throws IllegalStateException    if the marker is not in the queue.
     */
    public void insertBefore(Action marker, Action insert) {
        if (marker == null) {
            throw new NullPointerException("Marker may not be null");
        }
        if (insert == null) {
            throw new NullPointerException("Insert may not be null");
        }
        if (isQueued(insert)) {
            throw new IllegalArgumentException("That Action is already queued.");
        }

        synchronized (queue) {
            for (int i = 0; i < queue.size(); i++) {
                Action w = queue.get(i);
                if (w == marker) {
                    queue.add(i, insert);
                    return;
                }
            }
        }
        throw new IllegalStateException("No such action marker found for insert operation (Marker not found)");
    }

    public boolean hasUncancellable() {
        synchronized (this) {
            for (Action a : this.queue) {
                if (!a.isCancellable()) return true;
            }
        }

        return false;
    }

    /**
     * Returns the Action after the given marker. Useful for assertions.
     *
     * @param marker the action in the queue which wants the action after it
     * @return the Action after the given action, or null if there is none or the marker wasn't found
     */
    public Action after(Action marker) {
        synchronized (this) {
            for (int i = 0; i < this.queue.size() - 1; i++) {
                if (this.queue.get(i) == marker) {
                    return this.queue.get(i + 1);
                }
            }
        }
        return null;
    }

    /**
     * Returns the Action before the given marker. Useful for assertions.
     *
     * @param marker the action in the queue which wants the action before it
     * @return the Action before the given action, or null if there is none or the marker wasn't found
     */
    public Action before(Action marker) {
        synchronized (this) {
            for (int i = 1; i < this.queue.size(); i++) {
                if (this.queue.get(i) == marker) {
                    return this.queue.get(i - 1);
                }
            }
        }
        return null;
    }

    /**
     * Inserts the given action at the start of the queue, may interrupt another action but
     * does not cancel the other action. Eg use for equipping items.
     *
     * @param insert The action to insert
     */
    public void insertFirst(Action insert) {
        if (insert == null) {
            throw new NullPointerException("Insert may not be null");
        }
        if (isQueued(insert)) {
            throw new IllegalArgumentException("That Action is already queued.");
        }

        synchronized (queue) {
            queue.addFirst(insert);
            if (!this.isQueued()) {
                this.queue(0);
            }
        }
    }

    /**
     * Inserts the given action after the given marker action.
     *
     * @param marker the action to look for
     * @param insert the action to place immediately after the marker
     * @throws IllegalArgumentException if the insert Action is already queued
     * @throws IllegalStateException    if the marker is not in the queue.
     */
    public void insertAfter(Action marker, Action insert) {
        if (marker == null) {
            throw new NullPointerException("Marker may not be null");
        }
        if (insert == null) {
            throw new NullPointerException("Insert may not be null");
        }
        if (isQueued(insert)) {
            throw new IllegalArgumentException("That Action is already queued.");
        }

        synchronized (queue) {
            for (int i = 0; i < queue.size(); i++) {
                Action w = queue.get(i);
                if (w == marker) {
                    queue.add(i + 1, insert);
                    return;
                }
            }
        }
        throw new IllegalStateException("No such action marker found for insert operation (Marker not found)");
    }

    /**
     * Returns true if the given action is queued to be run
     *
     * @param w the action to search for
     * @return true if it is queued to be run, false if not.
     */
    public boolean isQueued(Action w) {
        if (w == null) {
            throw new NullPointerException("Action may not be null");
        }
        synchronized (queue) {
            return queue.contains(w);
        }
    }

    /**
     * Cancels the given action. This invokes Action.onCancel() on the given
     * action, if it is removes from the queue. If it is not found in the queue,
     * the method returns. This may be invoked on actions which are not
     * cancellable and will have the same effect.
     *
     * @param w the action to queue
     */
    public void cancel(Action w) {
        if (w == null) {
            throw new NullPointerException("Action may not be null");
        }

        synchronized (queue) {
            if (queue.remove(w)) {
                w.onCancel();

                //Now cancel all paired actions
                if (w.paired != null) {
                    for (Action p : w.paired) {
                        cancel(p);
                    }
                }
            }
        }
        //Technically could be put in the queue again by onCancel() of w or any of the items it's paired with
        assert !isQueued(w) : "Removed " + w + " from queue, but it is still in the queue.";
    }

    /**
     * Cancels all tasks in this queue which are cancellable. If tasks return
     * false to Action.isCancellable(), then they are not removed from the
     * queue.
     */
    public void clear() {
        synchronized (queue) {
            Iterator<Action> wit = new ArrayList<>(queue).iterator();
            while (wit.hasNext()) {
                Action w = wit.next();

                if (w.isCancellable()) {
                    cancel(w);
                }
            }
        }
    }

    /**
     * Ticks this ActionQueue, processing any elements required.
     *
     * @return
     */
    public void tick() {
        if (!Core.getServer().getThread().isServerThread()) {
            throw new RuntimeException("ActionQueue should only be ticked on the Server thread.");
        }

        synchronized (queue) {
            if (!getOwner().isLoaded()) {
                //We should not process mobs which are not loaded.
                return;
            }

            if (getOwner().isDestroyed()) {
                //We cannot be processed, our owner is destroyed.
                return;
            }

            if (queue.isEmpty()) {
                getOwner().onIdle();
                return;
            }

            Action w = queue.getFirst();
            try {
                current = w;
                w.tick(); //W will end itself when its done.
            } catch (Exception e) {
                //Our task failed to work properly.
                queue.remove(w); //Threw an exception, don't trust it again.

                Log.warning("Error ticking ActionQueue for Mob " + owner + ". Action: " + w);
                e.printStackTrace();
            }
            current = null;
        }
    }

    /**
     * Gracefully ends the action - Called when the action has finished. This removes it from
     * the queue of actions to run, and thus it will not be run again. This is different to
     * cancelling a task, which will inform the task that it was cancelled forcefully. If you
     * wish to cancel an action, use cancel. If your action has exited its run() method, this
     * method will be called for you by the Fiber which ran your action.
     *
     * @param a the action which is ending
     */
    public void end(Action a) {
        synchronized (this) {
            this.queue.remove(a);
        }
    }

    /**
     * Yields the given Action, and allows the action immediately after it to be
     * run in this tick. This is a nice way of prompting other tasks to 'go
     * ahead' without cancelling the current task. A great example of this is
     * combat. Firstly, a FollowAction is queued. Then, a Combat Action is
     * queued. Once the Follow Action reaches the target mob, it then calls
     * yield(). This yield method allows the Combat Action to have its run()
     * invoked. If the target moves, then the Follow action does not have to
     * call yield(). Thus the follow is not cancelled, nor the combat cancelled.
     *
     * @param yielder the Action that is yielding. This is required to find the
     *                action which follows it, eg, the one we will yield to.
     */
    protected void yield(Action yielder) {
        synchronized (queue) {
            //Yielder is requesting that we allow the next item in the queue to be ticked.
            int index = queue.indexOf(yielder);
            if (index == -1) return; //Cannot yield, since it is not queued

            Action w;

            if (index < queue.size() - 1) { //If there's another item after the yielder
                w = queue.get(index + 1);
            } else {
                return; //There's nothing to yield to here.
            }

            Action old = current;
            try {
                current = w;
                //Performed inside of synchronization block.
                w.tick(); //w will end itself when its finished
            } catch (Exception e) {
                //Our task failed to work properly.
                queue.remove(w); //Threw an exception, don't trust it again.

                Log.warning("Error ticking ActionQueue for Mob " + owner + ". Action: " + w);
                e.printStackTrace();
            }
            current = old;
        }
    }

    /**
     * Returns an unmodifiable list, which contains all of the actions which are
     * currently queued for this mob. The first index(0) is the first action to
     * execute. Once that action completes, the second action index(1) will
     * execute until finished.
     *
     * @return the list of actions to execute
     */
    public List<Action> getList() {
        synchronized (this.queue) {
            return Collections.unmodifiableList(this.queue);
        }
    }

    /**
     * Returns true if this ActionQueue currently has no actions in the queue.
     *
     * @return true if empty, false if it has actions to execute.
     */
    public boolean isEmpty() {
        synchronized (this.queue) {
            return queue.isEmpty();
        }
    }

    /**
     * Returns a list of actions queued in this queue, seperated by commas.
     *
     * @return Returns a list of actions queued in this queue.
     */
    @Override
    public String toString() {
        synchronized (queue) {
            StringBuilder sb = new StringBuilder("ActionQueue(" + this.queue.size() + ") {");

            for (Action a : queue) {
                String s = a.toString();
                if (s == null || s.isEmpty()) {
                    if (a.getClass().isAnonymousClass()) {
                        s = a.getClass().getName();
                    }
                }
                sb.append(s).append(", ");
            }
            if (!queue.isEmpty()) {
                //Trim the last ", " off.
                sb.replace(sb.length() - 2, sb.length(), "");
            }
            sb.append("}, running=").append(this.isQueued());

            return sb.toString();
        }
    }

    public boolean isQueued(Class<? extends Action> type) {
        synchronized (queue) {
            for (Action a : queue) {
                if (type.isInstance(a)) return true;
            }
        }
        return false;
    }

    public int size() {
        return this.queue.size();
    }

    private static class RunRequest implements Runnable {
        private boolean cancel = false;
        private ActionQueue queue;

        private RunRequest(ActionQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            if (cancel) {
            } else {
                queue.req = null;
                queue.tick();
            }
        }
    }
}