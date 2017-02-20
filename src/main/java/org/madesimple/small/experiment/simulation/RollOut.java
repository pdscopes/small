package org.madesimple.small.experiment.simulation;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.experiment.Simulation;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class RollOut extends Simulation {

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

        // Run the experiment
        for (int update = 1, episode = 1; update <= experiment.totalUpdates; episode++) {
            // Restart the environment at the beginning of an episode
            environment.restart();

            // Play out an episode
            for (int turn = 1; !environment.isTerminal() && (environment.maxTurns() == 0 || turn <= environment.maxTurns()) && update <= experiment.totalUpdates; turn++, update++) {
                // Perform the next turn
                environment.performTurn();

                // Increment the task
                task.increment();

                // If evaluation point
                if (update % experiment.observationFrequency == 0) {
                    evaluate(run, update, episode);
                }
            }
        }
    }
}
