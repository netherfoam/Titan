package org.maxgamer.rs.tilus;

import org.junit.Assert;
import org.junit.Test;
import org.maxgamer.rs.tilus.paths.Coordinate;
import org.maxgamer.rs.tilus.paths.destination.BoundsDestination;

import static org.maxgamer.rs.tilus.paths.destination.Destinations.*;

/**
 * Test that our bounding destinations correctly calculate the closest tile
 */
public class DestinationTest {
    @Test
    public void testBothCoordinatesOutsideBounds() {
        BoundsDestination d = beside(new Coordinate(11, 11), new Coordinate(14, 14));

        Coordinate from = new Coordinate(1, 1);
        Coordinate closest = d.closest(from);

        // The closest matching coordinate from (1, 1) is (10, 10)
        Assert.assertEquals("Expect closest.x is 10", closest.x, 10);
        Assert.assertEquals("Expect closest.y is 10", closest.y, 10);
    }

    @Test
    public void testBothCoordinatesOppositeBounds() {
        BoundsDestination d = beside(new Coordinate(11, 11), new Coordinate(14, 14));

        Coordinate from = new Coordinate(19, 1);
        Coordinate closest = d.closest(from);

        // The closest matching coordinate from (19, 1) is (15, 10)
        Assert.assertEquals("Expect closest.x is 15", closest.x, 15);
        Assert.assertEquals("Expect closest.y is 10", closest.y, 10);
    }

    @Test
    public void testBothCoordinatesInsideBounds() {
        BoundsDestination d = beside(new Coordinate(11, 11), new Coordinate(14, 14));

        Coordinate from = new Coordinate(12, 13);
        Coordinate closest = d.closest(from);

        // The closest matching coordinate from (12, 13) is (10, 13)
        Assert.assertEquals("Expect closest.x is 10", closest.x, 10);
        Assert.assertEquals("Expect closest.y is 13", closest.y, 13);
    }

    @Test
    public void testBothMaximizedCoordinatesInsideBounds() {
        BoundsDestination d = beside(new Coordinate(11, 11), new Coordinate(14, 14));

        Coordinate from = new Coordinate(14, 13);
        Coordinate closest = d.closest(from);

        // The closest matching coordinate from (14, 13) is (15, 13)
        Assert.assertEquals("Expect closest.x is 15", closest.x, 15);
        Assert.assertEquals("Expect closest.y is 13", closest.y, 13);
    }

    @Test
    public void testBothMinimizedCoordinatesInsideBounds() {
        BoundsDestination d = beside(new Coordinate(11, 11), new Coordinate(14, 14));

        Coordinate from = new Coordinate(12, 12);
        Coordinate closest = d.closest(from);

        // The closest matching coordinate from (12, 12) is (10, 12) or (12, 10) if we were to favour the y axis changing instead
        Assert.assertEquals("Expect closest.x is 10", closest.x, 10);
        Assert.assertEquals("Expect closest.y is 12", closest.y, 12);
    }

    @Test
    public void testBothCoordinatesOutsideBoundsDiagonalNotAllowed() {
        BoundsDestination d = beside(new Coordinate(11, 11), new Coordinate(14, 14), false);

        Coordinate from = new Coordinate(5, 5);
        Coordinate closest = d.closest(from);

        // The closest matching coordinate from (5, 5) is (10, 11)
        Assert.assertEquals("Expect closest.x is 10", closest.x, 10);
        Assert.assertEquals("Expect closest.y is 11", closest.y, 11);
    }

    @Test
    public void testBothCoordinatesOutsideBoundsDiagonalNotAllowedEvenIfStartingThere() {
        BoundsDestination d = beside(new Coordinate(11, 11), new Coordinate(14, 14), false);

        Coordinate from = new Coordinate(10, 10);
        Coordinate closest = d.closest(from);

        // The closest matching coordinate from (5, 5) is (10, 11)
        Assert.assertEquals("Expect closest.x is 10", closest.x, 10);
        Assert.assertEquals("Expect closest.y is 11", closest.y, 11);
    }
}
