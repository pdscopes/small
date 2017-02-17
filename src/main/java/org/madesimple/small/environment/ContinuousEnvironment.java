package org.madesimple.small.environment;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface ContinuousEnvironment {
    double[] lowerBounds();
    double[] upperBounds();
}
