package org.madesimple.small.mdp;

import org.madesimple.small.environment.JointState;
import org.madesimple.small.environment.State;
import org.madesimple.small.utility.Hash;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * A Transition Matrix is a matrix for state <em>s</em> of states
 * <em>s' &isin; S</em> and actions <em>a &isin; A</em> that holds
 * the probability of the transition function <em>T(s,a,s')</em>
 * from state <em>s</em> of a particular action <em>a</em> arriving
 * in state <em>s'</em>.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class TransitionMatrix {

    /**
     * The state this transition matrix is modelling from.
     */
    private JointState           state;
    /**
     * The probabilities of an action arriving a particular state
     * given the action used.
     */
    private Map<Integer, Double> probabilities;
    private Set<JointState>      states;
    private Set<Integer>         actions;


    /**
     * Creates a new Transition Matrix for the specified state that
     * expects nStates and nActions to be added.
     *
     * @param state    The state this transition matrix is for
     */
    public TransitionMatrix(JointState state) {
        this.state = state;

        probabilities = new HashMap<>();
        states        = new HashSet<>();
        actions       = new HashSet<>();
    }

    /**
     * Sets the <em>probability</em> of arriving in <em>state</em> if
     * <em>action</em> was performed.
     * @param action      The action performed
     * @param state       The state transitioned to
     * @param probability The probability
     */
    public void set(int action, JointState state, double probability) {
        int hash = Hash.pair(action, state.hashCode());
        actions.add(action);
        states.add(state);

        if( probability > 0.0d )
            probabilities.put(hash, probability);
    }

    /**
     * Gets the probability of arriving in <em>state</em> when <em>action</em>
     * is performed. By default this returns 0.0d if there is no matching
     * action/state in the transition matrix.
     *
     * @param action The action being performed
     * @param state  The state to arrive in
     * @return The probability of arriving in the specified state when
     *         the specified action is performed
     */
    public double get(int action, JointState state) {
        int hash = Hash.pair(action, state.hashCode());
        Double d = probabilities.get(hash);
        if( d != null )
            return d;
        else
            return 0.0d;
    }

    /**
     * @return The state this transition matrix is defined for
     */
    public State getState() {
        return state;
    }
    /**
     * The state that have been set.
     * @return The states in this transition matrix
     */
    public JointState[] getStates() {
        return states.toArray(new JointState[states.size()]);
    }
    /**
     * The actions that have been set.
     * @return The actions in this transition matrix
     */
    public Integer[] getActions() {
        return actions.toArray(new Integer[actions.size()]);
    }
}
