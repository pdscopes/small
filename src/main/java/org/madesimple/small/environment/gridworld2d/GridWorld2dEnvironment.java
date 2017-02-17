package org.madesimple.small.environment.gridworld2d;

import org.madesimple.small.agent.Agent;
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
public class GridWorld2dEnvironment implements TurnBasedEnvironment {

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

    protected Configuration     cfg;
    protected GridWorld2dLayout layout;
    protected Tuple[]           tuples;
    protected Map<Agent, Tuple> mappedTuples;
    protected Compass[]         actions;
    protected int               time;
    protected int               turn;
//    protected Visualiser visualiser;

    public GridWorld2dEnvironment() {
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        this.cfg = cfg;
    }

    @Override
    public void initialise() {
        // Set the actions
        actions = "cardinal".equals(cfg.getString("Environment.GridWorld2d.AvailableActions").toLowerCase()) ?
                Compass.Cardinal.values() :
                Compass.Ordinal.values();

        // Get the layout
        layout = fetchLayout();
        mappedTuples = new HashMap<>();

        // Initialise max turns
        maxTurns = cfg.getInteger("Environment.GridWorld2d.MaxTurns");

        // Initialise the clock
        time = 0;
        turn = 0;

        // Initialise the visualiser
//        if (cfg.get('observer.visualise', false)) {
//            if (visualiser == null) {
//                visualiser = new GridWorld2dVisualiser();
//            }
//        }
    }

    protected GridWorld2dLayout fetchLayout() {
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
    public int countAgents() {
        return mappedTuples.size();
    }

    @Override
    public void performTurn() {
        // Let each agent choose their action
        for (int i = 0; i < tuples.length; i++) {
            if (!tuples[i].arrived) {
                tuples[i].next.set(tuples[i].state);
                int action = tuples[i].agent.act(this, tuples[i].state);
                attemptAction(tuples[i], action);
            }
        }

        // Handle conflicts
        conflictResolution();

        // Now move all agents and update whether they have arrived
        for (int i = 0; i < tuples.length; i++) {
            tuples[i].state.set(tuples[i].next);
            tuples[i].arrived = tuples[i].goal != null ?
                    tuples[i].state.equals(tuples[i].goal) :
                    layout.goals.contains(tuples[i].state);
        }

        // Provide the agents with their rewards
        for (int i = 0; i < tuples.length; i++) {
            tuples[i].agent.receive(this, tuples[i].state, tuples[i].arrived ? rewardAtGoal : rewardTransition);
        }

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
    public boolean isTerminal(Agent agent) {
        Tuple tuple = fetch(agent);
        if (tuple != null) {
            return tuple.arrived;
        }

        return false;
    }

    @Override
    public boolean isTerminal(State state) {
        return false;
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
//                a.next.set(a.state);
//                a.collided = true;
//                i = 0;
            }
        }
    }
}
