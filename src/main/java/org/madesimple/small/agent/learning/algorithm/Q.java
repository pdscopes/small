package org.madesimple.small.agent.learning.algorithm;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.agent.learning.LearningAlgorithm;
import org.madesimple.small.agent.learning.storage.QTable;
import org.madesimple.small.agent.strategy.Argmax;
import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.State;
import org.madesimple.small.agent.learning.storage.qtable.ActionValueTable;
import org.madesimple.small.utility.Configuration;

import java.io.File;

/**
 * <p>
 * Discrete Q-Learning is an class which uses a discrete Q-Table to
 * store Q values for each state representation given to it and all actions
 * available in that state representation.
 * </p>
 * <p>
 * The discrete nature of this learning algorithm is in actions; that is to say
 * that actions must be natural numbers not doubles. Furthermore the algorithm
 * looks at the values of actions not the indices and casts
 * them to an integer when learning about them.
 * </p>
 * <p>
 * <em>Note:</em> This version of Q-Learning only can handle one action at a
 * time to learn about.
 * </p>
 * <p>
 * To use this or any of its derivatives the following is needed in the
 * configuration file:
 * </p>
 * <pre>
 * ## Q-Learning Learning Settings
 * LearningAlgorithm.Q.Alpha = 0.4
 * LearningAlgorithm.Q.Gamma = 0.999
 * LearningAlgorithm.Q.Strategy = org.madesimple.small.agent.strategy.EpsilonGreedy
 * LearningAlgorithm.Q.InitialValue = 0.0d
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Q implements LearningAlgorithm {
    protected Configuration cfg;
    protected QTable        qTable;
    protected Strategy      strategy;
    protected double        alpha;
    protected double        gamma;

    public Q() {
        this.qTable = new ActionValueTable();
    }

    public Q(double alpha, double gamma, Strategy strategy) {
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
        alpha = cfg.getDouble("LearningAlgorithm.Q.Alpha");
        gamma = cfg.getDouble("LearningAlgorithm.Q.Gamma");

        // Initialise the action-value table
        qTable.setInitialValue(cfg.getDouble("LearningAlgorithm.Q.InitialValue"));

        // Initialise the strategy
        strategy = null;
        if (cfg.hasProperty("LearningAlgorithm.Q.Strategy")) {
            try {
                Object instance = cfg.getInstance("LearningAlgorithm.Q.Strategy");
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
    public void commence(Environment environment) {

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
        double[] qValues = qTable.get(state.hashCode(), state.availableActions());

        if (greedy) {
            return Argmax.select(qValues);
        } else {
            return strategy.select(qValues, time);
        }
    }

    @Override
    public void clearTransitions() {

    }

    @Override
    public void update(State s, int a, State s_, double r) {
        // Get the last Q values
        double[] lastQValues = qTable.get(s.hashCode(), s.availableActions());

        // Get the old and max Q values
        double oldQ = lastQValues[a];
        double maxQ = s_ != null ? Argmax.max(qTable.get(s_.hashCode(), s.availableActions())) : 0.0;

        // Calculate the new Q value
        double Delta = r + (gamma * maxQ) - oldQ;
        double newQ  = oldQ + (alpha * Delta);
        qTable.put(s.hashCode(), a, newQ, s.availableActions());
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
