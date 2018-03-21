package org.maxgamer.rs.tilus;

import java.util.Objects;

/**
 * TODO: Document this
 */
public final class SectionReference {
    public final int sx;
    public final int sy;
    public final int z;

    private final int hash;

    public SectionReference(int sx, int sy, int z) {
        this.sx = sx;
        this.sy = sy;
        this.z = z;

        hash = Objects.hash(sx, sy, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectionReference that = (SectionReference) o;

        return sx == that.sx &&
                sy == that.sy &&
                z == that.z;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
