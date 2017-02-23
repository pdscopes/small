package org.madesimple.small.visualisation;

import org.madesimple.small.environment.*;
import org.madesimple.small.utility.Configurable;
import org.madesimple.small.utility.Configuration;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class FadeAway2dVisualiser extends JLayeredPane implements Visualiser {
    public static final int DEFAULT_MAXSIZE = 1000;

    private GridPane     gridPane;
    private FadeAwayPane fadeAwayPane;

    public FadeAway2dVisualiser(int xDivisions, int yDivisions) {

        gridPane = new GridPane(xDivisions, yDivisions);
        fadeAwayPane = new FadeAwayPane(xDivisions, yDivisions);
        gridPane.setBounds(0, 0, 800, 800);
        fadeAwayPane.setBounds(0, 0, 800, 800);

        add(fadeAwayPane, 1);
        add(gridPane, 2);
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        gridPane.setConfiguration(cfg);
        fadeAwayPane.setConfiguration(cfg);
    }

    public void setDivisions(int x, int y) {
        gridPane.setDivisions(x, y);
        fadeAwayPane.setDivisions(x, y);
    }

    @Override
    public void update(Observable o, Object arg) {
        fadeAwayPane.update(o, arg);
    }

    static class GridPane extends JPanel implements Configurable {

        private boolean renderGrid;
        private int     xDivisions;
        private int     yDivisions;

        GridPane(int xDivisions, int yDivisions) {
            this.xDivisions = xDivisions;
            this.yDivisions = yDivisions;
            this.setOpaque(false);
        }

        @Override
        public void setConfiguration(Configuration cfg) {
            renderGrid = cfg.getBoolean("Visualiser.Grid.Render");
            invalidate();
        }

        public void setDivisions(int x, int y) {
            xDivisions = x;
            yDivisions = y;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (renderGrid) {

                // Do some calculations
                int    width   = this.getWidth();
                int    height  = this.getHeight();
                int    size    = Math.min(width, height);
                double xSquare = size / xDivisions;
                double ySquare = size / yDivisions;
                int    x       = (width - size) / 2;
                int    y       = (height - size) / 2;


                g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.50f));

                // Draw outline
                g.drawRect(x, y, size - 1, size - 1);

                // Draw grid
                for (int i = 1; i < xDivisions; i++) {
                    g.drawLine(
                            (int) (x + (i * xSquare)),
                            (int) (y),
                            (int) (x + (i * xSquare)),
                            (int) (y + (yDivisions * ySquare))
                    );
                }
                for (int i = 1; i < yDivisions; i++) {
                    g.drawLine(
                            (int) (x),
                            (int) (y + (i * ySquare)),
                            (int) (x + (xDivisions * xSquare)),
                            (int) (y + (i * ySquare))
                    );
                }
            }
        }
    }

    static class FadeAwayPane extends JPanel implements Configurable, Observer {

        private int[][]     counters;
        private Queue<Pair> queue;
        private int         xDivisions;
        private int         yDivisions;

        FadeAwayPane(int xDivisions, int yDivisions) {

            this.xDivisions = xDivisions;
            this.yDivisions = yDivisions;
            counters = new int[yDivisions][xDivisions];
            queue = new LinkedList<>();
            this.setOpaque(false);
        }

        @Override
        public void setConfiguration(Configuration cfg) {

        }

        public void setDivisions(int x, int y) {
            xDivisions = x;
            yDivisions = y;
        }

        @Override
        public synchronized void update(Observable o, Object arg) {

            if (o instanceof Environment && arg == null) {
                counters = new int[yDivisions][xDivisions];
                queue.clear();
            }

            if (arg instanceof DiscreteState) {
                add(convert((DiscreteEnvironment) o, (DiscreteState) arg));
            }
            if (o instanceof ContinuousEnvironment && arg instanceof ContinuousState) {
                add(convert((ContinuousEnvironment) o, (ContinuousState) arg));
            }
        }

        private void add(Pair pair) {
            queue.add(pair);
            counters[pair.y][pair.x]++;

            if (queue.size() > DEFAULT_MAXSIZE) {
                pair = queue.remove();
                counters[pair.y][pair.x] = Math.max(0, counters[pair.y][pair.x] - 1);
            }

            invalidate();
            repaint();
        }

        private Pair convert(DiscreteEnvironment environment, DiscreteState state) {
            int[] lowerBounds = environment.lowerBounds();
            int[] upperBounds = environment.upperBounds();
            int[] tuple       = state.tuple();

            double xRange = upperBounds[0] - lowerBounds[0];
            double yRange = upperBounds[1] - lowerBounds[1];


            int x = Math.min(xDivisions - 1, Math.max(0, (int) Math.round((((double) tuple[0] - (double) lowerBounds[0]) / xRange) * (double) xDivisions)));
            int y = Math.min(yDivisions - 1, Math.max(0, (int) Math.round((((double) tuple[1] - (double) lowerBounds[1]) / yRange) * (double) yDivisions)));

            return new Pair(x, y);
        }

        private Pair convert(ContinuousEnvironment environment, ContinuousState state) {
            double[] lowerBounds = environment.lowerBounds();
            double[] upperBounds = environment.upperBounds();
            double[] tuple       = state.tuple();

            double xRange = upperBounds[0] - lowerBounds[0];
            double yRange = upperBounds[1] - lowerBounds[1];

            int x = Math.min(xDivisions - 1, Math.max(0, (int) Math.round(((tuple[0] - lowerBounds[0]) / xRange) * (double) xDivisions)));
            int y = Math.min(yDivisions - 1, Math.max(0, (int) Math.round(((tuple[1] - lowerBounds[1]) / yRange) * (double) yDivisions)));

            return new Pair(x, y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Do some calculations
            int    width    = this.getWidth();
            int    height   = this.getHeight();
            int    size     = Math.min(width, height);
            double xSquare  = size / xDivisions;
            double ySquare  = size / yDivisions;
            int    x        = (width - size) / 2;
            int    y        = (height - size) / 2;
            int    maxCount = queue.size();

            // Render
            for (int dy = 0; dy < yDivisions; dy++) {
                for (int dx = 0; dx < xDivisions; dx++) {
                    if (counters[dy][dx] > 0) {
                        g.setColor(temperature(counters[dy][dx], maxCount));
                        g.fillRect(
                                (int) (x + (dx * xSquare)),
                                (int) (y + (dy * ySquare)),
                                (int) (xSquare),
                                (int) (ySquare)
                        );
                    }
                }
            }
        }

        private Color temperature(int value, int maxCount) {
            // Use this to calculate the colour
            float temperature = Math.min(1.0f, (float) value / (float) maxCount);
            float red         = (temperature < 0.5f) ? 1.0f : (1.0f - (2.0f * (temperature - 0.5f)));
            float green       = (temperature < 0.5f) ? temperature * 2.0f : (1.0f - (2.0f * (temperature - 0.5f)));
            float blue        = (temperature > 0.5f) ? Math.min(1.0f, 0.4f + temperature) : 0.0f;
            float alpha       = (temperature < 0.25f) ? Math.min(1.0f, 0.4f + (4.0f * temperature)) : 1.0f;

            return new Color(red, green, blue, alpha);
        }

        static class Pair {
            int x;
            int y;

            Pair(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }
    }
}
