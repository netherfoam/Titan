package org.maxgamer.rs.tilus;

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
    private JFrame frame;

    @Before
    public void setup() {
        frame = new JFrame("Plan");
        frame.setSize(512, 512);
    }

    public void visualise(PlanBuilder builder, Dimension dimension) throws InterruptedException {
        final TileVisualiser panel = new TileVisualiser(dimension);

        final PathStatistics statistics = new PathStatistics();

        Plan plan = new Plan(
                builder.parameters()
        ) {
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
    }

    @Test
    public void test() throws InterruptedException {
        Dimension dimension = new Dimension(8, 2, 2);
        for (int i = 0; i < dimension.getWidth(); i++) {
            for (int j = 0; j < dimension.getHeight(); j++) {
                dimension.set(i, j, new VariableSection(dimension.getSectionResolution()));
            }
        }

        int size = dimension.getSectionResolution();
        for (int i = 0; i < dimension.getWidth() * size; i++) {
            Section section = dimension.get(i / size, 7 / size);
            if (i == 9) continue;

            section.set(i % size, 7, ClipMasks.BLOCKED_TILE);
        }

        visualise(PlanBuilder.create()
                        .start(new Coordinate(5, 5))
                        .end(new Coordinate(1, 14), new Coordinate(5, 5), DestinationBounds.Type.INSIDE),
                dimension);
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
