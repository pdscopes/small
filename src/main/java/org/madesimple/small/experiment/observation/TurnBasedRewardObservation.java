package org.madesimple.small.experiment.observation;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.environment.Environment;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class TurnBasedRewardObservation {

    private int run;
    private int update;
    private int episode;
    private int turn;
    private double[] rewards;

    public TurnBasedRewardObservation(int run, int update, int episode, int turn, double[] rewards) {
        set(run, update, episode, turn, rewards);
    }

    public TurnBasedRewardObservation(int run, int update, int episode, int turn, Environment environment, Agent[] agents) {
        double[] rewards = new double[agents.length];
        for (int i=0; i<rewards.length; i++) {
            rewards[i] = agents[i].accumulativeReward(environment);
        }

        set(run, update, episode, turn, rewards);
    }

    private void set(int run, int update, int episode, int turn, double[] rewards) {
        this.run = run;
        this.update = update;
        this.episode = episode;
        this.turn = turn;
        this.rewards = rewards;
    }

    @Override
    public String toString() {
        String agents = "";
        for (double reward: rewards) {
            agents += "\t" + Double.toString(reward);
        }
        return String.format("%d\t%d\t%d\t%d%s", run, update, episode, turn, agents);
    }
}
