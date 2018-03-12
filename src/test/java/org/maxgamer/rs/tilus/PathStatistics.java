package org.maxgamer.rs.tilus;

/**
 * TODO: Document this
 */
public class PathStatistics {
    private int iterations = 0;

    public void incrementIteratinos() {
        iterations++;
    }

    public int getIterations() {
        return iterations;
    }

    public String toString() {
        return "Iterations: " + iterations;
    }
}
