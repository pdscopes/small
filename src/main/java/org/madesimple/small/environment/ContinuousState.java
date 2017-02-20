package org.madesimple.small.environment;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface ContinuousState extends State {

    /**
     * @param feature feature
     * @return double
     */
    double tuple(int feature);

    /**
     * Convert the state to an integer array.
     *
     * @return identifiable tuple
     */
    double[] tuple();
}
