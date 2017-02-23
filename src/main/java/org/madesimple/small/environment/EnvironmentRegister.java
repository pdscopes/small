package org.madesimple.small.environment;

import org.madesimple.small.visualisation.Visualiser;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface EnvironmentRegister
{
    /**
     * @return The identifier of the environment
     */
    String getIdentifier();

    /**
     * @return The name of the environment
     */
    String getName();

    /**
     * @return An instance of the environment
     */
    Environment getEnvironment();

    /**
     * @return An instance of a visualiser of the environment
     */
    Visualiser getVisualiser();
}