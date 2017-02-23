package org.madesimple.small.environment.acrobot;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.environment.*;
import org.madesimple.small.utility.Configuration;

import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <pre>
 * Environment.Acrobot.RewardPerStep = -1.0d
 * Environment.Acrobot.RewardAtGoal = 0.0d
 * Environment.Acrobot.RandomStarts = true
 * Environment.Acrobot.TransitionNoise = 0.0d
 * Environment.Acrobot.MaxTurns = 4000
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class AcrobotEnvironment extends Observable implements TurnBasedEnvironment, ContinuousEnvironment {

    public static class Tuple extends Environment.Tuple {

        public Tuple() {
            this.state = new AcrobotState();
            this.next = new AcrobotState();
        }

        /**
         * Current state.
         */
        AcrobotState state;

        /**
         * Next state.
         */
        AcrobotState next;
    }

    protected static final double GOAL_POSITION = 1.0d;

    protected double  rewardPerStep;
    protected double  rewardAtGoal;
    protected boolean randomStarts;
    protected double  transitionNoise;
    protected int     maxTurns;

    protected Configuration cfg;
    protected int           time;
    protected int           turn;
    protected Tuple         tuple;
//    protected Visualiser visualiser;

    public AcrobotEnvironment() {
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        this.cfg = cfg;
    }

    @Override
    public void initialise() {
        // Initialise the reward
        rewardPerStep = cfg.getDouble("Environment.Acrobot.RewardPerStep");
        rewardAtGoal = cfg.getDouble("Environment.Acrobot.RewardAtGoal");

        // Initialise random starts and noise
        randomStarts = cfg.getBoolean("Environment.Acrobot.RandomStarts");
        transitionNoise = cfg.getDouble("Environment.Acrobot.TransitionNoise");

        // Initialise max turns
        maxTurns = cfg.getInteger("Environment.Acrobot.MaxTurns");

        // Initialise the agent tuple
        tuple = new Tuple();

        // Initialise the clock
        time = 0;
        turn = 0;

        // Initialise the visualiser
//        if (cfg.get('observer.visualise', false)) {
//            if (visualiser == null) {
//                visualiser = new AcrobotVisualiser();
//            }
//        }
    }

    @Override
    public void reseed() {
        tuple.state.set(
                ThreadLocalRandom.current().nextDouble(-AcrobotState.maxTheta1, AcrobotState.maxTheta1),
                ThreadLocalRandom.current().nextDouble(-AcrobotState.maxTheta2, AcrobotState.maxTheta2),
                ThreadLocalRandom.current().nextDouble(-AcrobotState.maxTheta1Dot, AcrobotState.maxTheta1Dot),
                ThreadLocalRandom.current().nextDouble(-AcrobotState.maxTheta2Dot, AcrobotState.maxTheta2Dot)
        );
    }

    @Override
    public void restart() {
        // Reset the environment
        turn = 0;
        tuple.state = new AcrobotState();
        if (randomStarts) {
            tuple.state.set(
                    ThreadLocalRandom.current().nextDouble() - 0.5d,
                    ThreadLocalRandom.current().nextDouble() - 0.5d,
                    ThreadLocalRandom.current().nextDouble() - 0.5d,
                    ThreadLocalRandom.current().nextDouble() - 0.5d
            );
        }
        tuple.next = new AcrobotState();

        // Reset the agent
        tuple.agent.reset(this);
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
        AcrobotState.perform(this, tuple.next, AcrobotAction.values()[action], transitionNoise);
        tuple.state.set(tuple.next);

        // Provide the agent with a reward
        tuple.agent.receive(this, tuple.state, rewardPerStep);
        if (isTerminal(tuple.state)) {
            tuple.agent.receive(this, tuple.state, rewardAtGoal);
        }

        // Increment the clock
        time++;
        turn++;
    }

    @Override
    public int countBounds() {
        return 4;
    }

    @Override
    public double[] lowerBounds() {
        return new double[]{-AcrobotState.maxTheta1, -AcrobotState.maxTheta2, -AcrobotState.maxTheta1Dot, -AcrobotState.maxTheta2Dot};
    }

    @Override
    public double[] upperBounds() {
        return new double[]{AcrobotState.maxTheta1, AcrobotState.maxTheta2, AcrobotState.maxTheta1Dot, AcrobotState.maxTheta2Dot};
    }

    @Override
    public boolean isTerminal(Agent agent, State state) {
        return tuple.agent == agent && isTerminal(tuple.state);
    }

    @Override
    public boolean isTerminal() {
        return isTerminal(tuple.state);
    }

    public boolean isTerminal(AcrobotState state) {
        double feet_height = -(AcrobotState.l1 * Math.cos(state.getTheta1()) + AcrobotState.l2 * Math.cos(state.getTheta2()));

        //New Code
        double firstJointEndHeight = AcrobotState.l1 * Math.cos(state.getTheta1());
        //Second Joint height (relative to first joint)
        double secondJointEndHeight = AcrobotState.l2 * Math.sin(Math.PI / 2 - state.getTheta1() - state.getTheta2());

        feet_height = -(firstJointEndHeight + secondJointEndHeight);
        return (feet_height > GOAL_POSITION);
    }
}
