package org.madesimple.small.agent.learning;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.State;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class LearningAgent implements Agent {
    /**
     * Flag of whether the agent should be learning.
     */
    protected boolean evaluationMode = false;

    /**
     * Set the LearningAgent into evaluation mode.
     *
     * @param active True if in evaluation mode, false otherwise
     */
    public void setEvaluationMode(boolean active) {
        evaluationMode = active;
    }

    /**
     * Initialise the agent so that is is ready to be added into an environment. This should include setting the
     * learning algorithm and action selection strategy.
     *
     * @see Agent#initialise()
     */
    @Override
    public abstract void initialise();

    @Override
    public abstract void reset(Environment environment);

    @Override
    public void receive(Environment environment, State arrived, double reward) {
        // Update the agents knowledge
        if (!evaluationMode) {
            update(environment, arrived, reward);
        }
    }

    /**
     * <p>
     * Called by Environment through the {@link LearningAgent::receive} method. This should update the learning
     * algorithm with the reward given for the latest action. The agent should also perceive its environment at this
     * point to see what it's latest action did.
     * </p>
     *
     * @param environment Environment given reward
     * @param arrived     MdpState agent arrived in
     * @param reward      Reward received for previous action
     */
    protected abstract void update(Environment environment, State arrived, double reward);
}
