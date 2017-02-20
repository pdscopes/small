package org.madesimple.small.environment;

/**
 * <p>
 * A Turn-based Environment is an {@link Environment} where time progresses
 * partitioned, well-defined and visible parts called <em>turns</em>. An agent
 * in a turn-based environment is allowed a period of analysis (sometimes
 * bounded, sometimes unbounded) before performed an action, ensuring a
 * separation between the actions and the thinking process. Once every agent
 * has taken its turn, that round is over and the environment progresses to
 * the next round, unless a terminal state is reached or a specified number of
 * rounds has occurred.
 * </p>
 * <p>
 * Turn-based environments come in two main forms depending on whether, within
 * a turn, agents take their turn simultaneously or take their turns in
 * sequence. The former environments fall under the category of
 * <em>simultaneously executed</em> environments (also called
 * <em>phased-based</em> or &quot;We-Go&quot;). The latter environments fall
 * into <em>agent-alternated</em> environments (also called
 * &quot;I-Go-You-Go&quot;, or &quot;IGOUGO&quot; for short), and are further
 * subdivided into (A) ranked, (B) round-robin, and (C) random
 * </p>
 * <dl>
 * <dt>Simultaneous</dt><dd>all player play at the same time</dd>
 * <dt>Sequence: Ranked</dt><dd>the first player being the same every time</dd>
 * <dt>Sequence: Round-Robin</dt><dd>the first player selection strategy is round-robin</dd>
 * <dt>Sequence: Random</dt><dd>the first player is selected at random</dd>
 * </dl>
 * <a href="http://en.wikipedia.org/wiki/Turns,_rounds_and_time-keeping_systems_in_games#Turn-based">
 * http://en.wikipedia.org/wiki/Turns,_rounds_and_time-keeping_systems_in_games#Turn-based</a>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface TurnBasedEnvironment extends Environment {

    enum Turn {
        SIMULTANEOUS, SEQUENTIAL_RANKED,
        SEQUENTIAL_ROUNDROBIN, SEQUENTIAL_RANDOM
    }

    /**
     * Return the environment to the beginning of an episode.
     */
    void restart();

    /**
     * Place the environment is a random, valid state.
     */
    void reseed();

    /**
     * Return the maximum number of turns allowed in a single episode.
     *
     * @return Number of turns per episode allowed
     */
    int maxTurns();

    /**
     * <p>
     * This method should firstly call each agent that has been added to
     * perform a step, which in turn should notify the environment what
     * its intended action is. The environment can decide upon the ordering.
     * Once all agents have performed their step the environment should
     * perform any conflict resolution; conflicts can be resolved in the
     * following ways:
     * </p>
     * <ul>
     * <li>mutually = decline all agents involved in the conflict</li>
     * <li>orderly  = decline agents in a specified order, such as priority</li>
     * <li>randomly = decline agents in a random order</li>
     * </ul>
     * <p>
     * Finally this method should inform the agent(s) of their reward for
     * the state->action->state transition.
     * </p>
     * <p>
     * <em>Note</em>: That if the agent has reached a terminal state then the method
     * should also call the agents update terminal method with its reward
     * for entering the goal.
     * </p>
     * <p>
     * <em>Note</em>: The time counter should be incremented each step.
     * state.
     * </p>
     */
    void performTurn();
}
