package org.madesimple.small.agent.learning.algorithm;

import org.madesimple.small.agent.Strategy;
import org.madesimple.small.agent.learning.LearningAlgorithm;
import org.madesimple.small.agent.learning.storage.TileCoding;
import org.madesimple.small.agent.learning.storage.qtable.ActionValueTable;
import org.madesimple.small.agent.strategy.Argmax;
import org.madesimple.small.environment.ContinuousEnvironment;
import org.madesimple.small.environment.ContinuousState;
import org.madesimple.small.environment.Environment;
import org.madesimple.small.environment.State;
import org.madesimple.small.utility.Configuration;

import java.io.File;

/**
 * <pre>
 * LearningAlgorithm.TileCode.Alpha = 0.4
 * LearningAlgorithm.TileCode.Gamma = 0.999
 * LearningAlgorithm.TileCode.Storage = org.madesimple.small.agent.learning.storage.tilecoding.Whiteson
 * LearningAlgorithm.TileCode.Strategy = org.madesimple.small.agent.strategy.EpsilonGreedy
 * TileCoding.NumTilings = 4
 * TileCoding.Sutton.NumTiles = 50
 * TileCoding.Whiteson.TilesPerFeature = 50,50
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class TileCode implements LearningAlgorithm {
    protected ActionValueTable qTable;
    protected double           alpha;
    protected double           gamma;
    protected int              nTilings;
    protected Strategy         strategy;
    protected TileCoding       tc;

    public TileCode() {
        qTable = new ActionValueTable();
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        try {
            this.alpha = cfg.getDouble("LearningAlgorithm.TileCode.Alpha");
            this.gamma = cfg.getDouble("LearningAlgorithm.TileCode.Gamma");
            this.nTilings = cfg.getInteger("TileCoding.NumTilings");

            // Instantiate and configure tile coding
            tc = (TileCoding) cfg.getInstance("LearningAlgorithm.TileCode.Storage");
            tc.setConfiguration(cfg);

            // Instantiate and configure strategy
            strategy = (Strategy) cfg.getInstance("LearningAlgorithm.TileCode.Strategy");
            strategy.setConfiguration(cfg);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected double[] qValues(int[] ts, int m, int nActions) {
        double[] Qs_ = new double[nActions];
        for (int a = 0; a < nActions; a++) {
            for (int i = 0; i < m; i++) {
                Qs_[a] += qTable.get(ts[i])[a];
            }
        }
        return Qs_;
    }

    @Override
    public void initialise() {

    }

    @Override
    public void commence(Environment environment) {
        tc.initialise((ContinuousEnvironment) environment);
    }

    @Override
    public int select(State state, int time) {
        return select(state, time, false);
    }

    @Override
    public void conclude() {

    }

    @Override
    public boolean loadPolicy(File file) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean savePolicy(File file) {
        throw new RuntimeException("Not yet implemented");
    }

    public static class Q extends TileCode {
        @Override
        public int select(State state, int time, boolean greedy) {
            // Get all the tiles of this state representation
            int[] tiles = tc.tiles((ContinuousState) state);

            // Initialise a container for the sum of qValues and sum them
            double[] qValues = qValues(tiles, nTilings, state.availableActions());

            // Select the best action
            if (greedy) {
                return Argmax.select(qValues);
            } else {
                return strategy.select(qValues, time);
            }
        }

        @Override
        public void clearTransitions() {

        }

        @Override
        public void update(State s, int a, State s_, double r) {
            // Find tiles
            int[] prevTiles = tc.tiles((ContinuousState) s);
            int[] nextTiles = tc.tiles((ContinuousState) s_);


            // Perform calculations
            double m     = nTilings;
            double Qsa   = qValues(prevTiles, nTilings, s.availableActions())[a];
            double Qs_a_ = Argmax.max(qValues(nextTiles, nTilings, s_.availableActions()));
            double error = r + (gamma * Qs_a_) - Qsa;

            // Update the qValues
            for (int i = 0; i < nTilings; i++) {
                double val = qTable.get(prevTiles[i])[a] + ((alpha / m) * error);
                qTable.put(prevTiles[i], a, val);
            }
        }
    }

    public static class Sarsa extends TileCode {
        private boolean hasPotentialState;
        private State   potentialState;
        private int     potentialAction;

        @Override
        public int select(State state, int time, boolean greedy) {
            if (greedy || !hasPotentialState || state.hashCode() != potentialState.hashCode()) {
                return Argmax.select(qTable.get(state.hashCode(), state.availableActions()));
            } else {
                return potentialAction;
            }
        }

        private double selectPotential(State state, int time) {
            // Get all the tiles of this state representation
            int[] tiles = tc.tiles((ContinuousState) state);

            double[] qValues = qValues(tiles, nTilings, state.availableActions());

            hasPotentialState = true;
            potentialState = state;
            potentialAction = strategy.select(qValues, time);

            return qValues[potentialAction];
        }

        @Override
        public void clearTransitions() {
            hasPotentialState = false;
            potentialState = null;
            potentialAction = -1;
        }

        @Override
        public void update(State s, int a, State s_, double r) {
            // Find tiles
            int[] prevTiles = tc.tiles((ContinuousState) s);
            int[] nextTiles = tc.tiles((ContinuousState) s_);

            // Perform calculations
            double m     = nTilings;
            double Qsa   = qValues(prevTiles, nTilings, s.availableActions())[a];
            double Qs_a_ = s_ != null ? selectPotential(s_, 0) : 0.0d;
            double error = r + (gamma * Qs_a_) - Qsa;

            // Update the qValues
            for (int i = 0; i < nTilings; i++) {
                double val = qTable.get(prevTiles[i])[a] + ((alpha / m) * error);
                qTable.put(prevTiles[i], a, val);
            }
        }
    }
}
