package org.madesimple.small.agent.learning.algorithm;

import org.madesimple.small.agent.learning.LearningAlgorithm;
import org.madesimple.small.agent.strategy.Argmax;
import org.madesimple.small.environment.State;
import org.madesimple.small.agent.learning.storage.ActionValueTable;
import org.madesimple.small.utility.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <span style="font-variant: small-caps;">R-max</span> is a very simple model-based reinforcement
 * learning algorithm which can attain near-optimal average reward in polynomial time. In
 * <span style="font-variant: small-caps;">R-max</span>, the agent always maintains a complete,
 * but possibly inaccurate model of its environment and acts based on the optimal strategy derived
 * from this model. The model is initialised in an optimistic fashion: all actions in all states
 * return the maximal possible reward (hence the name). During execution, it is updated based on
 * the agent's observations.
 * </p>
 * <p>
 * <span style="font-variant: small-caps;">R-max</span> comes from 2002 paper in the Journal of
 * Machine Learning Research 3, &quot;<span style="font-variant: small-caps;">R-max</span>
 * - A General Polynomial Time Algorithm for Near-Optimal Reinforcement Learning&quot; by Brafman
 * &amp; Tennenholtz.
 * </p>
 * <p>
 * The actual implementation comes from the 2009 paper in the Journal of Machine Learning Research
 * 10, &quot;Reinforcement Learning in Finite MDPs: PAC Analysis&quot; by Strehl, Li, &amp; Littman.
 * </p>
 * <p>
 * To use this or any of its derivatives the following is needed in the
 * configuration file:
 * </p>
 * <pre>
 * ## R-max Learning Settings
 * ; The future discount factor
 * LearningAlgorithm.RMax.Gamma      = 0.99
 * ; The number of updates needed for a state to be known
 * LearningAlgorithm.RMax.M          = 5
 * ; The desired closeness to optimality
 * LearningAlgorithm.RMax.Epsilon    = 1
 * ; An upper bound on the maximum reward
 * LearningAlgorithm.RMax.Upperbound = 10
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class RMax implements LearningAlgorithm {
    protected Configuration    cfg;
    protected ActionValueTable qTable;
    protected double           gamma;
    protected int              m;
    protected double           epsilon;
    protected double           upperBound;

    protected Map<Integer, Map<Integer, Double>>                r_func;
    protected Map<Integer, Map<Integer, Integer>>               n1_func;
    protected Map<Integer, Map<Integer, Map<Integer, Integer>>> n2_func;

    public RMax() {
        qTable = new ActionValueTable();
        r_func = new HashMap<>();
        n1_func = new HashMap<>();
        n2_func = new HashMap<>();
    }

    public RMax(double gamma, int m, double epsilon, double upperBound) {
        this();
        this.gamma = gamma;
        this.m = m;
        this.epsilon = epsilon;
        this.upperBound = upperBound;
    }

    @Override
    public void setConfiguration(Configuration cfg) {
        this.cfg = cfg;
    }

    @Override
    public void initialise() {
        // Initialise alpha/gamma
        gamma = cfg.getDouble("LearningAlgorithm.RMax.Gamma");
        m = cfg.getInteger("LearningAlgorithm.RMax.M");
        epsilon = cfg.getDouble("LearningAlgorithm.RMax.Epsilon");
        upperBound = cfg.getDouble("LearningAlgorithm.RMax.UpperBound");
    }

    @Override
    public void commence() {

    }

    @Override
    public void conclude() {

    }

    @Override
    public int select(State state, int time) {
        return select(state, time, false);
    }

    @Override
    public int select(State state, int time, boolean greedy) {
        return Argmax.select(Q(state.hashCode()));
    }

    @Override
    public void clearTransitions() {

    }

    @Override
    public void update(State state, int a, State state_, double r) {
        int s  = state.hashCode();
        int s_ = state_.hashCode();

        if (n(s, a) < m) {
            // Record the state-action
            increment_n(s, a);
            // Record the immediate reward
            increment_r(s, a, r);
            // Record the immediate next state
            increment_n(s, a, s_);

            if (n(s, a) == m) {
                updateModel(state.availableActions());
            }
        }
    }

    protected void updateModel(int nActions) {
        int nUpdates = (int) Math.ceil((Math.log(1 / (epsilon * (1 - gamma)))) / (1 - gamma));
        for (int i = 0; i < nUpdates; i++) {
            // Update the estimated Q function
            for (int s_bar : n1_func.keySet()) {
                for (int a_bar = 0; a_bar < nActions; a_bar++) {
                    if (n(s_bar, a_bar) >= m) {
                        double summation = 0.0d;
                        for (int s_bar_ : n2_func.get(s_bar).get(a_bar).keySet()) {
                            summation += T_hat(s_bar_, s_bar, a_bar) * Argmax.max(Q(s_bar_));
                        }

                        update_Q(s_bar, a_bar, R_hat(s_bar, a_bar) + (gamma * summation));
                    }
                }
            }
        }
    }

    protected int n(int s, int a) {
        try {
            return n1_func.get(s).get(a);
        } catch (Exception e) {
            if (!n1_func.containsKey(s)) {
                n1_func.put(s, new HashMap<>());
            }
            if (!n1_func.get(s).containsKey(a)) {
                n1_func.get(s).put(a, 0);
            }

            return 0;
        }
    }

    protected int n(int s, int a, int s_) {
        try {
            return n2_func.get(s).get(a).get(s_);
        } catch (Exception e) {
            if (!n2_func.containsKey(s)) {
                n2_func.put(s, new HashMap<>());
            }
            if (!n2_func.get(s).containsKey(a)) {
                n2_func.get(s).put(a, new HashMap<>());
            }
            if (!n2_func.get(s).get(a).containsKey(s_)) {
                n2_func.get(s).get(a).put(s_, 0);
            }

            return 0;
        }
    }

    protected void increment_n(int s, int a) {
        int n = n(s, a);
        n1_func.get(s).put(a, n + 1);
    }

    protected void increment_n(int s, int a, int s_) {
        int n = n(s, a, s_);
        n2_func.get(s).get(a).put(s_, n + 1);
    }

    protected double r(int s, int a) {
        try {
            return r_func.get(s).get(a);
        } catch (Exception e) {
            if (!r_func.containsKey(s)) {
                r_func.put(s, new HashMap<>());
            }
            if (!r_func.get(s).containsKey(a)) {
                r_func.get(s).put(a, 0.0d);
            }

            return 0.0d;
        }
    }

    protected void increment_r(int s, int a, double v) {
        double r = r(s, a);
        r_func.get(s).put(a, r + v);
    }

    protected double R_hat(int s, int a) {
        double n  = n(s, a);
        double p1 = (1 / n);
        double p2 = r(s, a);

        return p1 * p2;
    }

    protected double T_hat(int s_, int s, int a) {
        return n(s, a, s_) / n(s, a);
    }

    protected double[] Q(int s) {
        return qTable.get(s);
    }

    protected double Q(int s, int a) {
        return qTable.get(s)[a];
    }

    protected double U(int s, int a) {
        return upperBound;
    }

    protected void update_Q(int s, int a, double value) {
        qTable.put(s, a, value);
    }

    @Override
    public boolean savePolicy(File file) {
        return qTable.save(file);
    }

    @Override
    public boolean loadPolicy(File file) {
        return qTable.load(file);
    }
}
