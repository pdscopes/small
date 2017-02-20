package org.madesimple.small.environment;

/**
 * <p>
 * State is an interface which defines a the key aspects of an environmental
 * state. States must be able to be copied such that once they have been
 * copied if the equals(Object that) method was called it would return true.
 * The equals method must also be properly implemented so that two states may
 * be compared to see if they are the same. Finally a state should be able to
 * provide a unique hash code; clearly if one, or more, of the state features
 * are continuous rather than discrete then this method is non-sensical and
 * can be ignored though it should be noted that this will be parts of the
 * library like the DiscreteQTable will not properly work.
 * </p>
 * <p>
 * Note. If you do have a continuous environment you could make the State check
 * a discretised version of the state in the equals and hashCode method but it
 * must be the same in both methods (i.e. if the two objects are equal then
 * their hash codes should be the same also).
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface State {

    /**
     * @return Copied state
     */
    State copy();

    /**
     * Copy the state information of <em>that</em> into this state such that if {@link #equals(Object)} method was then
     * called between this and <em>that</em> it would return <code>true</code>.
     *
     * @param that State
     */
    void set(State that);

    /**
     * Allow States to be compared to one another.
     *
     * @param that Object to compare
     * @return True if states are equivalent, false otherwise
     */
    boolean equals(Object that);

    /**
     * Returns the number of available actions in this state.
     *
     * @return Number of available actions
     */
    int availableActions();

    /**
     * Number of features this a state in this environment has.
     * @return Number of features
     */
    int countFeatures();
}
