package org.maxgamer.rs.core.server;

/**
 * @author netherfoam
 */
public class TickableWrapper implements Comparable<TickableWrapper> {
    private Runnable tick;
    private int period;
    private Exception trace;

    public TickableWrapper(int period, Runnable tick) {
        if (tick == null) {
            throw new NullPointerException("Tick may not be null");
        }
        this.period = period;
        this.tick = tick;
        this.trace = new Exception();
        this.trace.fillInStackTrace();
    }

    public Exception getTrace() {
        return trace;
    }

    public Runnable getTick() {
        return tick;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public int compareTo(TickableWrapper t2) {
        return this.getPeriod() - t2.getPeriod();
    }

    @Override
    public String toString() {
        return "TickTask For: " + tick.toString();
    }
}