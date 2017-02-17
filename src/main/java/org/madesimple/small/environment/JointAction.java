package org.madesimple.small.environment;

import org.madesimple.small.utility.Hash;

import java.util.Arrays;

/**
 * <p>
 * A Joint Action is a collection of actions for <em>n</em> number of agents.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class JointAction {

    /**
     * The set of actions.
     */
    private int[] actions;

    /**
     * Create a single Joint Action.
     *
     * @param action The single action
     */
    public JointAction(int action) {
        actions = new int[]{action};
    }

    /**
     * Create a Joint Action consisting of the specified actions
     *
     * @param actions The actions that make up the joint action
     */
    public JointAction(int... actions) {
        this.actions = actions;
    }

    public int size() {
        return actions.length;
    }

    /**
     * @param nth The action
     * @return Returns the nth action
     */
    public int get(int nth) {
        return actions[nth];
    }

    /**
     * @return Get the Joint Action number
     */
    public int get() {
        return Hash.pair(actions);
    }

    @Override
    public int hashCode() {
        return get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JointAction) {
            JointAction that = (JointAction) obj;
            return this.get() == that.get();
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return Arrays.toString(actions);
    }
}
