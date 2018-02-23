package org.maxgamer.rs.tiler;

/**
 * TODO: Document this
 */
public class ConstantSection extends Section {
    private int clip;

    public ConstantSection(int clip) {
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
}
