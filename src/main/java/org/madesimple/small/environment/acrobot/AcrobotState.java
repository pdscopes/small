package org.madesimple.small.environment.acrobot;

import org.madesimple.small.environment.ContinuousState;
import org.madesimple.small.environment.State;
import org.madesimple.small.utility.Hash;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class AcrobotState implements ContinuousState {
    /*STATIC CONSTANTS*/
    public final static double maxTheta1    = Math.PI;
    public final static double maxTheta2    = Math.PI;
    public final static double maxTheta1Dot = 4.0d * Math.PI;
    public final static double maxTheta2Dot = 9.0d * Math.PI;
    public final static double m1           = 1.0d;
    public final static double m2           = 1.0d;
    public final static double l1           = 1.0d;
    public final static double l2           = 1.0d;
    public final static double lc1          = 0.5d;
    public final static double lc2          = 0.5d;
    public final static double I1           = 1.0d;
    public final static double I2           = 1.0d;
    public final static double g            = 9.8d;
    public final static double dt           = 0.05d;

    private double theta1;
    private double theta2;
    private double theta1Dot;
    private double theta2Dot;

    public AcrobotState() {
        theta1 = 0.0d;
        theta2 = 0.0d;
        theta1Dot = 0.0d;
        theta2Dot = 0.0d;
    }

    public AcrobotState(double theta1, double theta2, double theta1Dot, double theta2Dot) {
        set(theta1, theta2, theta1Dot, theta2Dot);
    }

    public AcrobotState(AcrobotState that) {
        set(that);
    }

    @Override
    public AcrobotState copy() {
        return new AcrobotState(this);
    }

    public void set(double theta1, double theta2, double theta1Dot, double theta2Dot) {
        this.theta1 = theta1;
        this.theta2 = theta2;
        this.theta1Dot = theta1Dot;
        this.theta2Dot = theta2Dot;
    }

    public void set(AcrobotState that) {
        set(that.theta1, that.theta2, that.theta1Dot, that.theta2Dot);
    }

    @Override
    public void set(State that) {
        if (that instanceof AcrobotState) {
            set((AcrobotState) that);
        }
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof AcrobotState && cmp(this, (AcrobotState) that);
    }

    @Override
    public double tuple(int feature) {
        switch (feature) {
            case 0:
                return theta1;
            case 1:
                return theta2;
            case 2:
                return theta1Dot;
            case 3:
                return theta2Dot;
        }
        throw new RuntimeException("Unknown feature");
    }

    @Override
    public double[] tuple() {
        return new double[] {theta1, theta2, theta1Dot, theta2Dot};
    }

    @Override
    public int availableActions() {
        return AcrobotAction.values().length;
    }

    @Override
    public int countFeatures() {
        return 4;
    }

    @Override
    public int hashCode() {
        return Hash.pair(
                (int)(theta1*10000.0d),
                (int)(theta2*10000.0d),
                (int)(theta1Dot*10000.0d),
                (int)(theta2Dot*10000.0d)
        );
    }

    @Override
    public String toString()
    {
        return "AcrobotState[theta1="+theta1+", theta2="+theta2+
               ", theta1Dot="+theta1Dot+", theta2Dot="+theta2Dot+"]";
    }

    private static boolean cmp(AcrobotState a, AcrobotState b) {
        return (a.theta1 == b.theta1) && (a.theta2 == b.theta2) &&
               (a.theta1Dot == b.theta1Dot) && (a.theta2Dot == b.theta2Dot);
    }

    public double getTheta1() {
        return theta1;
    }

    public double getTheta2() {
        return theta2;
    }

    public double getTheta1Dot() {
        return theta1Dot;
    }

    public double getTheta2Dot() {
        return theta2Dot;
    }

    public void setTheta1(double theta1) {
        this.theta1 = theta1;
    }

    public void setTheta2(double theta2) {
        this.theta2 = theta2;
    }

    public void setTheta1Dot(double theta1Dot) {
        this.theta1Dot = theta1Dot;
    }

    public void setTheta2Dot(double theta2Dot) {
        this.theta2Dot = theta2Dot;
    }

    static void perform(AcrobotEnvironment env, AcrobotState state, AcrobotAction action, double transitionNoise) {
        double torque = action.value;
        double d1;
        double d2;
        double phi_2;
        double phi_1;

        double theta2_ddot;
        double theta1_ddot;

        //torque is in [-1,1]
        //We'll make noise equal to at most +/- 1
        double theNoise = transitionNoise * 2.0d * (ThreadLocalRandom.current().nextDouble() - .5d);

        torque += theNoise;

        int count = 0;
        while (!env.isTerminal(state) && count < 4) {
            count++;

            d1 = m1 * Math.pow(lc1, 2) + m2 * (Math.pow(l1, 2) + Math.pow(lc2, 2) + 2 * l1 * lc2 * Math.cos(state.theta2)) + I1 + I2;
            d2 = m2 * (Math.pow(lc2, 2) + l1 * lc2 * Math.cos(state.theta2)) + I2;

            phi_2 = m2 * lc2 * g * Math.cos(state.theta1 + state.theta2 - Math.PI / 2.0);
            phi_1 = -(m2 * l1 * lc2 * Math.pow(state.theta2Dot, 2) * Math.sin(state.theta2) - 2 * m2 * l1 * lc2 * state.theta1Dot * state.theta2Dot * Math.sin(state.theta2)) + (m1 * lc1 + m2 * l1) * g * Math.cos(state.theta1 - Math.PI / 2.0) + phi_2;

            theta2_ddot = (torque + (d2 / d1) * phi_1 - m2 * l1 * lc2 * Math.pow(state.theta1Dot, 2) * Math.sin(state.theta2) - phi_2) / (m2 * Math.pow(lc2, 2) + I2 - Math.pow(d2, 2) / d1);
            theta1_ddot = -(d2 * theta2_ddot + phi_1) / d1;

            state.theta1Dot += theta1_ddot * dt;
            state.theta2Dot += theta2_ddot * dt;

            state.theta1 += state.theta1Dot * dt;
            state.theta2 += state.theta2Dot * dt;
        }
        if (Math.abs(state.theta1Dot) > maxTheta1Dot) {
            state.theta1Dot = Math.signum(state.theta1Dot) * maxTheta1Dot;
        }

        if (Math.abs(state.theta2Dot) > maxTheta2Dot) {
            state.theta2Dot = Math.signum(state.theta2Dot) * maxTheta2Dot;
        }
        /* Put a hard constraint on the acrobot physics, thetas MUST be in [-PI,+PI]
         * if they reach a top then angular velocity becomes zero
         */
        if (Math.abs(state.theta2) > Math.PI) {
            state.theta2 = Math.signum(state.theta2) * Math.PI;
            state.theta2Dot = 0;
        }
        if (Math.abs(state.theta1) > Math.PI) {
            state.theta1 = Math.signum(state.theta1) * Math.PI;
            state.theta1Dot = 0;
        }
    }
}
