package org.madesimple.small.experiment;

import org.madesimple.small.utility.Factory;
import org.madesimple.small.utility.StopWatch;

import java.util.concurrent.Semaphore;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Simulator implements Runnable {

    public enum Type {
        CONCURRENT, SEQUENTIAL
    }

    private TurnBasedExperiment experiment;
    private Factory<Simulation> simulationFactory;

    public Simulator(TurnBasedExperiment experiment, Factory<Simulation> simulationFactory) {
        this.experiment = experiment;
        this.simulationFactory = simulationFactory;
    }

    @Override
    public void run() {
        // Output that the simulation is beginning
        System.out.println("####################################\n------------------------------------");
        System.out.println("Starting Simulation of Experiment: \"" + experiment.name + "\"");
        System.out.println(experiment.properties + "\n\n");

        // Create the task monitor
        final Progress.Task[] tasks = new Progress.Task[experiment.totalRuns];
        for (int i = 0; i < experiment.totalRuns; i++) {
            tasks[i] = new Progress.Task(experiment.totalUpdates);
            tasks[i].setName("Run " + (i + 1));
        }
        final Progress.Agenda agenda = new Progress.Agenda(tasks);
        agenda.setName(experiment.name);

        // Start the simulation
        StopWatch sw = new StopWatch();
        sw.start();
        switch (experiment.simulatorType) {
            case CONCURRENT:
                concurrent(agenda);
                break;
            case SEQUENTIAL:
                sequential(agenda);
                break;
            default:
                throw new IllegalArgumentException("Unknown simulation type");
        }
        sw.stop();

        System.out.println("\n\n" + "Simulation ran for " + sw.getTime() + "ms\n");
        System.out.println("------------------------------------\n####################################");
    }

    /**
     * Gets the number of available processors, <code>n</code>,  and attempts to run <code>n - 1</code> simulations
     * concurrently.
     *
     * @param agenda agenda
     */
    private void concurrent(Progress.Agenda agenda) {
        // Get the number of available processors
        int             availableProcessors = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        final Semaphore semaphore           = new Semaphore(availableProcessors, true);

        // Run the experiments
        for (int run = 1; run <= experiment.totalRuns; run++) {
            try {
                semaphore.acquire();

                Simulation runnable = this.simulationFactory.generate();
                runnable.setRun(run);
                runnable.setTask(agenda.task(run-1));

                Thread th = new Thread(runnable);
                th.start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Runs <code>experiment.totalRuns</code> simulations of the experiment sequentially.
     * @param agenda agenda
     */
    private void sequential(Progress.Agenda agenda) {
        // Run the experiments
        for (int run = 1; run <= experiment.totalRuns; run++) {
            Simulation runnable = this.simulationFactory.generate();
            runnable.setRun(run);
            runnable.setTask(agenda.task(run-1));
            runnable.run();
        }
    }
}
