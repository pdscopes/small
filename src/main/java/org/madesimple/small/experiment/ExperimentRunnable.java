package org.madesimple.small.experiment;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.agent.learning.LearningAgent;
import org.madesimple.small.environment.TurnBasedEnvironment;
import org.madesimple.small.experiment.observation.TurnBasedRewardObservation;
import org.madesimple.small.utility.Configurable;
import org.madesimple.small.utility.Configuration;

import java.util.Observable;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * ExperimentRunnable can run both <em>episodic</em> and <em>continuous</em>
 * experiments.
 * </p>
 * <p>
 * Warnings:
 * </p>
 * <ul>
 * <li>When using the &quot;<em>during</em>&quot; evaluation point ensure that the
 * previous and current states are currently retained.</li>
 * </ul>
 * <p>
 * Valid <em>episodic</em> experiment configurations are as follows:
 * </p>
 * <pre>
 * ## Configuration of running the simulation
 * TurnBasedExperiment.RandomSeed      = [-1 | >= 0]
 * TurnBasedExperiment.NumRuns         = [ >= 1]
 * TurnBasedExperiment.NumUpdates      = [ >= 1]
 * TurnBasedExperiment.NumEpisodes     = [ >= 1]
 * TurnBasedExperiment.NumSteps        = [0  | >= 1]
 * TurnBasedExperiment.RunningType     = episodic
 * TurnBasedExperiment.LimitType       = [episodes | updates]
 * TurnBasedExperiment.EvaluationPoint = [before | during | after]
 * TurnBasedExperiment.OutputFrequency = [ >= 1]
 * TurnBasedExperiment.NumAgents       = [ >= 0]
 * #  For TurnBasedSimulator
 * TurnBasedSimulator.ProgressUpdateTime = [-1 | >= 1]
 * #  For statistics
 * Statistics.WindowSize      = [ >= 1]
 * </pre>
 * <p>
 * Valid <em>continuous</em> experiment configurations are as follows:
 * </p>
 * <pre>
 * ## Configuration of running the simulation
 * Experiment.RandomSeed      = [-1 | >= 0]
 * Experiment.NumRuns         = [ >= 1]
 * ; Experiment.NumUpdates      =
 * ; Experiment.NumEpisodes     =
 * ; Experiment.NumSteps        =
 * Experiment.RunningType     = continuous
 * ; Experiment.LimitType       =
 * ; Experiment.EvaluationPoint = during
 * Experiment.OutputFrequency = [ >= 1]
 * Experiment.NumAgents       = [ >= 0]
 * #  For TurnBasedSimulator
 * TurnBasedSimulator.ProgressUpdateTime = [-1 | >= 1]
 * #  For statistics
 * Statistics.WindowSize      = [ >= 1]
 * </pre>
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class ExperimentRunnable extends Observable implements Runnable {
    protected TurnBasedExperiment  experiment;
    protected Progress.Task        task;
    protected TurnBasedEnvironment environment;
    protected TurnBasedEnvironment evaluation;
    protected Agent[]              agents;

    protected int run;

    public ExperimentRunnable(TurnBasedExperiment experiment) {
        this.experiment = experiment;
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

    public void setTask(Progress.Task task) {
        this.task = task;
    }

    @Override
    public void run() {
        // Seed the random singleton
        if (experiment.randomSeed != -1) {
            ThreadLocalRandom.current().setSeed(experiment.randomSeed);
        }

        // Initialise the environment and agents
        environment.initialise();
        for (Agent agent : agents) {
            agent.initialise();
        }

        // Add the agents to the environment
        for (Agent agent : agents) {
            environment.add(agent);
        }

        // Evaluation the initial situation
        evaluationRun(run, 0, 0);

        // Run the experiment
        for (int update = 1, episode = 1; update <= experiment.totalUpdates; episode++) {
            // Restart the environment at the beginning of an episode
            environment.restart();

            // Play out an episode
            for (int turn = 1; !environment.isTerminal() && (environment.maxTurns() == 0 || turn <= environment.maxTurns()) && update <= experiment.totalUpdates; turn++, update++) {
                // Perform the next turn
                environment.performTurn();

                // Increment the task task
                task.increment();

                // If evaluation point
                if (update % experiment.observationFrequency == 0) {
                    evaluationRun(run, update, episode);
                }
            }
        }
    }

    private void evaluationRun(int run, int update, int episode) {
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
