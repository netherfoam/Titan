package org.maxgamer.rs.structure.timings;

import org.maxgamer.rs.util.Calc;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A class for measuring the amount of processing time taken by particular
 * sections of code over a larger period of time. This is not entirely accurate,
 * and adds a small overhead.
 *
 * @author netherfoam
 */
public class Timings {
    private HashMap<String, ThreadTiming> threads = new HashMap<>();

    /**
     * Constructs a new Timings object
     */
    public Timings() {
    }

    /**
     * Empties all existing data and starts this timings object again.
     */
    public void reset() {
        threads.clear();
    }

    /**
     * Constructs a new Timer for the given alias, use this method to start
     * tracking time. Call {@link StopWatch#stop()} when done to finalize the
     * time and add it to this Timings object.
     *
     * @param alias the name of the timer to add to, this must be unique for
     *              different tasks, and identifiable to the user as it is used in the
     *              report this class generates.
     * @return the timer object
     */
    public StopWatch start(String alias) {
        return new StopWatch(alias, this);
    }

    /**
     * Called by a timer object when it is finished.
     *
     * @param t        the timer that finished
     * @param nanosecs the number of nanoseconds that the timer was running for
     */
    protected void stop(StopWatch t, long nanosecs) {
        ThreadTiming thread = threads.get(Thread.currentThread().getName());
        if (thread == null) {
            thread = new ThreadTiming();
            thread.name = Thread.currentThread().getName();
            threads.put(thread.name, thread);
        }

        String k = t.getAlias();
        Long l = thread.data.get(k);
        if (l != null) l = l + nanosecs;
        else l = nanosecs;
        thread.data.put(k, l);

        l = thread.calls.get(k);
        if (l != null) l = l + 1;
        else l = 1L;
        thread.calls.put(k, l);
    }

    /**
     * Returns a basic output of the timings for all the activities. The output
     * format is alias: xxx.xxms. (yyyy calls)\n for each unique timer.
     *
     * @return A formatted report on the time usage of particular tasks.
     */
    public String getReport() {
        //ThreadTiming thread = threads.get(Thread.currentThread().getName());
        StringBuilder sb = new StringBuilder();

        for (final ThreadTiming thread : threads.values()) {
            LinkedList<String> keys = new LinkedList<>(thread.data.keySet());
            Collections.sort(keys, new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    return (int) Calc.betweenl(Integer.MIN_VALUE, Integer.MAX_VALUE, thread.data.get(b) - thread.data.get(a));
                }
            });

            sb.append("-- Thread: ").append(thread.name).append(" --\n");
            for (String k : keys) {
                sb.append(k).append(": ").append(String.format("%.3f", (thread.data.get(k) / 1000) / 1000.0)).append("ms. (").append(thread.calls.get(k)).append(" calls)\n"); //ms with 3 decimals
            }
        }
        return sb.toString();
    }

    private static class ThreadTiming {
        /**
         * The name of the thread, we don't keep a reference to threads so they
         * can be garbage collected
         */
        private String name;

        /**
         * Completed times are added here. This is a map of alias to the number
         * of nanoseconds (approximately) the task has used.
         */
        private HashMap<String, Long> data = new HashMap<>();

        /**
         * The number of times a particular alias was timed. This is useful to
         * show that tasks which are executed frequently are not necessarily the
         * badly written ones.
         */
        private HashMap<String, Long> calls = new HashMap<>();
    }
}
