package org.maxgamer.rs.tilus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.maxgamer.rs.model.map.ClipMasks;
import org.maxgamer.rs.tilus.paths.*;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * TODO: Document this
 */
public class PlanTest {
    public static void open(Dimension dimension) {
        for (int i = 0; i < dimension.getWidth(); i++) {
            for (int j = 0; j < dimension.getHeight(); j++) {
                dimension.set(i, j, new VariableSection(dimension.getSectionResolution()));
            }
        }
    }

    private JFrame frame;

    @Before
    public void setup() {
        frame = new JFrame("Plan");
        frame.setSize(512, 512);
    }

    public Plan visualise(PlanBuilder builder, Dimension dimension) throws InterruptedException {
        final TileVisualiser panel = new TileVisualiser(dimension);

        final PathStatistics statistics = new PathStatistics();

        Plan plan = new Plan(builder) {
            @Override
            public void forward() {
                synchronized (this) {
                    statistics.incrementIteratinos();
                    panel.repaint();

                    try {
                        this.wait(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        super.forward();
                    }
                }
            }
        };

        panel.setPlan(plan);
        frame.setContentPane(panel);
        frame.setVisible(true);

        dimension.plan(plan);

        // Trigger the final successful repaint
        panel.repaint();

        System.out.println("Statistics\n" + statistics);

        // Just wait forever until the window is closed
        CloseWaiter waiter = new CloseWaiter();
        frame.addWindowListener(waiter);
        waiter.awaitClose();

        return plan;
    }

    @Test
    public void testEmpty() throws InterruptedException {
        Dimension dimension = new Dimension(2, 2, 2);
        open(dimension);

        Plan plan = visualise(PlanBuilder.create()
                        .start(new Coordinate(1, 1))
                        .end(new Coordinate(2, 2), new Coordinate(2, 2), DestinationBounds.Type.INSIDE),
                dimension);

        Assert.assertFalse("Expect plan success", plan.isFailed());
    }

    @Test
    public void testBasicWall() throws InterruptedException {
        Dimension dimension = new Dimension(8, 2, 2);
        open(dimension);

        int size = dimension.getSectionResolution();
        for (int i = 0; i < dimension.getWidth() * size; i++) {
            Section section = dimension.get(i / size, 7 / size);
            if (i == 9) continue;

            section.set(i % size, 7, ClipMasks.BLOCKED_TILE);
        }

        Plan plan = visualise(PlanBuilder.create()
                        .start(new Coordinate(5, 5))
                        .end(new Coordinate(1, 10), new Coordinate(2, 12), DestinationBounds.Type.INSIDE),
                dimension);

        Assert.assertFalse("Expect plan success", plan.isFailed());
    }

    @Test
    public void testMaze() throws InterruptedException {
        Dimension dimension = new Dimension(2, 3, 3);
        open(dimension);

        char[][] MAZE = new char[][]{
                {' ', ' ', '^', ' ', ' ', 'X'},
                {'X', ' ', 'X', ' ', 'X', 'X'},
                {'X', ' ', 'X', ' ', ' ', 'X'},
                {' ', ' ', 'X', 'X', 'X', 'X'},
                {' ', 'X', ' ', ' ', '$', 'X'},
                {' ', ' ', ' ', 'X', 'X', 'X'}
        };

        int size = dimension.getSectionResolution();
        for (int y = 0; y < MAZE.length; y++) {
            for (int x = 0; x < MAZE[y].length; x++) {
                if (MAZE[y][x] != 'X') {
                    // Area isn't blocked
                    continue;
                }

                Section section = dimension.get(x / size, y / size);
                section.set(x % size, y % size, ClipMasks.BLOCKED_TILE);
            }
        }

        Plan plan = visualise(PlanBuilder.create()
                        .start(new Coordinate(2, 0))
                        .end(new Coordinate(4, 4), new Coordinate(4, 4), DestinationBounds.Type.INSIDE),
                dimension);

        Assert.assertFalse("Expect plan success", plan.isFailed());
    }

    private static class CloseWaiter extends WindowAdapter {
        private boolean closed = false;

        @Override
        public void windowClosing(WindowEvent windowEvent) {
            super.windowClosed(windowEvent);

            synchronized (this) {
                closed = true;
                this.notifyAll();
            }
        }

        public void awaitClose() throws InterruptedException {
            synchronized (this) {
                while (!closed) {
                    this.wait();
                }
            }
        }
    }
}
