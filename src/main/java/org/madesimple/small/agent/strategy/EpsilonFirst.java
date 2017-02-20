package org.madesimple.small.agent.strategy;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.utility.Configuration;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * The &epsilon;-First action selection strategy consists of a pure exploration phase followed by a pure exploitation
 * phase. These phases can be cycled, however it is not recommended. For <em>N</em> steps in the cycle, the exploration
 * phase occupies <code>&epsilon; * N</code> steps and the exploitation phases <code>(1 - &epsilon;) * N</code> steps.
 * </p>
 * <p>
 * During the exploration phase actions are selected purely at random, with uniform probability. During the exploitation
 * phase the <em>Argmax</em> value of possible action-value pairs.
 * </p>
 * <p>
 * The default properties for EpsilonFirst are:
 * </p>
 * <pre>
 * ## Epsilon-First Selection Settings
 * Strategy.EpsilonFirst.Epsilon = 0.1
 * Strategy.EpsilonFirst.N       = 500
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class EpsilonFirst implements Strategy {
    /**
     * The number of steps in the cycle.
     */
    private int N;

    /**
     * <p>
     * The calculated value of <code>&epsilon; * N</code>.
     * </p>
     * <dl>
     * <dt>&epsilon;</dt><dd>Percentage of steps that are exploration actions</dd>
     * <dt>N</dt><dd>Number of steps in exploration/exploitation cycle</dd>
     * </dl>
     */
    private double epsilonN;

    public EpsilonFirst() {

    }

    public EpsilonFirst(Configuration cfg) {
        setConfiguration(cfg);
    }

    public EpsilonFirst(double epsilon, int N) {
        set(epsilon, N);
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        double epsilon = cfg.getDouble("Strategy.EpsilonFirst.Epsilon");
        int    N       = cfg.getInteger("Strategy.EpsilonFirst.N");

        set(epsilon, N);
    }

    private void set(double epsilon, int N) {
        this.N = N;
        epsilonN = epsilon * N;
    }

    @Override
    public int select(double[] actionValuePairs, int time) {
        if ((time % N) < epsilonN) {
            return ThreadLocalRandom.current().nextInt(actionValuePairs.length);
        } else {
            return Argmax.select(actionValuePairs);
        }
    }
}
