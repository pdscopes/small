package org.madesimple.small.agent;

import org.madesimple.small.utility.Configurable;

/**
 * <p>
 * The Strategy interface defines a means for agents to choose between different possible actions based upon some
 * numerical valuation.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface Strategy extends Configurable {
    /**
     * This method should select one of the indices of the given array and
     * return it. How it is decided and what the values should represent are
     * defined by the implementing class.
     *
     * @param actionValuePairs An array of values for the index action choice
     * @param time             Time step
     * @return The index action that has been selected
     */
    int select(double[] actionValuePairs, int time);
}
