package org.madesimple.small.environment.gridworld2d;

import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.EnvironmentRegister;
import org.madesimple.small.visualisation.Visualiser;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class GridWorld2dRegister implements EnvironmentRegister
{
    @Override
    public String getIdentifier() {
        return "grid-world";
    }

    @Override
    public String getName()
    {
        return "Grid World 2D";
    }

    @Override
    public Environment getEnvironment() {
        return new GridWorld2dEnvironment();
    }

    @Override
    public Visualiser getVisualiser() {
        return new GridWorld2dVisualiser();
    }
}