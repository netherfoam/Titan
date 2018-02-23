package org.maxgamer.rs.tiler;

/**
 * TODO: Document this
 *
 * TODO: safety checks on bounds
 */
public class VariableSection extends Section {
    private final int width;
    private final int height;
    private final int[] tiles;

    public VariableSection(Dimension dimension) {
        this.width = dimension.getSectionResolution();
        this.height = dimension.getSectionResolution();
        this.tiles = new int[width * height];
    }

    @Override
    public void set(int x, int y, int clip) {
        // TODO: safety check x and y are appropriate (positive, x < width and y < height)
        tiles[x * width + y] |= clip;
    }

    @Override
    public void unset(int x, int y, int clip) {
        // TODO: safety check x and y are appropriate (positive, x < width and y < height)
        tiles[x * width + y] &= ~clip;
    }

    @Override
    public int get(int x, int y) {
        // TODO: safety check x and y are appropriate (positive, x < width and y < height)
        return tiles[x * width + y];
    }
}
