package org.madesimple.small.environment.gridworld2d;

import org.madesimple.small.utility.Configurable;
import org.madesimple.small.utility.Configuration;
import org.madesimple.small.visualisation.FadeAway2dVisualiser;
import org.madesimple.small.visualisation.Visualiser;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class GridWorld2dVisualiser extends JPanel implements Visualiser {

    private LayoutMapPane        layoutMapPane;
    private FadeAway2dVisualiser fadeAwayPane;

    public GridWorld2dVisualiser() {
        JLayeredPane pane = new JLayeredPane();
        pane.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.setPreferredSize(new Dimension(800, 800));
        layoutMapPane = new LayoutMapPane();
        fadeAwayPane = new FadeAway2dVisualiser(10, 10);
        layoutMapPane.setPreferredSize(new Dimension(800, 800));
        fadeAwayPane.setPreferredSize(new Dimension(800, 800));
        layoutMapPane.setBounds(0, 0, 800, 800);
        fadeAwayPane.setBounds(0, 0, 800, 800);
        pane.add(layoutMapPane, 1);
        pane.add(fadeAwayPane, 2);
        setBackground(Color.BLACK);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(pane);
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        layoutMapPane.setConfiguration(cfg);
        fadeAwayPane.setConfiguration(cfg);
    }

    @Override
    public void update(Observable o, Object arg) {
        layoutMapPane.update(o, arg);
        fadeAwayPane.update(o, arg);

        if (o instanceof GridWorld2dEnvironment && arg instanceof GridWorld2dLayout) {
            fadeAwayPane.setDivisions(((GridWorld2dLayout) arg).stateWidth, ((GridWorld2dLayout) arg).stateHeight);
            fadeAwayPane.invalidate();
            fadeAwayPane.repaint();
        }
    }

    static class LayoutMapPane extends JPanel implements Configurable, Observer {
        GridWorld2dLayout layout;

        LayoutMapPane() {
            setOpaque(false);
        }

        @Override
        public void setConfiguration(Configuration cfg) {

        }

        @Override
        public void update(Observable o, Object arg) {
            if (o instanceof GridWorld2dEnvironment && arg instanceof GridWorld2dLayout) {
                layout = (GridWorld2dLayout) arg;
                invalidate();
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (layout == null) {
                return;
            }

            // Do some calculations
            double width   = getWidth();
            double height  = getHeight();
            double size    = Math.min(width, height);
            double xSquare = size / layout.stateWidth;
            double ySquare = size / layout.stateHeight;
            double x       = (width - size) / 2;
            double y       = (height - size) / 2;


            // Render
            for (int dy = 1, ddy = 0; dy < layout.height; dy += 2, ddy++) {
                for (int dx = 1, ddx = 0; dx < layout.width; dx += 2, ddx++) {

                    drawSquare(g, dx, dy, ddx, ddy, x, y, xSquare, ySquare);
                    drawBorders(g, dx, dy, ddx, ddy, x, y, xSquare, ySquare);
                }
            }
        }

        private void drawBorders(Graphics g, int dx, int dy, int ddx, int ddy, double x, double y, double xSquare, double ySquare) {
            double eastX  = x + (((double) ddx) * xSquare);
            double westX  = x + (((double) ddx+1) * xSquare);
            double northY = y + (((double) ddy+1) * ySquare);
            double southY = y + (((double) ddy) * ySquare);
            double per    = 0.05;

            // North
            switch (layout.raw[dy+1][dx]) {
                case GridWorld2dLayout.MAP_WALL:
                    g.setColor(Color.GRAY);
                    g.fillPolygon(
                            new int[]{(int) eastX, (int) westX, (int) (westX - (xSquare * per)), (int) (eastX + (xSquare * per))},
                            new int[]{(int) northY, (int) northY, (int) (northY - (ySquare * per)), (int) (northY - (ySquare * per))},
                            4
                    );
                    break;
                case GridWorld2dLayout.MAP_GATE:
                case GridWorld2dLayout.MAP_ONE_WAY_NORTH:
                case GridWorld2dLayout.MAP_ONE_WAY_SOUTH:
                    g.setColor(Color.BLUE);
                    g.fillPolygon(
                            new int[]{(int) eastX, (int) westX, (int) (westX - (xSquare * per)), (int) (eastX + (xSquare * per))},
                            new int[]{(int) northY, (int) northY, (int) (northY - (ySquare * per)), (int) (northY - (ySquare * per))},
                            4
                    );
                    break;
            }
            // South
            switch (layout.raw[dy-1][dx]) {
                case GridWorld2dLayout.MAP_WALL:
                    g.setColor(Color.GRAY);
                    g.fillPolygon(
                            new int[] {(int) eastX, (int) westX, (int) (westX - (xSquare * per)), (int) (eastX + (xSquare * per))},
                            new int[] {(int) southY, (int) southY, (int) (southY + (ySquare * per)), (int) (southY + (ySquare * per))},
                            4
                    );
                    break;
                case GridWorld2dLayout.MAP_GATE:
                case GridWorld2dLayout.MAP_ONE_WAY_NORTH:
                case GridWorld2dLayout.MAP_ONE_WAY_SOUTH:
                    g.setColor(Color.BLUE);
                    g.fillPolygon(
                            new int[] {(int) eastX, (int) westX, (int) (westX - (xSquare * per)), (int) (eastX + (xSquare * per))},
                            new int[] {(int) southY, (int) southY, (int) (southY + (ySquare * per)), (int) (southY + (ySquare * per))},
                            4
                    );
                    break;
            }

            // West
            switch (layout.raw[dy][dx+1]) {
                case GridWorld2dLayout.MAP_WALL:
                    g.setColor(Color.GRAY);
                    g.fillPolygon(
                            new int[] {(int) westX, (int) westX, (int) (westX - (xSquare * per)), (int) (westX - (xSquare * per))},
                            new int[] {(int) northY, (int) southY, (int) (southY + (ySquare * per)), (int) (northY - (ySquare * per))},
                            4
                    );
                    break;
                case GridWorld2dLayout.MAP_GATE:
                case GridWorld2dLayout.MAP_ONE_WAY_EAST:
                case GridWorld2dLayout.MAP_ONE_WAY_WEST:
                    g.setColor(Color.BLUE);
                    g.fillPolygon(
                            new int[] {(int) westX, (int) westX, (int) (westX - (xSquare * per)), (int) (westX - (xSquare * per))},
                            new int[] {(int) northY, (int) southY, (int) (southY + (ySquare * per)), (int) (northY - (ySquare * per))},
                            4
                    );
                    break;
            }
            // East
            switch (layout.raw[dy][dx-1]) {
                case GridWorld2dLayout.MAP_WALL:
                    g.setColor(Color.GRAY);
                    g.fillPolygon(
                            new int[] {(int) eastX, (int) eastX, (int) (eastX + (xSquare * per)), (int) (eastX + (xSquare * per))},
                            new int[] {(int) northY, (int) southY, (int) (southY + (ySquare * per)), (int) (northY - (ySquare * per))},
                            4
                    );
                    break;
                case GridWorld2dLayout.MAP_GATE:
                case GridWorld2dLayout.MAP_ONE_WAY_EAST:
                case GridWorld2dLayout.MAP_ONE_WAY_WEST:
                    g.setColor(Color.BLUE);
                    g.fillPolygon(
                            new int[] {(int) eastX, (int) eastX, (int) (eastX + (xSquare * per)), (int) (eastX + (xSquare * per))},
                            new int[] {(int) northY, (int) southY, (int) (southY + (ySquare * per)), (int) (northY - (ySquare * per))},
                            4
                    );
                    break;
            }

        }

        private void drawSquare(Graphics g, int dx, int dy, int ddx, int ddy, double x, double y, double xSquare, double ySquare) {
            double eastX  = x + (((double) ddx) * xSquare);
            double westX  = x + (((double) ddx+1) * xSquare);
            double northY = y + (((double) ddy+1) * ySquare);
            double southY = y + (((double) ddy) * ySquare);
            double per    = 0.05;

            if (layout.raw[dy][dx] == GridWorld2dLayout.MAP_BARRIER) {
                g.setColor(Color.GRAY);
                g.fillRect(
                        (int) (x + (((double) ddx) * xSquare)),
                        (int) (y + (((double) ddy) * ySquare)),
                        (int) (xSquare),
                        (int) (ySquare)
                );
            } else if (Character.isAlphabetic(layout.raw[dy][dx])) {
                if (layout.raw[dy][dx] == 'S' || Character.isLowerCase(layout.raw[dy][dx])) {
                    g.setColor(Color.MAGENTA);
                } else {
                    g.setColor(Color.PINK);
                }
                g.fillPolygon(
                        new int[] {(int) eastX, (int) (eastX + xSquare*per), (int) westX, (int) (westX - xSquare*per)},
                        new int[] {(int) (northY - ySquare*per), (int) northY, (int) (southY + ySquare*per), (int) southY},
                        4
                );
                g.fillPolygon(
                        new int[] {(int) eastX, (int) (eastX + xSquare*per), (int) westX, (int) (westX - xSquare*per)},
                        new int[] {(int) (southY + ySquare*per), (int) southY, (int) (northY - ySquare*per), (int) northY},
                        4
                );
            } else {
                return;
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
