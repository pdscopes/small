package org.madesimple.small.experiment;

import org.madesimple.small.utility.Factory;
import org.madesimple.small.utility.StopWatch;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Simulator implements Runnable {

    private TurnBasedExperiment         experiment;
    private Factory<ExperimentRunnable> runnableFactory;

    public Simulator(TurnBasedExperiment experiment, Factory<ExperimentRunnable> runnableFactory) {
        this.experiment = experiment;
        this.runnableFactory = runnableFactory;
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

        // Create the simulation
        Simulation simulation;
        switch (experiment.simulationType) {
            case CONCURRENT:
                simulation = new Simulation.Concurrent();
                break;
            case SEQUENTIAL:
                simulation = new Simulation.Sequential();
                break;
            default:
                throw new IllegalArgumentException("Unknown simulation type");
        }
        simulation.setExperiment(experiment);
        simulation.setRunnableFactory(runnableFactory);
        simulation.setAgenda(agenda);

        // Start timing the simulation
        StopWatch sw = new StopWatch();
        sw.start();
        simulation.run();
        sw.stop();

        System.out.println("\n\n" + "Simulation ran for " + sw.getTime() + "ms\n");
        System.out.println("------------------------------------\n####################################");
    }
}
