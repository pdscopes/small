package org.madesimple.small.environment.mountaincar;

import org.madesimple.small.environment.ContinuousState;
import org.madesimple.small.environment.State;
import org.madesimple.small.utility.Hash;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class MountainCarState implements ContinuousState {

    /**
     * The position of the car.
     */
    private double position;

    /**
     * The velocity of the car.
     */
    private double velocity;

    /**
     *
     */
    public MountainCarState() {
        this(0.0d, 0.0d);
    }

    public MountainCarState(double position, double velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public MountainCarState(MountainCarState that) {
        this(that.position, that.velocity);
    }

    @Override
    public State copy() {
        return new MountainCarState(this);
    }

    /**
     * @param position x The new position of this state
     * @param velocity y The new velocity of this state
     */
    public void set(double position, double velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public void set(MountainCarState that) {
        set(that.position, that.velocity);
    }

    @Override
    public void set(State that) {
        if (that instanceof MountainCarState) {
            set((MountainCarState) that);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MountainCarState && cmp(this, (MountainCarState) obj);
    }

    @Override
    public double tuple(int feature) {
        switch (feature) {
            case 0:
                return position;
            case 1:
                return velocity;
        }
        throw new RuntimeException("Unknown feature");
    }

    @Override
    public double[] tuple() {
        return new double[]{position, velocity};
    }

    @Override
    public int availableActions() {
        return MountainCarAction.values().length;
    }

    @Override
    public int countFeatures() {
        return 2;
    }

    @Override
    public int hashCode() {
        return Hash.pair((int)(position*1000.0d), (int)(velocity*1000.0d));
    }

    @Override
    public String toString() {
        return "State[position=" + position + ", velocity=" + velocity + "]";
    }

    /**
     * @return position The position of this state
     */
    public double getPosition() {
        return position;
    }

    /**
     * @return velocity The velocity of this state
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * @param position The new position of this state
     */
    public void setPosition(double position) {
        this.position = position;
    }

    /**
     * @param velocity The new velocity of this state
     */
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }


    private static boolean cmp(MountainCarState a, MountainCarState b) {
        return (a.position == b.position) && (a.velocity == b.velocity);
    }
}
