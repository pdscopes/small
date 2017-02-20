package org.madesimple.small.agent.learning.storage;

import java.io.File;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public interface QTable {
    /**
     * Set the initial value of any unencountered rows of the Q-Table.
     *
     * @param initialValue initial value
     */
    void setInitialValue(double initialValue);

    /**
     * Reset the Q-Table.
     */
    void reset();

    /**
     *
     * @param state    Row to update
     * @param action   Column to update
     * @param value    New value
     * @param nActions Number of columns
     */
    void put(int state, int action, double value, int nActions);

    /**
     *
     * @param state    Row to get
     * @param nActions Number of columns
     * @return Q Values for state
     */
    double[] get(int state, int nActions);

    /**
     * Load the strategy stored in <em>file</em>.
     *
     * @param file Policy file
     * @return True on success, false on failure
     */
    boolean save(File file);

    /**
     * Save the strategy in <em>file</em>.
     *
     * @param file Policy file
     * @return True on success, false on failure
     */
    boolean load(File file);
}
