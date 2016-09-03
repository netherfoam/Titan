package org.maxgamer.rs;

import org.junit.Test;
import org.maxgamer.rs.structure.areagrid.AreaGrid;
import org.maxgamer.rs.structure.areagrid.Cube;
import org.maxgamer.rs.structure.areagrid.MBR;

import static org.junit.Assert.assertEquals;

public class AreaGridTest {
    @Test
    public void evaluatesExpression() {
        //Unit test. This adds 3x3 Cubes to a new AreaGrid, except for the middle one and then queries for anything
        //overlapping the middle grid. If any overlaps are detected, there is an error and the test has failed.
        AreaGrid<MBR> g = new AreaGrid<MBR>(64, 64, 8);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    //We don't want to put anything in the center! :)
                    continue;
                }

                Cube c = new Cube(new int[]{i * 8, j * 8}, new int[]{8, 8});
                assert (g.get(c, 0).isEmpty());
                g.put(c, c);
            }
        }
        Cube c = new Cube(new int[]{8, 8}, new int[]{8, 8});
        assertEquals(g.get(c, 0).size(), 0);
        c = new Cube(new int[]{1, 1}, new int[]{1, 1});
        assertEquals(g.get(c, 0).size(), 1);
    }
}