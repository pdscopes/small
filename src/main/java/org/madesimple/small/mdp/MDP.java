package org.madesimple.small.mdp;

import org.madesimple.small.environment.JointState;

/**
 * <p>
 * An MDP is able to provide the transition function as transition matrix
 * of probabilities for a given joint state. It should also be able to provide
 * the current joint state of the environment.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface MDP {

    /**
     * @return The current JointState of the environment.
     */
    JointState getJointState();

    /**
     * Given a JointState <em>joint</em> this method should return a transition
     * matrix of probabilities. The matrix should contain all joint states that
     * are reachable from <em>joint</em> and all actions that can be performed.
     *
     * @param joint The joint state in question
     * @return The transition matrix of probabilities for the given joint state
     */
    TransitionMatrix getTransitionMatrix(JointState joint);
}
