package org.madesimple.small.agent;

import org.madesimple.small.agent.learning.LearningAgent;
import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.State;
import org.madesimple.small.utility.Configurable;

/**
 * <p>
 * An Agent of an Environment can <em>perceive</em> its current state within its Environment and <em>act</em> upon
 * its current Environment. An extending class should <em>not</em> be able to learn rather it should be purely
 * a reaction based Agent.
 * </p>
 * <p>
 * If a LearningAgent is required see {@link LearningAgent}
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 * @see Environment
 * @see State
 * @see LearningAgent
 */
public interface Agent extends Configurable {

    /**
     * Return the agent to its initial conditions.
     */
    void initialise();

    /**
     * Return the agent to its condition when it first enters an environment.
     *
     * @see #add(Environment)
     */
    void reset(Environment environment);

    /**
     * Perform any actions required for being added to <em>environment</em>.
     *
     * @param environment Environment agent was added to
     * @param state       Initial state of the agent
     * @see #reset(Environment)
     */
    void add(Environment environment, State state);

    /**
     * Perform any actions required for being removed from <em>environment</em>.
     *
     * @param environment Environment agent was removed from
     */
    void remove(Environment environment);

    /**
     * Act upon <em>environment</em>.
     *
     * @param environment Environment to act upon
     * @param state       MdpState to act from
     * @return Action to perform
     */
    int act(Environment environment, State state);

    /**
     * Receive <em>reward</em> from <em>environment</em>. The agent can assume that its perception history is true as to
     * why the it received this reward.
     *
     * @param environment Environment given reward
     * @param arrived     MdpState agent arrived in
     * @param reward      Numeric amount
     */
    void receive(Environment environment, State arrived, double reward);

    /**
     * @param environment Environment to get the reward associated
     * @return Amount of reward
     */
    double accumulativeReward(Environment environment);
}