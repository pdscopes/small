package org.madesimple.small.environment;

import org.madesimple.small.agent.Agent;

/**
 * A RealTimeEnvironment is an {@link Environment} where time progresses regardless of whether an {@link Agent} decides
 * to perform an action in a particular tick of time. A {@link org.madesimple.small.experiment.Simulation} should obey
 * {@link #tickSeparation()}.
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface RealTimeEnvironment extends Environment {

    /**
     * Return the environment to the beginning of time.
     */
    void restart();

    /**
     * Number of milliseconds between each tick of time.
     *
     * @return Number of milliseconds
     */
    long tickSeparation();

    /**
     * Return the current state of agent.
     * @param agent agent
     * @return current state of agent
     */
    State performPerception(Agent agent);

    /**
     * Set the action of agent for the next tick.
     * @param agent agent
     * @param action action
     */
    void performAction(Agent agent, int action);

    /**
     * Tick through time. Conflicts of actions should be resolved.. Compute the new state of this Environment after all
     * marked actions have been performed. Give all agents a reward for the action/inaction.
     */
    void tick();
}
