package org.maxgamer.rs.tilus.paths.destination;

import org.maxgamer.rs.tilus.paths.Coordinate;

/**
 * Implementation of Destination which desires to be beside a set of bounds rather than inside it. This allows diagonals.
 */
public class BesideBoundsDestination extends BoundsDestination {
    public BesideBoundsDestination(Coordinate min, Coordinate max) {
        super(min, max);
    }

    @Override
    public Coordinate closest(Coordinate from) {
        int x = from.x;
        int y = from.y;

        if (min.x >= from.x) x = min.x - 1;
        if (min.y >= from.y) y = min.y - 1;

        if (max.x <= from.x) x = max.x + 1;
        if (max.y <= from.y) y = max.y + 1;

        // TODO: This logic is broken, this will *always* put us at the diagonal at the end of the square.
        // TODO: we are modifying both coordinates when we should only be modifying one.

        // TODO: See DestinationTest for examples of why this fails

        // TODO: Somehow, we need to compare the (width - inset * 2) and (height - inset * 2) values, and, the one with
        // TODO: the greatest magnitude should be enacted only.



        if (from.x == x && from.y == y) {
            // X and Y were never modified! They coordinate must be inside the bounds!
            // However, we only want to be beside the bounds! So we must modify it.

            // The width of the bounding box
            int width = max.x - min.x;

            // The number of tiles inside the bounding box that our target is
            int insetX = from.x - min.x;

            // The number of moves we need to fix the X axis
            int dxToFix = width - insetX * 2;

            // The height of the bounding box
            int height = max.y - min.y;

            // The number of tiles inside the bounding box that our target is
            int insetY = from.y - min.y;

            // The number of moves we need to fix the Y axis
            int dyToFix = height - insetY * 2;

            if (Math.abs(dxToFix) > Math.abs(dyToFix)) {
                // X is more effort to fix. Fix Y instead.
                if (dyToFix >= 0) {
                    // We are closer to the minimum
                    y = min.y - 1;
                } else {
                    // We are closer to the maximum
                    y = max.y + 1;
                }
            } else {
                // Y is more effort to fix. Fix X instead.
                if (dxToFix >= 0) {
                    // We are closer to the minimum
                    x = min.x - 1;
                } else {
                    // We are closer to the maximum
                    x = max.x + 1;
                }
            }
        }

        return new Coordinate(x, y);
    }
}
