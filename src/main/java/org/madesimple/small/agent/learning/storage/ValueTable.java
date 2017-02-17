package org.madesimple.small.agent.learning.storage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Discrete Value Table is an implementation of a value table specifically made
 * for YORLL. It allows for simple access and updates of state values.
 * </p>
 * <p>
 * A nice feature of this Value Table is that it isn't required before hand to
 * know the number of state representations that will be encountered along the
 * way but can dynamically increase it's size when needed. This obviously has a
 * latent cost so where possible it is recommended to use the
 * DiscreteValueTable(int nStates) as the constructor to give it a large
 * starting size since it defaults to a maximum size of 10.
 * </p>
 * <p>
 * <em>Note:</em> That if you underestimate the number of states in the
 * constructor the dynamic increase of size will still happen.
 * </p>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class ValueTable {
    private Map<Integer, Double> table;

    public ValueTable() {
        this(10);
    }

    public ValueTable(int nStates) {
        table = new HashMap<>(nStates);
        reset();
    }

    public void reset() {
        table.clear();
    }

    public void put(int state, double value) {
        table.put(state, value);
    }

    public double get(int state) {
        return table.get(state);
    }

    public boolean has(int state) {
        return table.containsKey(state);
    }

    public boolean save(File file) {
        try (PrintStream ps = new PrintStream(file)) {
            for (int key : table.keySet()) {
                ps.printf("%d\t%f", key, table.get(key));
                ps.println();
            }

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean load(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                Integer  key   = Integer.parseInt(parts[0]);
                Double   value = Double.parseDouble(parts[1]);

                table.put(key, value);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
