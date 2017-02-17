package org.madesimple.small.agent.learning;

import org.madesimple.small.agent.Policy;
import org.madesimple.small.environment.State;

import java.io.File;

/**
 * <p>
 * LearningAlgorithm is an interface which describes how an agent should be able
 * to interact with learning algorithms in general. This allows for each agent
 * to learn individually or collectively (in the case of a centralised learner).
 * Learning algorithms should be able to:
 * </p>
 * <ul>
 * <li>Select an action based solely on the state provided</li>
 * <li>Update itself based upon the state it was in, the state it moved into,</li>
 * <li>the action taken, and the received reward for said state transition</li>
 * <li>Be informed of the number of actions currently available to select from</li>
 * </ul>
 * <p>
 * The library provides a fair few different learning algorithms built in and
 * ready to use and has separated them out in such a way that customisations
 * should be easy.
 * </p>
 * <p>
 * For example: If your agent must decide upon which action to take and one
 * needs a magnitude you could create an extra method in your learning algorithm
 * to account for this extra information to be called after said action has
 * been selected.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface LearningAlgorithm extends Policy {

    /**
     * Initialise the LearningAlgorithm ready to learn. This should clear all knowledge learnt.
     */
    void initialise();

    /**
     * Informs the LearningAlgorithm that learning is about to commence. Should forget transition history.
     */
    void commence();

    /**
     * Given <em>state</em> this method should return the action that should be performed.
     *
     * @param state  Current state
     * @param time   Time step
     * @param greedy True if should greedy select
     * @return Action to perform
     */
    int select(State state, int time, boolean greedy);

    /**
     * Clear the LearningAlgorithm's transition history.
     */
    void clearTransitions();

    /**
     * Add a transition to the LearningAlgorithm.
     *
     * @param s  MdpState where the action was performed
     * @param a  Action performed
     * @param s_ MdpState transitioned to
     * @param r  Numeric amount
     */
    void update(State s, int a, State s_, double r);

    /**
     * Informs the LearningAlgorithm that learning has concluded for now.
     */
    void conclude();

    /**
     * Load the strategy stored in <em>file</em>.
     *
     * @param file Policy file
     * @return True on success, false on failure
     */
    boolean loadPolicy(File file);

    /**
     * Save the strategy in <em>file</em>.
     *
     * @param file Policy file
     * @return True on success, false on failure
     */
    boolean savePolicy(File file);
}
