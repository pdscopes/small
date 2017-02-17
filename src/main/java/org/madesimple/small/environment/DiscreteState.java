package org.madesimple.small.environment;

import org.madesimple.small.utility.Hash;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class DiscreteState implements State {

    /**
     * Convert the state to an integer array.
     *
     * @return identifiable tuple
     */
    public abstract int[] tuple();

    /**
     * Allow the MdpState to be hashed.
     *
     * @return hash code representation
     */
    public int hashCode() {
        return Hash.pair(tuple());
    }
}
