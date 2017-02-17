package org.madesimple.small.agent.learning.storage;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Discrete Q-Table is an implementation of a Q-Table specifically made for
 * Discrete Q-Learning. It allows for simple access and updates of Q values
 * within the table of state representations with discrete actions. That is to
 * say natural number valued actions.
 * </p>
 * <p>
 * The table uses a state access control mechanism, by this I mean it doesn't
 * keep track itself of the number of actions each state representation has but
 * assumes that it will be informed if the number of actions it should be
 * considering changes.
 * </p>
 * <p>
 * A nice feature of this Q-Table is that it isn't required before hand to
 * know the number of state representations that will be encountered along the
 * way but can dynamically increase it's size when needed. This obviously has a
 * latent cost so where possible it is recommended to use the
 * DiscreteQTable(int nStates) as the constructor to give it a large starting
 * size since it defaults to a maximum size of 10.
 * </p>
 * <p>
 * <em>Note:</em> That if you underestimate the number of states in the
 * constructor the dynamic increase of size will still happen.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class ActionValueTable {
    private static final class ActionValues {
        double[] values;

        public ActionValues(int nActions, double initialValue) {
            values = new double[nActions];
            Arrays.fill(values, initialValue);
        }
    }

    private Map<Integer, ActionValues> table;


    public ActionValueTable() {
        this(10);
    }

    public ActionValueTable(int nStates) {
        table = new HashMap<>(nStates);
        reset();
    }

    public void reset() {
        table.clear();
    }

    public void put(int state, int action, double value) {
        table.get(state).values[action] = value;
    }

    public void put(int state, int action, double value, int nActions) {
        ActionValues actionValues;

        if ((actionValues = table.get(state)) == null) {
            actionValues = new ActionValues(nActions, 0.0);
            table.put(state, actionValues);
        }

        actionValues.values[action] = value;
    }

    public double[] get(int state) {
        return table.get(state).values;
    }

    public double[] get(int state, int nActions) {
        ActionValues actionValues;

        if ((actionValues = table.get(state)) == null) {
            actionValues = new ActionValues(nActions, 0.0);
            table.put(state, actionValues);
        }

        return actionValues.values;
    }

    /**
     * <p>
     * Writes the contents of the table to <em>file</em>. Overwrites existing data.
     * </p>
     * <p>
     * The output is in a plain text, CSV style format. Each row is an entry in the table; the first column is the hash
     * code and of the state and the following columns are the respective values for action 1 to <em>n</em>.
     * </p>
     *
     * @param file file to save ActionValueTable data
     * @return True on success, false on failure
     */
    public boolean save(File file) {
        try (PrintStream ps = new PrintStream(file)) {
            for (int key : table.keySet()) {
                ps.print(key);
                for (double value : table.get(key).values) {
                    ps.print("\t" + value);
                }

                ps.println();
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * <p>
     * Reads from <em>file</em> and overwrites all the entries in the current table. Existing entries persist.
     * </p>
     * <p>
     * The input format should be the same as the output format used by {@link #save(File)}. It should be in plain text,
     * CSV style format. Each row is an entry in the table; the first column is the hash code of the state and the
     * following columns as the values of actions 1 to <em>n</em>.
     * </p>
     *
     * @param file file to load ActionValueTable
     * @return True on success, false on failure
     */
    public boolean load(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[]     parts        = line.split("\t");
                Integer      key          = Integer.parseInt(parts[0]);
                ActionValues actionValues = new ActionValues(parts.length - 1, 0.0);

                for (int i = 1; i < parts.length; i++) {
                    actionValues.values[i - 1] = Double.parseDouble(parts[i]);
                }

                table.put(key, actionValues);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
