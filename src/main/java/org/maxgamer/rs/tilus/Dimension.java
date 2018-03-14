package org.maxgamer.rs.tilus;

import org.maxgamer.rs.tilus.paths.Coordinate;
import org.maxgamer.rs.tilus.paths.Move;
import org.maxgamer.rs.tilus.paths.Plan;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Document this
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

    private void validateSection(int sx, int sy) {
        if (sx >= width) throw new IllegalArgumentException("sx given " + sx + ", but width is " + width);
        if (sy >= width) throw new IllegalArgumentException("sy given " + sy + ", but width is " + width);
        if (sx < 0) throw new IllegalArgumentException("sx must be >= 0");
        if (sy < 0) throw new IllegalArgumentException("sy must be >= 0");
    }

    private void set(int sx, int sy, Section section, boolean init) {
        validateSection(sx, sy);

        contents.put((sx << 16) | sy, section);

        if (init) {
            section.init(this, sx, sy);
        }
    }

    public void set(int x, int y, int clip) {
        int sx = (x / sectionResolution);
        int sy = (y / sectionResolution);

        Section s = get(sx, sy);
        if (s == null) return;

        s.set(x % sectionResolution, y % sectionResolution, clip);
    }

    public Section get(int sx, int sy) {
        validateSection(sx, sy);

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

    public void plan(Plan plan) {
        Move move = plan.getMoves().remove();
        Coordinate start = move.getEndingCoordinate();

        Section section;
        if (move.getFrom() == null) {
            // TODO: power of 2 for sectionResolution means we use bitshifting instead of expensive divide
            section = get(start.x / sectionResolution, start.y / sectionResolution);

            if (section == null) {
                // TODO: Exception?
                return;
            }
        } else {
            section = move.getFrom();
        }

        section.visit(plan, move);
    }
}
