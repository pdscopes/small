package org.madesimple.small.environment.mountaincar;

import org.madesimple.small.utility.Configurable;
import org.madesimple.small.utility.Configuration;
import org.madesimple.small.visualisation.FadeAway2dVisualiser;
import org.madesimple.small.visualisation.Visualiser;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class MountainCarVisualiser extends JPanel implements Visualiser {

    private static int DEFAULT_DIVISIONS = 100;

    private BackgroundPane backgroundPane;
    private FadeAway2dVisualiser foregroundPane;

    public MountainCarVisualiser() {
        JLayeredPane pane = new JLayeredPane();
        pane.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane.setPreferredSize(new Dimension(800,800));
        backgroundPane = new BackgroundPane();
        foregroundPane = new FadeAway2dVisualiser(DEFAULT_DIVISIONS, DEFAULT_DIVISIONS);
        backgroundPane.setBounds(0, 0, 800, 800);
        foregroundPane.setBounds(0, 0, 800, 800);
        pane.add(foregroundPane, 1);
        pane.add(backgroundPane, 2);

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
        foregroundPane.update(o, arg);
    }

    static class BackgroundPane extends JPanel implements Configurable {

        BackgroundPane() {
            setBackground(new Color(0.0f, 0.0f, 0.0f));
            this.setOpaque(true);
        }

        @Override
        public void setConfiguration(Configuration cfg) {

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            double width  = Math.min(this.getWidth(), this.getHeight());
            double height = Math.min(this.getWidth(), this.getHeight());

            double range    = MountainCarEnvironment.MAX_POSITION - MountainCarEnvironment.MIN_POSITION;
            double goal     = MountainCarEnvironment.GOAL_POSITION - MountainCarEnvironment.MIN_POSITION;
            double position = goal / range;

            g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.5f));
            g.fillRect(
                    (int) Math.ceil(width * position),
                    0,
                    (int) Math.ceil(width * (1.0 - position)),
                    (int) (height)
            );
            g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
            g.drawRect(
                    0,
                    0,
                    (int) (width),
                    (int) (height)
            );
        }
    }
}
