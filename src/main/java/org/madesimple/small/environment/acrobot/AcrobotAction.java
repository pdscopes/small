package org.madesimple.small.environment.acrobot;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public enum AcrobotAction {
    REVERSE(-1.0d), NEUTRAL(0.0d), FORWARD(1.0d);

    public final double value;

    AcrobotAction(double value) {
        this.value = value;
    }
}
