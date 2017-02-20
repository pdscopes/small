package org.madesimple.small.experiment.simulation;

import org.madesimple.small.agent.Agent;
import org.madesimple.small.experiment.Simulation;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Sampling extends Simulation {

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
        for (int update = 1; update <= experiment.totalUpdates; update++) {
            // Place the environment in a new random state
            environment.reseed();

            // Perform the next turn
            environment.performTurn();

            // Increment the task
            task.increment();

            // If evaluation point
            if (update % experiment.observationFrequency == 0) {
                evaluate(run, update, 0);
            }
        }
    }
}
