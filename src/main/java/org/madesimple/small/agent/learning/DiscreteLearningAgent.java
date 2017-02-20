package org.madesimple.small.agent.learning;

import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.State;
import org.madesimple.small.utility.Configuration;

import java.io.File;

/**
 * <pre>
 * Agent.LearningAlgorithm=org.madesimple.small.agent.learning.algorithm.Q
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class DiscreteLearningAgent extends LearningAgent {
    private   Configuration     cfg;
    private   LearningAlgorithm learning;
    private   State             actionState;
    private   State             rewardState;
    private   int               action;
    private   boolean           retrieved;
    /**
     * Accumulative reward received since last reset.
     */
    protected double            accumulativeReward;

    public DiscreteLearningAgent() {
        this.retrieved = false;
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        this.cfg = cfg;
    }

    @Override
    public void initialise() {
        if (!retrieved) {
            try {
                // Initialise the learning algorithm
                learning = (LearningAlgorithm) cfg.getInstance("Agent.LearningAlgorithm");
                learning.setConfiguration(cfg);
                learning.initialise();
            } catch (Exception e) {
                throw new RuntimeException(e.getCause());
            }
        }

        // Clear the retrieved flag
        retrieved = false;
    }

    @Override
    public void reset(Environment environment) {
        // Reset the cumulative reward
        accumulativeReward = 0.0;

        // Commence the learning
        learning.commence(environment);
    }

    @Override
    public void add(Environment environment, State state) {
        this.actionState = state.copy();
        this.rewardState = state.copy();
    }

    @Override
    public void remove(Environment environment) {

    }

    @Override
    public int act(Environment environment, State state) {
        actionState.set(state);
        action = learning.select(state, 0, evaluationMode);
        return action;
    }

    @Override
    public void receive(Environment environment, State arrived, double reward) {
        super.receive(environment, arrived, reward);

        // accumulate the reward
        accumulativeReward += reward;
    }

    @Override
    protected void update(Environment environment, State arrived, double reward) {
        rewardState.set(arrived);
        learning.update(actionState, action, rewardState, reward);
        if (environment.isTerminal(this, arrived)) {
            learning.conclude();
        }
    }

    @Override
    public double accumulativeReward(Environment environment) {
        return accumulativeReward;
    }

    public boolean savePolicy(File file) {
        return learning.savePolicy(file);
    }

    public boolean loadPolicy(File file) {
        if (learning == null) {
            initialise();
        }
        if (learning.loadPolicy(file)) {
            retrieved = true;
            return true;
        }

        return false;
    }
}
