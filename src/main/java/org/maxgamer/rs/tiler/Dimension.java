package org.maxgamer.rs.tiler;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document this
 *
 * TODO: safety checks on bounds
 */
public class Dimension {
    /**
     * Width in sections
     */
    private final int width;

    /**
     * Height in sections
     */
    private final int height;

    private final int sectionResolution;

    private final Map<Integer, Section> contents;

    public Dimension(int sectionResolution, int width, int height) {
        // TODO: safety check sectionResolution > 1, width > 0, height > 0
        // TODO: safety check width <= 0xFFFFFF, height <= 0xFFFFFF
        this.width = width;
        this.height = height;
        this.sectionResolution = sectionResolution;
        this.contents = new HashMap<>(width / sectionResolution + 1);
    }

    public void init() {
        for (Map.Entry<Integer, Section> entry : contents.entrySet()) {
            int sx = (entry.getKey() >> 16) & 0xFFFF;
            int sy = entry.getKey() & 0xFFFF;

            Section section = entry.getValue();
            section.init(this, sx, sy);
        }
    }

    public void set(int sx, int sy, Section section) {
        set(sx, sy, section, true);
    }

    private void set(int sx, int sy, Section section, boolean init) {
        // TODO: safety check sx, sy > 0
        contents.put((sx << 16) | sy, section);

        if (init) {
            section.init(this, sx, sy);
        }
    }

    public void set(int minX, int minY, int[][] clip) {
        // TODO: do this in a non-stupid way
    }

    public Section get(int sx, int sy) {
        // TODO: safety check sx, sy > 0
        return contents.get((sx << 16) | sy);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSectionResolution() {
        return sectionResolution;
    }
}
