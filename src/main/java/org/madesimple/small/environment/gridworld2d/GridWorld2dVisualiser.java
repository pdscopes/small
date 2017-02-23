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

    private BackgroundPane       backgroundPane;
    private FadeAway2dVisualiser foregroundPane;

    public GridWorld2dVisualiser() {
        JLayeredPane pane = new JLayeredPane();
        pane.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.setPreferredSize(new Dimension(800, 800));
        backgroundPane = new BackgroundPane();
        foregroundPane = new FadeAway2dVisualiser(10, 10);
        backgroundPane.setPreferredSize(new Dimension(800, 800));
        foregroundPane.setPreferredSize(new Dimension(800, 800));
        backgroundPane.setBounds(0, 0, 800, 800);
        foregroundPane.setBounds(0, 0, 800, 800);
        pane.add(backgroundPane, 2);
        pane.add(foregroundPane, 1);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(pane);
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        backgroundPane.setConfiguration(cfg);
        foregroundPane.setConfiguration(cfg);
    }

    @Override
    public void update(Observable o, Object arg) {
        backgroundPane.update(o, arg);
        foregroundPane.update(o, arg);

        if (o instanceof GridWorld2dEnvironment && arg instanceof GridWorld2dLayout) {
            foregroundPane.setDivisions(((GridWorld2dLayout) arg).stateWidth, ((GridWorld2dLayout) arg).stateHeight);
            foregroundPane.invalidate();
            foregroundPane.repaint();
        }
    }

    static class BackgroundPane extends JPanel implements Configurable, Observer {
        GridWorld2dLayout layout;

        BackgroundPane() {
            setBackground(Color.BLACK);
            setOpaque(true);
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
            int    width   = getWidth();
            int    height  = getHeight();
            int    size    = Math.min(width, height);
            double xSquare = size / layout.stateWidth;
            double ySquare = size / layout.stateHeight;
            int    x       = (width - size) / 2;
            int    y       = (height - size) / 2;


            // Render
            for (int dy = 1; dy < layout.height; dy += 2) {
                for (int dx = 1; dx < layout.width; dx += 2) {

                    drawBorders(g, dx, dy, x, y, xSquare, ySquare);
                    drawSquare(g, dx, dy, x, y, xSquare, ySquare);
                }
            }
        }

        private void drawBorders(Graphics g, int dx, int dy, int x, int y, double xSquare, double ySquare) {
            // North
            switch (layout.map[dy+1][dx]) {
                case GridWorld2dLayout.MAP_WALL_VER:
                    g.setColor(Color.GRAY);
                    g.drawLine(
                            (int) (x + (dx * xSquare)), (int) (y + (dy * ySquare)),
                            (int) (x + ((dx+1) * xSquare)), (int) (y + (dy * ySquare))
                    );
                    break;
            }
            // South
            switch (layout.map[dy-1][dx]) {
                case GridWorld2dLayout.MAP_WALL_VER:
                    g.setColor(Color.GRAY);
                    g.drawLine(
                            (int) (x + (dx * xSquare)), (int) (y + ((dy+1) * ySquare)),
                            (int) (x + ((dx+1) * xSquare)), (int) (y + ((dy+1) * ySquare))
                    );
                    break;
            }

            // West
            switch (layout.map[dy][dx-1]) {
                case GridWorld2dLayout.MAP_WALL_HOR:
                    g.setColor(Color.GRAY);
                    g.drawLine(
                            (int) (x + (dx * xSquare)), (int) (y + (dy * ySquare)),
                            (int) (x + (dx * xSquare)), (int) (y + ((dy+1) * ySquare))
                    );
                    break;
            }
            // East
            switch (layout.map[dy][dx+1]) {
                case GridWorld2dLayout.MAP_WALL_HOR:
                    g.setColor(Color.GRAY);
                    g.drawLine(
                            (int) (x + ((dx+1) * xSquare)), (int) (y + (dy * ySquare)),
                            (int) (x + ((dx+1) * xSquare)), (int) ((y+1) + ((dy+1) * ySquare))
                    );
                    break;
            }

        }

        private void drawSquare(Graphics g, int dx, int dy, int x, int y, double xSquare, double ySquare) {
            switch (layout.map[dy][dx]) {
                case GridWorld2dLayout.MAP_BARRIER:
                    g.setColor(Color.GRAY);
                    break;

                default:
                    return;
            }

            g.fillRect(
                    (int) (x + (dx * xSquare)),
                    (int) (y + (dy * ySquare)),
                    (int) (xSquare),
                    (int) (ySquare)
            );
        }
    }
}
