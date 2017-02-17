package org.madesimple.small.agent.strategy;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.utility.Configuration;
import org.madesimple.small.utility.Decay;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * EpsilonGreedy is an implementation of the &epsilon;-Greedy action
 * selection strategy.
 * </p>
 * <p>
 * The &epsilon;-Greedy strategy is to be greedy (select the Argmax action) with
 * probability 1-&epsilon; and then with &epsilon; probability select a random
 * action which could be the {@link Argmax} action.
 * </p>
 * <p>
 * Properties required to run EpsilonGreedy in a configuration file:
 * </p>
 * <pre>
 * ## E-Greedy Selection Settings
 * Strategy.EpsilonGreedy.Epsilon       = 0.4
 * Strategy.EpsilonGreedy.ShouldDecay   = true
 * Strategy.EpsilonGreedy.Decay.Type    = LINEAR
 * Strategy.EpsilonGreedy.Decay.Over    = 300
 * Strategy.EpsilonGreedy.Decay.Minimum = 0
 * Strategy.EpsilonGreedy.Decay.Start   = 0
 * ; Strategy.EpsilonGreedy.Decay.L       = 500
 * ; Strategy.EpsilonGreedy.Decay.K       = 2
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class EpsilonGreedy implements Strategy {
    /**
     * The probability that a random action will be taken over the greedy action.
     */
    private Decay epsilon;

    public EpsilonGreedy() {

    }
    public EpsilonGreedy(Configuration cfg) {
        setConfiguration(cfg);
    }
    public EpsilonGreedy(double epsilon, Decay.Type type, double over, double minimum, double start, double L, double k) {
        set(epsilon, type, over, minimum, start, L, k);
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        double     epsilon = cfg.getDouble("Strategy.EpsilonGreedy.Epsilon");
        Decay.Type type    = Decay.Type.valueOf(cfg.getString("Strategy.EpsilonGreedy.Type").toUpperCase());
        double     over    = cfg.getDouble("Strategy.EpsilonGreedy.Over");
        double     minimum = cfg.getDouble("Strategy.EpsilonGreedy.Minimum");
        double     start   = cfg.getDouble("Strategy.EpsilonGreedy.Start");
        double     L       = cfg.getDouble("Strategy.EpsilonGreedy.L");
        double     k       = cfg.getDouble("Strategy.EpsilonGreedy.k");

        set(epsilon, type, over, minimum, start, L, k);
    }

    private void set(double epsilon, Decay.Type type, double over, double minimum, double start, double L, double k) {
        this.epsilon = new Decay(type, L, k, minimum, epsilon, start, over);
    }

    /**
     * Selects an action using the Epsilon Greedy selection mechanism.
     *
     * @param actionValuePairs An array of values for the index action choice
     * @param time             Time step
     * @return The index action that has been selected
     */
    @Override
    public int select(double[] actionValuePairs, int time) {
        // With epsilon probability choose a random action
        if (epsilon.decay(time) > ThreadLocalRandom.current().nextDouble()) {
            return ThreadLocalRandom.current().nextInt(actionValuePairs.length);
        } else {
            return Argmax.select(actionValuePairs);
        }
    }
}
