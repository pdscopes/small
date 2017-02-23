package org.madesimple.small.environment.mountaincar;

import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.EnvironmentRegister;
import org.madesimple.small.visualisation.Visualiser;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class MountainCarRegister implements EnvironmentRegister {

    @Override
    public String getIdentifier() {
        return "mountain-car";
    }

    @Override
    public String getName() {
        return "Mountain Car";
    }

    @Override
    public Environment getEnvironment() {
        return new MountainCarEnvironment();
    }

    @Override
    public Visualiser getVisualiser() {
        return new MountainCarVisualiser();
    }
}
