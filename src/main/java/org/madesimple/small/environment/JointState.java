package org.madesimple.small.environment;

import org.madesimple.small.utility.Hash;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class JointState implements State {
    private List<State> states;

    /**
     *
     */
    public JointState() {
        states = new ArrayList<>();
    }

    public JointState(State s) {
        this();
        add(s);
    }

    public JointState(Collection<State> s) {
        this();
        addAll(s);
    }


    public void add(State state) {
        states.add(state);
    }

    public void addAll(Collection<State> states) {
        this.states.addAll(states);
    }

    public int size() {
        return states.size();
    }

    public State get(int i) {
        return states.get(i);
    }

    @Override
    public void set(State s) {
        if (s instanceof JointState) {
            JointState that = (JointState) s;
            this.states.clear();
            this.states.addAll(that.states);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JointState) {
            JointState that = (JointState) obj;
            if (this.states.size() != that.states.size()) {
                return false;
            }
            for (int i = 0; i < this.states.size(); i++) {
                if (!this.states.get(i).equals(that.states.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int[] hashes = new int[states.size()];
        for (int i = 0; i < states.size(); i++) {
            hashes[i] = states.get(i).hashCode();
        }

        return Hash.pair(hashes);
    }

    @Override
    public String toString() {
        return states.toString();
    }
}
