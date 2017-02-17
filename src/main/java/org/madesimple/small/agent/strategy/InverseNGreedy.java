package org.madesimple.small.agent.strategy;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.utility.Configuration;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class InverseNGreedy implements Strategy {
    @Override
    public void setConfiguration(Configuration cfg) {
        // nothing to do...
    }

    @Override
    public int select(double[] actionValuePairs, int time) {
        double epsilon = time > 0 ? (1.0 / (double) time) : 1.0;

        if (epsilon > ThreadLocalRandom.current().nextDouble()) {
            return ThreadLocalRandom.current().nextInt(actionValuePairs.length);
        } else {
            return Argmax.select(actionValuePairs);
        }
    }
}
