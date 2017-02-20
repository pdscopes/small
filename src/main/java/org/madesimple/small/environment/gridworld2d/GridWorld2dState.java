package org.madesimple.small.environment.gridworld2d;

import org.madesimple.small.environment.DiscreteState;
import org.madesimple.small.environment.State;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class GridWorld2dState extends DiscreteState {
    /**
     * x-axis coordinate.
     */
    protected int x;

    /**
     * y-axis coordinate.
     */
    protected int y;

    public GridWorld2dState(int x, int y) {
        set(x, y);
    }

    public GridWorld2dState(State that) {
        set(that);
    }

    @Override
    public GridWorld2dState copy() {
        return new GridWorld2dState(this);
    }

    public void set(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void set(GridWorld2dState that) {
        set(that.x, that.y);
    }

    @Override
    public void set(State that) {
        if (that instanceof GridWorld2dState) {
            set((GridWorld2dState) that);
        }
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof GridWorld2dState && cmp(this, (GridWorld2dState) that);
    }

    @Override
    public int[] tuple() {
        return new int[] {x, y};
    }

    @Override
    public int availableActions() {
        return 4;
    }

    @Override
    public int countFeatures() {
        return 2;
    }

    @Override
    public String toString() {
        return "GridWorld2dState[x=" + x + ", y=" + y + "]";
    }

    public void move(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public boolean near(GridWorld2dState that, int distance) {
        return Math.abs(this.x-that.x) <= distance && Math.abs(this.y-that.y) <= distance;
    }

    public double distance(int x, int y) {
        return Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
    }

    public double distance(GridWorld2dState that) {
        return distance(that.x, that.y);
    }

    private static boolean cmp(GridWorld2dState a, GridWorld2dState b) {
        return a.x == b.x && a.y == b.y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    static void perform(GridWorld2dState state, Compass action, int dis) {
        if (action instanceof Compass.Cardinal) {
            switch ((Compass.Cardinal) action) {
                case NORTH:
                    state.move(0, dis);
                    break;
                case EAST:
                    state.move(dis, 0);
                    break;
                case SOUTH:
                    state.move(0, -dis);
                    break;
                case WEST:
                    state.move(-dis, 0);
                    break;
            }
            return;
        }
        if (action instanceof Compass.Ordinal) {
            switch ((Compass.Ordinal) action) {
                case NORTH:
                    state.move(0, dis);
                    break;
                case EAST:
                    state.move(dis, 0);
                    break;
                case SOUTH:
                    state.move(0, -dis);
                    break;
                case WEST:
                    state.move(-dis, 0);
                    break;

                case NORTHEAST:
                    state.move(dis, dis);
                    break;
                case NORTHWEST:
                    state.move(-dis, dis);
                    break;
                case SOUTHEAST:
                    state.move(dis, -dis);
                    break;
                case SOUTHWEST:
                    state.move(-dis, -dis);
                    break;
            }
            return;
        }

        throw new RuntimeException("Unknown action instance");
    }
}
