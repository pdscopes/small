package org.madesimple.small.experiment;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.agent.learning.LearningAgent;
import org.madesimple.small.environment.TurnBasedEnvironment;
import org.madesimple.small.experiment.observation.TurnBasedRewardObservation;

import java.util.Observable;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class Simulation extends Observable implements Runnable {

    protected Progress.Task        task;
    protected TurnBasedExperiment  experiment;
    protected TurnBasedEnvironment environment;
    protected TurnBasedEnvironment evaluation;
    protected Agent[]              agents;
    protected int                  run;

    public void setExperiment(TurnBasedExperiment experiment) {
        this.experiment = experiment;
    }

    public void setTask(Progress.Task task) {
        this.task = task;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public void setEnvironment(TurnBasedEnvironment environment) {
        this.environment = environment;
    }

    public void setEvaluation(TurnBasedEnvironment evaluation) {
        this.evaluation = evaluation;
    }

    public void setAgents(Agent[] agents) {
        this.agents = agents;
    }

    protected void evaluate(int run, int update, int episode) {
        // Initialise the evaluation environment
        evaluation.initialise();

        // Turn on the evaluation mode of the agents
        for (Agent agent : agents) {
            environment.remove(agent);
            evaluation.add(agent);
            if (agent instanceof LearningAgent) {
                ((LearningAgent) agent).setEvaluationMode(true);
            }
        }

        // Restart the evaluation environment
        evaluation.restart();

        // Play out an episode
        int turn;
        for (turn = 1; !evaluation.isTerminal() && (environment.maxTurns() == 0 || turn <= environment.maxTurns()); turn++) {
            evaluation.performTurn();
        }

        // Store the observation
        setChanged();
        notifyObservers(new TurnBasedRewardObservation(run, update, episode, turn, evaluation, agents));

        // Turn off the evaluation mode of the agents
        for (Agent agent : agents) {
            evaluation.remove(agent);
            environment.add(agent);
            if (agent instanceof LearningAgent) {
                ((LearningAgent) agent).setEvaluationMode(false);
            }
        }
    }
}
