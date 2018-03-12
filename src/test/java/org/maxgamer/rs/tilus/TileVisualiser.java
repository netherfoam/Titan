package org.maxgamer.rs.tilus;

import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.tilus.paths.Coordinate;
import org.maxgamer.rs.tilus.paths.Move;
import org.maxgamer.rs.tilus.paths.Plan;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * TODO: Document this
 */
public class TileVisualiser extends JPanel {
    private Dimension dimension;
    private Plan plan;

    public TileVisualiser(Dimension dimension) {
        this.dimension = dimension;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        synchronized (plan) {
            Graphics2D g2d = (Graphics2D) graphics;

            int width = getWidth();
            int height = getHeight();

            int dimWidth = dimension.getWidth() * dimension.getSectionResolution();
            int dimHeight = dimension.getHeight() * dimension.getSectionResolution();

            double scaleWidth = width / dimWidth;
            double scaleHeight = height / dimHeight;

            AffineTransform transform = g2d.getTransform();
            g2d.scale(scaleWidth, scaleHeight);

            paintSections(g2d);
            paintClipMasks(g2d, dimWidth, dimHeight);
            paintSeen(g2d);
            paintMoves(g2d);
            paintSuccessfulPath(g2d);

            // Reset transform to original
            g2d.setTransform(transform);

            paintTileMeta(g2d, scaleWidth, scaleHeight, dimWidth, dimHeight);
            g2d.setTransform(transform);
        }
    }

    private void paintSections(Graphics2D g2d) {
        for (int sx = 0; sx < dimension.getWidth(); sx++) {
            for (int sy = 0; sy < dimension.getHeight(); sy++) {
                paintSection(g2d, sx, sy, dimension.get(sx, sy));
            }
        }
    }

    private void paintSection(Graphics2D g2d, int sx, int sy, Section section) {
        int resolution = dimension.getSectionResolution();
        if (section == null) {
            g2d.setColor(Color.BLACK);
        } else {
            g2d.setColor(Color.DARK_GRAY);
        }

        g2d.fillRect(sx * resolution, sy * resolution, resolution, resolution);
    }

    private void paintSeen(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        for (Coordinate seen : plan.getBlacklisted()) {
            g2d.fillRect(seen.x, seen.y, 1, 1);
        }
    }

    private void paintMoves(Graphics2D g2d) {
        g2d.setColor(Color.BLUE);
        for (Move move : plan.getMoves()) {
            trace (g2d, move);
        }
    }

    private void trace(Graphics2D g2d, Move move) {
        while (move != null) {
            Coordinate c = move.getEndingCoordinate();
            g2d.fillRect(c.x, c.y, 1, 1);
            move = move.getPrevious();
        }
    }

    private void paintSuccessfulPath(Graphics2D g2d) {
        g2d.setColor(Color.MAGENTA);
        if (plan.getLast() != null) {
            Move m = plan.getLast();

            while (m != null) {
                Coordinate c = m.getEndingCoordinate();
                g2d.fillRect(c.x, c.y, 1, 1);
                m = m.getPrevious();
            }
        }
    }

    private void paintTileMeta(Graphics2D g2d, double scaleWidth, double scaleHeight, int dimWidth, int dimHeight) {
        // Begin detail information
        final int DETAIL_RESOLUTION = 40;
        g2d.scale(scaleWidth / DETAIL_RESOLUTION, scaleHeight / DETAIL_RESOLUTION);

        g2d.setColor(Color.ORANGE);
        // Draw a border around all of our tiles
        for (int x = 0; x < dimWidth; x++) {
            for (int y = 0; y < dimHeight; y++) {
                Graphics2D section = (Graphics2D) g2d.create(x * DETAIL_RESOLUTION, y * DETAIL_RESOLUTION, DETAIL_RESOLUTION, DETAIL_RESOLUTION);

                section.drawRect(0, 0, DETAIL_RESOLUTION, DETAIL_RESOLUTION);
                section.drawString(x + "," + y, 0, DETAIL_RESOLUTION);
                section.dispose();
            }
        }
    }

    private void paintClipMasks(Graphics2D g2d, int dimWidth, int dimHeight) {
        int size = dimension.getSectionResolution();

        // Sienna (Brown)
        g2d.setColor(new Color(160, 82, 45));
        //g2d.setColor(Color.YELLOW);

        for (int i = 0; i < dimWidth; i++) {
            for (int j = 0; j < dimHeight; j++) {
                Section section = dimension.get(i / size, j / size);
                if (section == null) continue;

                if ((section.get(i % size, j % size) & ClipMasks.BLOCKED_TILE) != 0) {
                    g2d.fillRect(i, j, 1, 1);
                }
            }
        }
    }
}
