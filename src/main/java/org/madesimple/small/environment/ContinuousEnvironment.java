package org.madesimple.small.environment;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface ContinuousEnvironment {
    /**
     * @return number of state features
     */
    int countBounds();

    /**
     * @return set of lower bounds of state features
     */
    double[] lowerBounds();

    /**
     * @return set of upper bounds of state features
     */
    double[] upperBounds();
}
