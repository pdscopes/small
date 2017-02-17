package org.madesimple.small.agent.strategy;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.utility.Configuration;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Random implements Strategy {

    @Override
    public void setConfiguration(Configuration cfg) {
        // nothing to do...
    }

    /**
     * Selects an action using the Random selection mechanism.
     *
     * @param actionValuePairs An array of values for the index action choice
     * @param time             Time step
     * @return The index action that has been selected
     */
    @Override
    public int select(double[] actionValuePairs, int time) {
        return ThreadLocalRandom.current().nextInt(actionValuePairs.length);
    }
}
