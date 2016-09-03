package org.maxgamer.rs.structure.timings;

public class NullStopWatch extends StopWatch {

    protected NullStopWatch(String alias, Timings src) {
        super(alias, src);
    }

    public void stop() {
        // Meh
    }

    /**
     * The number of nanoseconds this timer has been running for. If it was not
     * stopped, then this method returns the current System.nanoTime() -
     * startTime. Otherwise, it returns endTime - startTime.
     *
     * @return the time elapsed from start to finish.
     */
    public long getTime() {
        return 0;
    }

    public void pause() {
        // Meh
    }

    public void resume() {
        // Meh
    }
}