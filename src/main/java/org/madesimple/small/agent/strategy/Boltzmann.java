package org.madesimple.small.agent.strategy;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.utility.Configuration;
import org.madesimple.small.utility.Decay;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * Boltzmann is an implementation of Soft Max action selection strategy.
 * </p>
 * <p>
 * The Boltzmann strategy is to each time give a probability to to all actions
 * that may happen but weight them such that the larger their Q-Value the more
 * probable they are to be picked but also allow a temperature, tau, that should
 * decrease over time which makes actions selection more random the "hotter" it
 * is.
 * </p>
 * <p>
 * Properties required to run Boltzmann in a configuration file:
 * </p>
 * <pre>
 * ## Boltzmann Selection Settings
 * Strategy.Boltzmann.Tau           = 0.4
 * Strategy.Boltzmann.ShouldDecay   = true
 * Strategy.Boltzmann.Decay.Type    = LINEAR
 * Strategy.Boltzmann.Decay.Over    = 300
 * Strategy.Boltzmann.Decay.Minimum = 0
 * Strategy.Boltzmann.Decay.Start   = 0
 * ; Strategy.Boltzmann.Decay.L       = 500
 * ; Strategy.Boltzmann.Decay.k       = 2
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Boltzmann implements Strategy {

    private Decay tau;

    public Boltzmann() {

    }
    public Boltzmann(Configuration cfg) {
        setConfiguration(cfg);
    }
    public Boltzmann(double tau, boolean shouldDecay, Decay.Type type, double over, double minimum, int start) {
        double L = 0.0, k = 0.0;
        this.tau = new Decay(type, L, k, minimum, over, tau, over);
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        double     tau         = cfg.getDouble("Strategy.Boltzmann.Tau");
        boolean    shouldDecay = cfg.getBoolean("Strategy.Boltzmann.ShouldDecay");
        Decay.Type type        = Decay.Type.valueOf(cfg.getString("Strategy.Boltzmann.Type").toUpperCase());
        double     over        = cfg.getDouble("Strategy.Boltzmann.Over");
        double     minimum     = cfg.getDouble("Strategy.Boltzmann.Minimum");
        int        start       = cfg.getInteger("Strategy.Boltzmann.Start");

        set(tau, shouldDecay, type, over, minimum, start);
    }

    private void set(double tau, boolean shouldDecay, Decay.Type type, double over, double minimum, int start) {
        double L = 0.0, k = 0.0;
        this.tau = new Decay(type, L, k, minimum, over, tau, over);
    }

    /**
     * @param actionValuePairs An array of values for the index action choice
     * @param time             Time step
     * @return The Boltzmann soft max action
     */
    @Override
    public int select(double[] actionValuePairs, int time) {
        double random     = ThreadLocalRandom.current().nextDouble();
        double lowerBound;
        double upperBound = 0.0;
        double sumExp     = sumExponent(actionValuePairs, time);

        for (int i = 0; i < actionValuePairs.length; i++) {
            lowerBound = upperBound;
            upperBound += getExponent(actionValuePairs[i], time) / sumExp;
            if (random >= lowerBound && random < upperBound) {
                return i;
            }
        }

        return 0;
    }

    private double sumExponent(double[] actionValuePairs, int time) {
        double sum = 0.0;
        for (double value : actionValuePairs) {
            sum += getExponent(value, time);
        }

        return sum;
    }

    private double getExponent(double value, int time) {
        if (value == 0.0) {
            return Math.exp(0.0);
        } else {
            return Math.exp(value / tau.decay(time));
        }
    }
}
