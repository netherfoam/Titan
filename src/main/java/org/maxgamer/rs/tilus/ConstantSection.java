package org.maxgamer.rs.tilus;

import org.maxgamer.rs.tilus.paths.Move;
import org.maxgamer.rs.tilus.paths.Plan;

/**
 * TODO: Document this
 */
public class ConstantSection extends Section {
    private int clip;

    public ConstantSection(int size, int clip) {
        super(size);

        this.clip = clip;
    }

    @Override
    public void set(int x, int y, int clip) {
        // Setting this has no effect, but does not raise an exception
    }

    @Override
    public void unset(int x, int y, int clip) {
        // Unsetting this has no effect, but does not raise an exception
    }

    @Override
    public int get(int x, int y) {
        return clip;
    }

    @Override
    public void visit(Plan plan, Move move) {
        plan.forward();
    }
}
