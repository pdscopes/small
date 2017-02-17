package org.madesimple.small.environment.mountaincar;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public enum MountainCarAction {
    REVERSE(-1.0d), NEUTRAL(0.0d), FORWARD(1.0d);

    public final double value;

    MountainCarAction(double value) {
        this.value = value;
    }
}
