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

    private final Map<SectionReference, Section> contents;

    public Dimension(int sectionResolution, int width, int height) {
        // TODO: safety check sectionResolution > 1, width > 0, height > 0
        // TODO: safety check width <= 0xFFFFFF, height <= 0xFFFFFF
        this.width = width;
        this.height = height;
        this.sectionResolution = sectionResolution;
        this.contents = new HashMap<>(width / sectionResolution + 1);
    }

    public void init() {
        for (Map.Entry<SectionReference, Section> entry : contents.entrySet()) {
            SectionReference ref = entry.getKey();
            Section section = entry.getValue();

            section.init(this, ref.sx, ref.sy, ref.z);
        }
    }

    public void set(int sx, int sy, int z, Section section) {
        set(sx, sy, z, section, true);
    }

    private void validateSection(int sx, int sy, int z) {
        if (sx >= width) throw new IllegalArgumentException("sx given " + sx + ", but width is " + width);
        if (sy >= width) throw new IllegalArgumentException("sy given " + sy + ", but width is " + width);

        if (sx < 0) throw new IllegalArgumentException("sx must be >= 0");
        if (sy < 0) throw new IllegalArgumentException("sy must be >= 0");

        if (z >= 4 || z < 0) throw new IllegalArgumentException("z given " + z + ", but height must be 0-3");
    }

    private void set(int sx, int sy, int z, Section section, boolean init) {
        SectionReference key = keyFor(sx, sy, z);
        contents.put(key, section);

        if (init) {
            section.init(this, sx, sy, z);
        }
    }

    public void set(int x, int y, int z, int clip) {
        int sx = (x / sectionResolution);
        int sy = (y / sectionResolution);

        Section s = get(sx, sy, z);

        if (s == null) {
            s = new VariableSection(sectionResolution);
            set(sx, sy, z, s);
        }

        s.set(x % sectionResolution, y % sectionResolution, clip);
    }

    private SectionReference keyFor(int sx, int sy, int z) {
        validateSection(sx, sy, z);

        return new SectionReference(sx, sy, z);
    }

    protected Section get(int sx, int sy, int z) {
        SectionReference key = keyFor(sx, sy, z);

        return contents.get(key);
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
            section = get(start.x / sectionResolution, start.y / sectionResolution, plan.getZ());

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
