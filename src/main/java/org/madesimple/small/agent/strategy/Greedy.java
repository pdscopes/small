package org.madesimple.small.agent.strategy;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.utility.Configuration;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Greedy implements Strategy {

    @Override
    public void setConfiguration(Configuration cfg) {
        // Nothing to do...
    }

    /**
     * @param actionValuePairs An array of values for the index action choice
     * @return The greedy action
     */
    @Override
    public int select(double[] actionValuePairs, int time) {
        return Argmax.select(actionValuePairs);
    }
}
