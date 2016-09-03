package org.maxgamer.rs.structure.areagrid;

import java.util.ArrayList;
import java.util.HashSet;

public class AreaGrid<T> {
    /**
     * The number of bits we need to shift when converting x/y coordinates to
     * grid coordinates - Faster than dividing
     */
    private byte bits;
    /**
     * The array of grids we are to use. Stored as grid[X >> bits][Y >> bits]
     */
    private Grid[][] grid;

    /**
     * Constructs a new AreaGrid
     *
     * @param width       the width of the area grid (X, or Dimension 0). RS max X coordinate is 16383 for rev 637
     * @param length      the length of the grid (Y, or Dimension 1). RS max Y coordinate is 16383 for rev 637
     * @param subgridSize the size to subdivide the map into. Higher values cost less RAM and more CPU time (to lookup). Lower values are more RAM intensive,
     *                    but less intensive on the CPU. Recommended is 8/16/32, depending on density and total size of map.
     */
    public AreaGrid(int width, int length, int subgridSize) {
        if ((subgridSize & -subgridSize) != subgridSize) { // It looks like voodoo, but will return true if lengths isn't a power of 2.
            throw new IllegalArgumentException("Lengths should be a multiple of 2!");
        }

        width = (width + subgridSize - 1) / subgridSize;
        length = (length + subgridSize - 1) / subgridSize;

        //Counts the number of bits required to bitshift.
        if (subgridSize >= 0x7FFF) {
            subgridSize >>= 15;
            this.bits += 15;
        }
        if (subgridSize >= 0x7F) {
            subgridSize >>= 7;
            this.bits += 7;
        }
        if (subgridSize >= 0x7) {
            subgridSize >>= 3;
            this.bits += 3;
        }
        if (subgridSize >= 0x3) {
            subgridSize >>= 2;
            this.bits += 2;
        }
        if (subgridSize >= 0x1) {
            subgridSize >>= 1;
            this.bits += 1;
        }
        if (subgridSize >= 0x1) {
            subgridSize >>= 1;
            this.bits += 1;
        }

        this.bits--; //Take one for some reason.

        //Initialize our grid array.
        this.grid = new Grid[width][];
        for (int i = 0; i < width; i++) {
            this.grid[i] = new Grid[length];
        }
    }

