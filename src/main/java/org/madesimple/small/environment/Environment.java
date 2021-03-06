package org.madesimple.small.environment;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.utility.Configurable;

/**
 * <p>
 * An Environment is a domain in which a set Agents (see {@link Agent}) can interact. An Environment should keep track
 * of the entire state of the domain and the agents that are present in the domain. An Environment must contain all the
 * logic for interactions with agents; this could mean an Environment is a middleware wrapper class to connect agents to
 * an external simulation or actually contain the modelling code itself.
 * </p>
 * <p>
 * Please see the abstract methods for further information.
 * </p>
 * <p>
 * There are 2 known sub-classes of Environment:
 * </p>
 * <dl>
 * <dt>{@link TurnBasedEnvironment}</dt>
 * <dd>For environments where all agents must perform an action before time can continue.</dd>
 * <dt>{@link RealTimeEnvironment}</dt>
 * <dd>For environments where time progresses regardless of whether agents perform actions.</dd>
 * </dl>
 * <p>
 * <a href="http://en.wikipedia.org/wiki/Turns,_rounds_and_time-keeping_systems_in_games">
 * http://en.wikipedia.org/wiki/Turns,_rounds_and_time-keeping_systems_in_games</a>
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface Environment extends Configurable {
    /**
     * A generic container, to be extended when necessary, to hold an environment's information for a particular Agent.
     *
     * @author Peter Scopes (peter.scopes@gmail.com)
     */
    class Tuple {
        /**
         * The Agent.
         */
        public Agent agent;

        /**
         * The state associated with this agent, that is the state of the environment the agent can current perceive.
         */
        public State state;

        /**
         * A holder to store the potential next state of an agent before the environment fully commits to the state
         * transition.
         */
        public State next;
    }

    /**
     * Initialise the environment ready for agents to be added. This includes such actions as removing all current
     * agents. In this way this method should be able to be called more than once, i.e. at the beginning of each run.
     */
    void initialise();

    /**
     * Add <em>agent</em> to the environment.
     *
     * @param agent to be added
     * @return True if agent was added, false otherwise
     */
    boolean add(Agent agent);

    /**
     * Remove <em>agent</em> from the environment.
     *
     * @param agent to be removed
     * @return True if agent was removed, false otherwise
     */
    boolean remove(Agent agent);

    /**
     * @return number of agents that are currently in the environment
     */
    int agentCount();

    /**
     * @return number of agents this environment requires
     */
    int requiredAgentCount();

    /**
     * Tests the environment to see if an agent has arrived in a terminal state.
     *
     * @param agent Agent
     * @param state State
     * @return True if agent is in terminal state, false otherwise
     */
    boolean isTerminal(Agent agent, State state);

    /**
     * Tests the environment to see if all agents are in a terminal state.
     *
     * @return True if all agents are in a terminal state, false otherwise
     */
    boolean isTerminal();
}
