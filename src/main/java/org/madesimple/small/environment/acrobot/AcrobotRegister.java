package org.madesimple.small.environment.acrobot;

import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.EnvironmentRegister;
import org.madesimple.small.visualisation.Visualiser;

/**
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class AcrobotRegister implements EnvironmentRegister
{
    @Override
    public String getIdentifier() {
        return "acrobot";
    }

    @Override
    public String getName()
    {
        return "Acrobot";
    }

    @Override
    public Environment getEnvironment() {
        return new AcrobotEnvironment();
    }

    @Override
    public Visualiser getVisualiser() {
        return null;
    }
}