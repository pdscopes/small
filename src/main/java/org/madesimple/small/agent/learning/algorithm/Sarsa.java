package org.madesimple.small.agent.learning.algorithm;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.agent.learning.LearningAlgorithm;
import org.madesimple.small.agent.strategy.Argmax;
import org.madesimple.small.environment.State;
import org.madesimple.small.agent.learning.storage.ActionValueTable;
import org.madesimple.small.utility.Configuration;

import java.io.File;

/**
 * <p>
 * Discrete SARSA is an abstract class which uses a discrete Q-Table to store
 * Q values for each state representation given to it and all actions available
 * in that state representation.
 * </p>
 * <p>
 * The discrete nature of this learning algorithm is in actions; that is to say
 * that actions must be natural numbers not doubles. Furthermore the algorithm
 * looks at the values of actions not the indices and casts them to an integer
 * when learning about them.
 * </p>
 * <p>
 * <em>Note:</em> This version of SARSA can only handle one action at a time to#
 * learn about.
 * </p>
 * <p>
 * To use this or any of its derivatives the following is need in the
 * configuration file:
 * </p>
 * <pre>
 * ## SARSA Learning Settings
 * LearningAlgorithm.SARSA.Alpha     = 0.4
 * LearningAlgorithm.SARSA.Gamma     = 0.999
 * ; LearningAlgorithm.SARSA.NumStates = 2000
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Sarsa implements LearningAlgorithm {
    protected Configuration    cfg;
    protected ActionValueTable qTable;
    protected Strategy         strategy;
    protected double           alpha;
    protected double           gamma;

    protected boolean hasPotentialState;
    protected State   potentialState;
    protected int     potentialAction;

    public Sarsa() {
        this.qTable = new ActionValueTable();
    }

    public Sarsa(double alpha, double gamma, Strategy strategy) {
        this();
        this.alpha = alpha;
        this.gamma = gamma;
        this.strategy = strategy;
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        this.cfg = cfg;
    }

    @Override
    public void initialise() {
        // Initialise alpha/gamma
        alpha = cfg.getDouble("LearningAlgorithm.SARSA.Alpha");
        gamma = cfg.getDouble("LearningAlgorithm.SARSA.Gamma");

        // Initialise the strategy
        strategy = null;
        if (cfg.hasProperty("LearningAlgorithm.SARSA.Strategy")) {
            try {
                Object instance = cfg.getInstance("LearningAlgorithm.SARSA.Strategy");
                if (instance instanceof Strategy) {
                    strategy = (Strategy) instance;
                    strategy.setConfiguration(cfg);
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void commence() {
        clearTransitions();
    }

    @Override
    public void conclude() {

    }

    @Override
    public int select(State state, int time) {
        return select(state, time, false);
    }

    @Override
    public int select(State state, int time, boolean greedy) {
        if (greedy || !hasPotentialState || state.hashCode() != potentialState.hashCode()) {
            return Argmax.select(qTable.get(state.hashCode(), state.availableActions()));
        } else {
            return potentialAction;
        }
    }

    protected int selectPotential(State state, int time) {
        hasPotentialState = true;
        potentialState = state;
        potentialAction = strategy.select(qTable.get(state.hashCode(), state.availableActions()), time);

        return potentialAction;
    }

    @Override
    public void clearTransitions() {
        hasPotentialState = false;
        potentialState = null;
        potentialAction = -1;
    }

    @Override
    public void update(State s, int a, State s_, double r) {
        // Get the last Q values
        double[] lastQValues = qTable.get(s.hashCode(), s.availableActions());

        // Get the old and next Q values
        double oldQ  = lastQValues[a];
        double nextQ = s_ != null ? selectPotential(s_, 0) : 0.0;

        // Calculate the new Q value
        double Delta = r + (gamma * nextQ) - oldQ;
        double newQ  = oldQ + (alpha * Delta);

        qTable.put(s.hashCode(), a, newQ);
    }

    @Override
    public boolean savePolicy(File file) {
        return qTable.save(file);
    }

    @Override
    public boolean loadPolicy(File file) {
        return qTable.load(file);
    }
}