    /**
     * Trims all of the array lists to size, and removes empty ones from the AreaGrid.
     * This tries to same memory.
     */
    public void trim() {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                Grid g = grid[i][j];
                if (g == null) continue;
                if (g.objects == null) continue;
                if (g.objects.isEmpty()) {
                    g.objects = null;
                    continue;
                }
                g.objects.trimToSize();
            }
        }
    }

    /**
     * Gets all MBRs that overlap with the given MBR.
     *
     * @param query The MBR to check for overlaps with
     * @param guess The normal maximum number of results to expect (Efficiency)
     * @return A HashSet (Never null, possible empty) of overlapping results.
     */
    @SuppressWarnings("unchecked")
    public HashSet<T> get(MBR query, int guess) {
        this.validate(query);

        int X = (Math.max(query.getMin(0), 0)) >> this.bits;
        int Y = (Math.max(query.getMin(1), 0)) >> this.bits;

        //We want to reuse the bits that were dropped off from above, and add them here.
        int dx = ((query.getMin(0) & ((1 << this.bits) - 1)) + (query.getDimension(0)) >> this.bits);
        int dy = ((query.getMin(1) & ((1 << this.bits) - 1)) + (query.getDimension(1)) >> this.bits);

        dx = Math.min(this.grid.length - X, dx);

        HashSet<T> objects = new HashSet<T>(guess);

        //We must put it in each grid that it overlaps with.
        for (int xOffset = 0; xOffset <= dx; xOffset++) {
            dy = Math.min(this.grid[X + xOffset].length - Y, dy);

            for (int yOffset = 0; yOffset <= dy; yOffset++) {
                Grid g = this.grid[X + xOffset][Y + yOffset];
                if (g != null && g.objects != null) {
                    synchronized (g.getObjects()) {
                        for (Item i : g.getObjects()) {
                            MBR o = i.mbr;
                            //Version 2.0
                            //We use <= query.getMin(0) because the boundaries TOUCH but do not overlap!
                            if (o.getMin(0) + o.getDimension(0) <= query.getMin(0)) {
                                continue; //o's max is lower than query's min
                            }
                            if (o.getMin(0) >= query.getMin(0) + query.getDimension(0)) {
                                continue; //o's min is higher than query's max
                            }

                            if (o.getMin(1) + o.getDimension(1) <= query.getMin(1)) {
                                continue; //o's max is lower than query's min
                            }
                            if (o.getMin(1) >= query.getMin(1) + query.getDimension(1)) {
                                continue; //o's min is higher than query's max
                            }
                            objects.add((T) i.object);
                        }
                    }
                }
            }
        }

        return objects;
    }

    /**
     * Queries the AreaGrid for all MBR's which are instances of the given class and overlap the given query. For example,
     * HashSet<Player> players = area.get(overlap, 256, Player.class); would be a useful call to fetch all players that
     * are inside the MBR 'overlap'.
     *
     * @param query the MBR which is being used to query
     * @param guess the estimate number of results that will be found
     * @param clazz the type of objects you are interested in
     * @return a HashSet of the MBR's which extend the given class and are in the given area.
     */
    @SuppressWarnings("unchecked")
    public <U extends T> HashSet<U> get(MBR query, int guess, Class<U> clazz) {
        if (clazz == null) throw new NullPointerException("Class may not be null");
        this.validate(query);

        int qMinX = query.getMin(0);
        int qMinY = query.getMin(1);
        int qMaxX = query.getMin(0) + query.getDimension(0);
        int qMaxY = query.getMin(1) + query.getDimension(1);

        int X = (Math.max(qMinX, 0)) >> this.bits;
        int Y = (Math.max(qMinY, 0)) >> this.bits;

        //We want to reuse the bits that were dropped off from above, and add them here.
        int dx = ((qMinX & ((1 << this.bits) - 1)) + (query.getDimension(0)) >> this.bits);
        int dy = ((qMinY & ((1 << this.bits) - 1)) + (query.getDimension(1)) >> this.bits);

        dx = Math.min(this.grid.length - X - 1, dx);

        HashSet<U> objects = new HashSet<U>(guess);
        //We must put it in each grid that it overlaps with.
        for (int xOffset = 0; xOffset <= dx; xOffset++) {
            dy = Math.min(this.grid[X + xOffset].length - Y - 1, dy);

            for (int yOffset = 0; yOffset <= dy; yOffset++) {
                Grid g;
                try {
                    g = this.grid[X + xOffset][Y + yOffset];
                } catch (IndexOutOfBoundsException e) {
                    //We're < 0 or >= length. There are no MBR's here!
                    continue;
                }
                if (g != null && g.objects != null) {
                    synchronized (g.getObjects()) {
                        for (Item i : g.getObjects()) {
                            MBR o = i.mbr;
                            //Version 2.0
                            //We use <= qMinX because the boundaries TOUCH but do not overlap!
                            if (o.getMin(0) + o.getDimension(0) <= qMinX) {
                                continue; //o's max is lower than query's min
                            }
                            if (o.getMin(0) >= qMaxX) {
                                continue; //o's min is higher than query's max
                            }

                            if (o.getMin(1) + o.getDimension(1) <= qMinY) {
                                continue; //o's max is lower than query's min
                            }
                            if (o.getMin(1) >= qMaxY) {
                                continue; //o's min is higher than query's max
                            }

                            if (clazz.isInstance(i.object)) {
                                objects.add((U) i.object);
                            }
                        }
                    }
                }
            }
        }

        return objects;
    }

    /**
     * Adds the given MBR to this grid.
     *
     * @param m The MBR to add.
     */
    public void put(MBR m, T t) {
        this.validate(m);

        int X = (Math.max(m.getMin(0), 0)) >> this.bits;
        int Y = (Math.max(m.getMin(1), 0)) >> this.bits;

        int dx = (m.getDimension(0)) >> this.bits;
        int dy = (m.getDimension(1)) >> this.bits;

        dx = Math.min(this.grid.length - X - 1, dx);

        //We must put it in each grid that it overlaps with.
        for (int xOffset = 0; xOffset <= dx; xOffset++) {
            dy = Math.min(this.grid[X + xOffset].length - Y - 1, dy);

            for (int yOffset = 0; yOffset <= dy; yOffset++) {
                Grid g = this.grid[X + xOffset][Y + yOffset];
                if (g == null) {
                    g = new Grid(); //Guess size for RS objects is usually 150 entities per 8x8 cube.
                    this.grid[X + xOffset][Y + yOffset] = g;
                }
                synchronized (g.getObjects()) {
                    g.getObjects().add(new Item(m, t));
                }
            }
        }
    }

    /**
     * Removes the given MBR from this area grid.
     *
     * @param m The MBR to remove.
     */
    public void remove(MBR m, T t) {
        this.validate(m);

        int X = (Math.max(m.getMin(0), 0)) >> this.bits;
        int Y = (Math.max(m.getMin(1), 0)) >> this.bits;

        int dx = (m.getDimension(0)) >> this.bits;
        int dy = (m.getDimension(1)) >> this.bits;

        dx = Math.min(this.grid.length - X - 1, dx);

        Item item = new Item(m, t);

        //We must put it in each grid that it overlaps with.
        for (int xOffset = 0; xOffset <= dx; xOffset++) {
            dy = Math.min(this.grid[X + xOffset].length - Y - 1, dy);

            for (int yOffset = 0; yOffset <= dy; yOffset++) {
                Grid g = this.grid[X + xOffset][Y + yOffset];
                if (g == null) {
                    continue;
                }

                if (g.objects == null) continue;

                synchronized (g.getObjects()) {
                    g.getObjects().remove(item);
                }
            }
        }
    }

    public <U extends T> HashSet<U> all(Class<U> clazz) {
        MBR mbr = new MBR() {

            @Override
            public int getMin(int axis) {
                return 0;
            }

            @Override
            public int getDimensions() {
                return 2;
            }

            @Override
            public int getDimension(int axis) {
                if (axis == 0) {
                    return grid.length << bits;
                } else {
                    return grid[0].length << bits;
                }
            }
        };

        return get(mbr, 1000, clazz);
    }

    protected void validate(MBR m) {
        if (m == null) {
            throw new NullPointerException("MBR may not be null");
        }
        if (m.getDimensions() < 2) {
            throw new IllegalArgumentException("AreaGrids are 2D, and thus require MBR's have two dimensions at least.");
        }
        if (m.getDimension(0) < 0 || m.getDimension(1) < 0) {
            throw new IllegalArgumentException("AreaGrids require all MBR's to have all dimensions (lengths) of minimum 0.");
        }

        for (int i = 0; i < 2; i++) {
            if (m.getDimension(i) <= 0) {
                throw new IllegalArgumentException("AreaGrid MBR's must have all dimensions > 0. Dimension " + i + " is " + m.getDimension(i));
            }
        }
    }

    /**
     * Represents an area inside an AreaGrid. Say an AreaGrid is 64x64, split into 16x16 cubes. Then the AreaGrid would have 4x4 Grids inside it,
     * each of 16x16 dimensions.
     *
     * @author netherfoam
     */
    private static class Grid {
        /**
         * The objects in this grid.
         */
        private ArrayList<Item> objects;

        /**
         * Constructs an empty grid
         */
        public Grid() {
        }

        public ArrayList<Item> getObjects() {
            if (this.objects == null) {
                this.objects = new ArrayList<Item>(); //Default size here may be nice?
            }
            return this.objects;
        }
    }

    private static class Item {
        private final MBR mbr;
        private final Object object;

        public Item(MBR mbr, Object object) {
            this.mbr = mbr;
            this.object = object;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Item) {
                Item i = (Item) o;
                if (i.object != this.object) {
                    return false;
                }

                if (MBRUtil.isEqual(this.mbr, i.mbr, 2) == false) {
                    return false;
                }

                return true;
            }
            return false;
        }
    }
}