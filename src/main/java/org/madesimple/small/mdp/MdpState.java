package org.madesimple.small.mdp;

import org.madesimple.small.environment.JointAction;
import org.madesimple.small.environment.State;
import org.madesimple.small.utility.Hash;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * An MDP MdpState consists of a label, and a set of transition probabilities,
 * and a set of connected states. If two states have the same label it is
 * assumed they are the same state.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class MdpState implements State {

    /**
     * The label for the MDP MdpState.
     */
    protected String               label;
    /**
     * A Set of connected states.
     */
    protected Set<MdpState>        connected;
    /**
     * A Set of available actions.
     */
    protected Set<JointAction>     actions;
    /**
     * The probability of arriving in a state based on a
     * specified action being performed.
     */
    protected Map<Integer, Double> probabilities;

    /**
     * Create a new MDP MdpState with the specified label.
     * @param label The label for this MDP
     */
    public MdpState(String label) {
        this.label = label;
        this.connected = new HashSet<>();
        this.actions = new HashSet<>();
        this.probabilities = new HashMap<>();
    }

    /**
     * Add a connection between this MDP MdpState and the specified state. The connection
     * will be for the given action and have the given probability.
     *
     * @param state  The state to connect to
     * @param action The action that should be performed
     * @param probability The probability of the state transition
     */
    public void addConnection(MdpState state, JointAction action, double probability) {
        int hash = Hash.pair(action.get(), state.hashCode());
        actions.add(action);
        connected.add(state);

        if( probability > 0.0d )
            probabilities.put(hash, probability);
    }

    /**
     * @return All state that have had connections added
     */
    public Set<MdpState> getConnected() {
        return connected;
    }
    /**
     * @return All actions that have had connections added
     */
    public Set<JointAction> getActions() {
        return actions;
    }

    /**
     * Returns all the states, with given probabilities greater than zero,
     * that can be arrived in from the specified action.
     * @param action The action being performed
     * @return A Map of MDP States and the probabilities of arriving in that state
     */
    public Map<MdpState, Double> getStates(int action) {
        Map<MdpState, Double> states = new HashMap<>();
        if( actions.contains(action) )
            for( MdpState state: connected ) {
                int    hash = Hash.pair(action, state.hashCode());
                Double prob = probabilities.get(hash);
                if( prob != null )
                    states.put(state, prob);
            }

        return states;
    }
    /**
     * Returns all actions, with given probabilities greater than zero,
     * that can be performed to arrive in the specified state.
     * @param state The state to arrive in
     * @return A Map of actions and the probabilities of arriving in the specified state
     */
    public Map<JointAction, Double> getActions(MdpState state) {
        Map<JointAction, Double> as = new HashMap<>();
        if( connected.contains(state) )
            for( JointAction action: actions ) {
                int    hash = Hash.pair(action.get(), state.hashCode());
                Double prob = probabilities.get(hash);
                if( prob != null )
                    as.put(action, prob);
            }

        return as;
    }


    /* (non-Javadoc)
     * @see marl.environments.MdpState#set(marl.environments.MdpState)
     */
    @Override
    public void set(State s) {
        if( s instanceof MdpState ) {
            MdpState that = (MdpState) s;

            this.label = that.label;
            this.connected.clear();
            this.actions.clear();
            this.probabilities.clear();

            this.connected.addAll(that.connected);
            this.actions.addAll(that.actions);
            this.probabilities.putAll(that.probabilities);
        }
    }



    @Override
    public boolean equals(Object obj) {
        if( obj instanceof MdpState )
            return label.equals(((MdpState) obj).label);
        return false;
    }
    @Override
    public int hashCode() {
        return label.hashCode();
    }
    @Override
    public String toString() {
        return label;
    }

}
