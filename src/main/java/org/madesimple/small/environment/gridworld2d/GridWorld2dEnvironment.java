package org.madesimple.small.environment.gridworld2d;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.environment.DiscreteEnvironment;
import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.State;
import org.madesimple.small.environment.TurnBasedEnvironment;
import org.madesimple.small.utility.Configuration;

import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * A Grid World 2d Environment is an over-arching environment for any
 * and all 2d, compass move based environment.
 * </p>
 * <pre>
 * ## Settings for Grid World Environment
 * Environment.GridWorld2d.LayoutFilePath   = path/to/layout.txt
 * ; [cardinal|ordinal]
 * Environment.GridWorld2d.AvailableActions = cardinal
 * Environment.GridWorld2d.MaxTurns = 4000
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class GridWorld2dEnvironment extends Observable implements TurnBasedEnvironment, DiscreteEnvironment {

    public static class Tuple extends Environment.Tuple {
        /**
         * Current state.
         */
        GridWorld2dState state;

        /**
         * Next state.
         */
        GridWorld2dState next;

        /**
         * Starting state.
         */
        GridWorld2dState start;

        /**
         * Goal state.
         */
        GridWorld2dState goal;

        /**
         * Whether the agent has arrived at a goal.
         */
        boolean arrived;

        /**
         * The action performed by the agent.
         */
        Compass action;

        /**
         * Whether the agent failed to move.
         */
        boolean failed;

        /**
         * Whether the agent collided in the current step.
         */
        boolean collided;

        Tuple() {
            state = new GridWorld2dState(0, 0);
            next = new GridWorld2dState(0, 0);
            start = new GridWorld2dState(0, 0);
            goal = null;
            reset();
        }

        void reset() {
            state.set(start);
            next.set(start);
            arrived = false;
            action = null;
            failed = false;
            collided = false;
        }
    }

    protected double transitionNoise  = 0.0d;
    protected double rewardTransition = -1.0d;
    protected double rewardAtGoal     = 0.0d;
    protected int maxTurns;
    protected int requiredAgents;

    protected GridWorld2dLayout layout;
    protected Tuple[]           tuples;
    protected Map<Agent, Tuple> mappedTuples;
    protected Compass[]         actions;
    protected int               time;
    protected int               turn;

    public GridWorld2dEnvironment() {
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        // Set the actions
        actions = "cardinal".equals(cfg.getString("Environment.GridWorld2d.AvailableActions").toLowerCase()) ?
                  Compass.Cardinal.values() :
                  Compass.Ordinal.values();

        // Get the layout
        layout = fetchLayout(cfg);
        mappedTuples = new HashMap<>();
        updateTuples();

        // Initialise max turns
        maxTurns = cfg.getInteger("Environment.GridWorld2d.MaxTurns");
        requiredAgents = cfg.getInteger("Environment.GridWorld2d.NumAgents");
    }

    protected GridWorld2dLayout fetchLayout(Configuration cfg) {
        try {
            GridWorld2dFileReader fr = new GridWorld2dFileReader();
            fr.parse(Paths.get(cfg.getString("Environment.GridWorld2d.LayoutFilePath")));

            GridWorld2dLayout layout = new GridWorld2dLayout();
            layout.stateWidth = fr.getStateWidth();
            layout.stateHeight = fr.getStateHeight();
            layout.width = fr.getLayoutWidth();
            layout.height = fr.getLayoutHeight();
            layout.map = fr.getLayout();
            layout.parse();


            return layout;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void initialise() {
        // Initialise the clock
        time = 0;
        turn = 0;

        // Initialise the visualiser
        setChanged();
        notifyObservers(layout);
    }

    @Override
    public void reseed() {
        for (Tuple tuple : tuples) {
            tuple.state.set(
                    ThreadLocalRandom.current().nextInt(0, layout.stateWidth),
                    ThreadLocalRandom.current().nextInt(0, layout.stateHeight)
            );
        }
    }

    @Override
    public void restart() {
        // Reset the environment
        turn = 0;
        for (Tuple tuple : tuples) {
            tuple.reset();
        }

        // Reset the agents
        for (Tuple tuple : tuples) {
            tuple.agent.reset(this);
        }

        // Initialise the visualiser
        setChanged();
        notifyObservers(null);
    }

    @Override
    public int maxTurns() {
        return maxTurns;
    }


    @Override
    public boolean add(Agent agent) {
        if (layout.availableTuples.size() > 0 && !mappedTuples.containsKey(agent)) {
            Tuple tuple = layout.availableTuples.remove(0);
            tuple.agent = agent;
            mappedTuples.put(agent, tuple);

            updateTuples();
            tuple.agent.add(this, tuple.state);
            return true;
        }

        return false;
    }

    @Override
    public boolean remove(Agent agent) {
        Tuple tuple = mappedTuples.get(agent);
        if (tuple != null) {
            tuple.agent = null;
            layout.availableTuples.add(tuple);
            mappedTuples.remove(agent);

            updateTuples();
            agent.remove(this);
            return true;
        }
        return false;
    }

    private void updateTuples() {
        tuples = new Tuple[mappedTuples.size()];
        int i = 0;
        for (Tuple tuple : mappedTuples.values()) {
            tuples[i++] = tuple;
        }
    }

    @Override
    public int agentCount() {
        return mappedTuples.size();
    }

    @Override
    public int requiredAgentCount() {
        return requiredAgents;
    }

    @Override
    public void performTurn() {
        // Let each agent choose their action
        for (Tuple tuple : tuples) {
            if (!tuple.arrived) {
                tuple.next.set(tuple.state);
                int action = tuple.agent.act(this, tuple.state);
                attemptAction(tuple, action);
            }
        }

        // Handle conflicts
        conflictResolution();

        // Now move all agents and update whether they have arrived
        for (Tuple tuple : tuples) {
            tuple.state.set(tuple.next);
            tuple.arrived = tuple.goal != null ? tuple.state.equals(tuple.goal) : layout.goals.contains(tuple.state);
        }

        // Provide the agents with their rewards
        for (Tuple tuple : tuples) {
            tuple.agent.receive(this, tuple.state, tuple.arrived ? rewardAtGoal : rewardTransition);
        }

        // Update observers
        setChanged();
        notifyObservers(tuples[0].state);

        // Increment the clock
        time++;
        turn++;
    }

    protected Tuple fetch(Agent agent) {
        if (tuples.length == 1 && agent == tuples[0].agent) {
            return tuples[0];
        }

        Tuple tuple = mappedTuples.get(agent);
        if (tuple != null) {
            return tuple;
        }

        return null;
    }

    @Override
    public int countBounds() {
        return 2;
    }

    @Override
    public int[] lowerBounds() {
        return new int[] {0, 0};
    }

    @Override
    public int[] upperBounds() {
        return new int[] {layout.stateWidth, layout.stateHeight};
    }

    @Override
    public boolean isTerminal(Agent agent, State state) {
        return isTerminal(fetch(agent), (GridWorld2dState) state);
    }
    protected boolean isTerminal(Tuple tuple, GridWorld2dState state) {
        return tuple.goal != null ? state.equals(tuple.goal) : layout.goals.contains(state);
    }

    @Override
    public boolean isTerminal() {
        for (Tuple tuple : tuples) {
            if (!tuple.arrived) {
                return false;
            }
        }

        return true;
    }

    protected void attemptAction(Tuple tuple, int action) {
        if (!tuple.arrived) {
            tuple.action = actions[action];
            tuple.collided = false;
            GridWorld2dState next = tuple.next;
            // If the agents action didn't fail
            if (!(tuple.failed = transitionNoise > ThreadLocalRandom.current().nextDouble())) {
                // Attempt the move with the next state
                GridWorld2dState.perform(next, actions[action], 1);

                // Wrap movement
                next.x = wrapX(next.x);
                next.y = wrapY(next.y);

                // Check the new position and the move are valid
                if (!isValidMove(tuple.state, actions[action])) {
                    next.set(tuple.state);
                    tuple.action = null;
                    tuple.collided = true;
                }
            }
        }
    }

    protected int wrapX(int x) {
        return (x + layout.stateWidth) % layout.stateWidth;
    }

    protected int wrapY(int y) {
        return (y + layout.stateHeight) % layout.stateHeight;
    }

    protected boolean isValidMove(GridWorld2dState s, Compass a) {
        double probability = getProbability(s, a);
        return probability >= ThreadLocalRandom.current().nextDouble();
    }

    protected double getProbability(GridWorld2dState s, Compass action) {
        if (action instanceof Compass.Cardinal) {
            return layout.probabilities[s.y][s.x][((Compass.Cardinal) action).ordinal()];
        }
        if (action instanceof Compass.Ordinal) {
            return layout.probabilities[s.y][s.x][((Compass.Ordinal) action).ordinal()];
//            switch ((Compass.Ordinal) action) {
//                case NORTH:
//                case SOUTH:
//                case EAST:
//                case WEST:
//                    return layout.probabilities[s.y][s.x][((Compass.Ordinal) action).ordinal()];
//
//                case NORTHEAST:
//                    return layout.probabilities[s.y][s.x][Compass.Ordinal.NORTH.ordinal()] *
//                           layout.probabilities[s.y][s.x][Compass.Ordinal.EAST.ordinal()] *
//                           layout.probabilities[s.y][wrapX(s.x + 1)][Compass.Ordinal.NORTH.ordinal()] *
//                           layout.probabilities[wrapY(s.y + 1)][s.x][Compass.Ordinal.EAST.ordinal()];
//                case NORTHWEST:
//                    return layout.probabilities[s.y][s.x][Compass.Ordinal.NORTH.ordinal()] *
//                           layout.probabilities[s.y][s.x][Compass.Ordinal.WEST.ordinal()] *
//                           layout.probabilities[s.y][wrapX(s.x - 1)][Compass.Ordinal.NORTH.ordinal()] *
//                           layout.probabilities[wrapY(s.y + 1)][s.x][Compass.Ordinal.WEST.ordinal()];
//                case SOUTHEAST:
//                    return layout.probabilities[s.y][s.x][Compass.Ordinal.SOUTH.ordinal()] *
//                           layout.probabilities[s.y][s.x][Compass.Ordinal.EAST.ordinal()] *
//                           layout.probabilities[s.y][wrapX(s.x + 1)][Compass.Ordinal.SOUTH.ordinal()] *
//                           layout.probabilities[wrapY(s.y - 1)][s.x][Compass.Ordinal.EAST.ordinal()];
//                case SOUTHWEST:
//                    return layout.probabilities[s.y][s.x][Compass.Ordinal.SOUTH.ordinal()] *
//                           layout.probabilities[s.y][s.x][Compass.Ordinal.WEST.ordinal()] *
//                           layout.probabilities[s.y][wrapX(s.x - 1)][Compass.Ordinal.SOUTH.ordinal()] *
//                           layout.probabilities[wrapY(s.y - 1)][s.x][Compass.Ordinal.WEST.ordinal()];
//            }
        }
        return 0.0d;
    }

    /**
     * <p>
     * Determine if there are any conflicts to resolve and if so resolve them. There are 3 ways to resolve conflict:
     * </p>
     * <ol>
     * <li>Negate actions of all involved</li>
     * <li>Randomly choose who has precedence of action</li>
     * <li>Choose a set precedence of actions</li>
     * </ol>
     * <p>
     * <p>
     * This conflict resolution blocks the action from taking place. It assumes that the current state, hence the
     * starting state, must be valid and then blocks any movements that would leave two agents in the same location
     * or would cause them to cross paths.
     * </p>
     */
    protected void conflictResolution() {
        for (int i = 0; i < tuples.length; i++) {
            Tuple a = tuples[i];
            // @TODO If arrived at goal
            if (a.arrived) {
                continue;
            }

            boolean hasConflict = false;

            // Check for other agents moving into the same state
            for (int j = i + 1; j < tuples.length; j++) {
                Tuple b = tuples[j];
                // @TODO If in terminal state ignore
                if (b.arrived) {
                    continue;
                }

                // If moving into the same square
                // or if one agent isn't moving and the other tries to join it
                if (a.next.equals(b.next)) {
                    b.next.set(b.state);
                    b.collided = true;
                    hasConflict = true;
                }
                // or if they swap states
                else if (a.next.equals(b.state) && b.next.equals(a.state)) {
                    b.next.set(b.state);
                    b.collided = true;
                    hasConflict = true;
                }
                // or if they cross paths
                else if (
                        a.action != null &&
                        b.action != null &&
                        a.state.near(b.state, 1) &&
                        a.next.near(b.next, 1) &&
                        a.action.opposite() == b.action
                        ) {
                    b.next.set(b.state);
                    b.collided = true;
                    hasConflict = true;
                }
            }

            // If there was a conflict
            if (hasConflict) {
                a.next.set(a.state);
                a.collided = true;
                i = 0;
            }
        }
    }
}
