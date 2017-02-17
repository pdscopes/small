package org.madesimple.small.agent.strategy;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.utility.Configuration;

import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * Epsilon-First consists of a pure exploration phase followed by a pure
 * exploitation phase. For <em>N</em> steps in total, the exploration phase
 * occupies <em>&epsilon;N</em> steps and the exploitation phase
 * <em>(1 - &epsilon;)N</em> steps. During the exploration phase, actions are
 * chosen randomly (with uniform probability); during the exploitation phase,
 * the action with the highest value is always chosen.
 * </p>
 * <p>
 * Epsilon-First has an internal counter keeping track of the step number,
 * <em>n</em>. If a new episode is started and the step counter should be reset
 * call {@link #reset()}. When <em>N</em> steps have been performed
 * Epsilon-First returns to exploration actions until a further
 * <em>&epsilon;N</em> steps have been performed.
 * </p>
 * <p>
 * Properties required to run EpsilonFirst in a configuration file:
 * </p>
 * <pre>
 * ## Epsilon-First Selection Settings
 * Strategy.EpsilonFirst.Epsilon = 0.1
 * ; Strategy.EpsilonFirst.N       = 500
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class EpsilonFirst implements Strategy {
    /**
     * The percentage of the steps, <em>N</em>, that are exploration actions.
     */
    private double epsilon;

    /**
     * The number of steps in the cycle.
     */
    private int N;

    /**
     * The calculated value of epsilon * N.
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
        int N = cfg.getInteger("Strategy.EpsilonFirst.N");

        set(epsilon, N);
    }

    private void set(double epsilon, int N) {
        this.epsilon = epsilon;
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
