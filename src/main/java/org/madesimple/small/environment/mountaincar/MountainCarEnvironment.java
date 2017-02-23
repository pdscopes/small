package org.madesimple.small.environment.mountaincar;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.environment.ContinuousEnvironment;
import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.State;
import org.madesimple.small.environment.TurnBasedEnvironment;
import org.madesimple.small.utility.Configuration;

import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * Mountain Car environment requires the following in the configuration file
 * for it to work:
 * </p>
 * <pre>
 * ## Mountain Car Environment settings
 * Environment.MountainCar.RewardPerStep = -1.0d
 * Environment.MountainCar.RewardAtGoal = 0.0d
 * Environment.MountainCar.RandomStarts = false
 * Environment.MountainCar.TransitionNoise = 0.0d
 * Environment.MountainCar.MaxTurns = 4000
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class MountainCarEnvironment extends Observable implements TurnBasedEnvironment, ContinuousEnvironment {

    public static class Tuple extends Environment.Tuple {
        MountainCarState state;
        MountainCarState next;

        public Tuple() {
            this.state = new MountainCarState();
            this.next = new MountainCarState();
        }
    }

    /**
     * The minimum position a car can have in the environment.
     */
    public static final double MIN_POSITION = -1.20d;
    /**
     * The maximum position a car can have in the environment.
     */
    public static final double MAX_POSITION = 0.60d;
    /**
     * The minimum velocity a car can have in the environment.
     */
    public static final double MIN_VELOCITY = -0.07d;
    /**
     * The maximum velocity a car can have in the environment.
     */
    public static final double MAX_VELOCITY = 0.07d;


    protected static final double GOAL_POSITION = 0.50d;

    /**
     * The factor to which the acceleration of the car should scaled.
     */
    protected static final double ACCELERATION_FACTOR = 0.0010d;
    /**
     * The factor to which gravity helps to acceleration the car toward the
     * valley trough.
     */
    protected static final double GRAVITY_FACTOR      = -0.0025d;
    /**
     * The frequency of this hill peaks in the environment.
     */
    protected static final double HILL_PEAK_FREQUENCY = 3.0000d;

    /**
     * The default starting state of the car in the environment.
     */
    protected static final MountainCarState DEFAULT = new MountainCarState(-0.5d, 0.0d);

    /**
     * The actions that are available in the Mountain Car Environment.
     */
    protected static final MountainCarAction[] envActions_ = MountainCarAction.values();


    //These are configurable
    /**
     * The reward an agent receives per step in the environment.
     */
    protected double  rewardPerStep;
    /**
     * The reward an agent receives upon reaching the goal state.
     */
    protected double  rewardAtGoal;
    /**
     * True if environment should have random starting positions,
     * false otherwise.
     */
    protected boolean randomStarts;
    /**
     * True if the environment should have transition noise, that is agents
     * might move slightly different to how they requested.
     */
    protected double  transitionNoise;
    protected int     maxTurns;


    /**
     * The Tuple to hold the single agent information.
     */
    protected Tuple         tuple;
    /**
     * The configuration of the Mountain Car environment.
     */
    protected Configuration cfg;
    protected int           time;
    protected int           turn;

    public MountainCarEnvironment() {
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        this.cfg = cfg;
    }

    @Override
    public void initialise() {
        // Initialise the rewards
        rewardPerStep = cfg.getDouble("Environment.MountainCar.RewardPerStep");
        rewardAtGoal = cfg.getDouble("Environment.MountainCar.RewardAtGoal");

        // Initialise random starts and noise
        randomStarts = cfg.getBoolean("Environment.MountainCar.RandomStarts");
        transitionNoise = cfg.getDouble("Environment.MountainCar.TransitionNoise");

        // Initialise max turns
        maxTurns = cfg.getInteger("Environment.MountainCar.MaxTurns");

        // Initialise the agent tuple
        tuple = new Tuple();

        // Initialise the clock
        time = 0;
        turn = 0;

        // Reset visualiser
        setChanged();
        notifyObservers(null);
    }

    @Override
    public void reseed() {
        tuple.state.set(
                ThreadLocalRandom.current().nextDouble(MIN_POSITION, MAX_POSITION),
                ThreadLocalRandom.current().nextDouble(MIN_VELOCITY, MAX_VELOCITY)
        );
    }

    @Override
    public void restart() {
        // Reset the environment
        turn = 0;
        tuple.state = new MountainCarState(DEFAULT);
        if (randomStarts) {
            tuple.state.set(
                    DEFAULT.getPosition() + .25d * (ThreadLocalRandom.current().nextDouble() - .5d),
                    DEFAULT.getVelocity() + .025d * (ThreadLocalRandom.current().nextDouble() - .5d)
            );
        }
        tuple.next = new MountainCarState();

        // reset the agent
        tuple.agent.reset(this);

        // Reset visualiser
        setChanged();
        notifyObservers(null);
    }

    @Override
    public int maxTurns() {
        return maxTurns;
    }

    @Override
    public boolean add(Agent agent) {
        if (tuple.agent == null) {
            tuple.agent = agent;
            tuple.agent.add(this, tuple.state);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Agent agent) {
        if (tuple.agent == agent) {
            tuple.agent = null;
            agent.remove(this);
        }
        return false;
    }

    @Override
    public int agentCount() {
        return tuple.agent == null ? 0 : 1;
    }

    @Override
    public int requiredAgentCount() {
        return 1;
    }

    @Override
    public void performTurn() {
        // Make the agent take its turn
        tuple.next.set(tuple.state);
        int action = tuple.agent.act(this, tuple.state);
        move(tuple.next, action);
        tuple.state.set(tuple.next);

        // Provide the agent with a reward
        double reward = rewardPerStep;
        if (isTerminal()) {
            reward = rewardAtGoal;
        }
        tuple.agent.receive(this, tuple.state, reward);

        // Inform visualiser
        setChanged();
        notifyObservers(tuple.state);

        // Increment the clock
        time++;
        turn++;
    }

    @Override
    public int countBounds() {
        return 2;
    }

    @Override
    public double[] lowerBounds() {
        return new double[]{MIN_POSITION, MIN_VELOCITY};
    }

    @Override
    public double[] upperBounds() {
        return new double[]{MAX_POSITION, MAX_VELOCITY};
    }

    @Override
    public boolean isTerminal(Agent agent, State state) {
        return tuple.agent == agent && isTerminal((MountainCarState) state);
    }

    @Override
    public boolean isTerminal() {
        return isTerminal(tuple.state);
    }

    private boolean isTerminal(MountainCarState state) {
        return state.getPosition() >= GOAL_POSITION;
    }

    /**
     * Get the height of the hill at this position.
     *
     * @param queryPosition
     * @return The height at the specified position
     */
    public double getHeightAtPosition(double queryPosition) {
        return -Math.sin(HILL_PEAK_FREQUENCY * (queryPosition));
    }

    /**
     * Get the slope of the hill at this position.
     *
     * @param queryPosition
     * @return The slope at the specified position
     */
    public double getSlope(double queryPosition) {
        /*The curve is generated by cos(hillPeakFrequency(x-pi/2.0)) so the
         * pseudo-derivative is cos(hillPeakFrequency* x)
         */
        return Math.cos(HILL_PEAK_FREQUENCY * queryPosition);
    }

    public void move(MountainCarState state, int action) {
        double acceleration = ACCELERATION_FACTOR;

        double position = state.getPosition();
        double velocity = state.getVelocity();

        //Noise should be at most
        double thisNoise = 2.0d * acceleration * transitionNoise * (ThreadLocalRandom.current().nextDouble() - .5d);

        velocity += (thisNoise + ((envActions_[action].value)) * (acceleration)) + getSlope(position) * (GRAVITY_FACTOR);
        if (velocity > MAX_VELOCITY) {
            velocity = MAX_VELOCITY;
        }
        if (velocity < MIN_VELOCITY) {
            velocity = MIN_VELOCITY;
        }
        position += velocity;
        if (position > MAX_POSITION) {
            position = MAX_POSITION;
        }
        if (position < MIN_POSITION) {
            position = MIN_POSITION;
        }
        if (position == MIN_POSITION && velocity < 0) {
            velocity = 0;
        }

        state.set(position, velocity);
    }
}
