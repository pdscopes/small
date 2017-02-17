package org.madesimple.small.experiment;

import org.madesimple.small.utility.Factory;

import java.util.concurrent.Semaphore;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class Simulation implements Runnable {
    public enum Type {
        CONCURRENT, SEQUENTIAL
    }

    protected Experiment experiment;
    protected Factory<ExperimentRunnable> runnableFactory;
    protected Progress.Agenda agenda;

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public void setRunnableFactory(Factory<ExperimentRunnable> runnableFactory) {
        this.runnableFactory = runnableFactory;
    }

    public void setAgenda(Progress.Agenda agenda) {
        this.agenda = agenda;
    }

    public static final class Concurrent extends Simulation {

        @Override
        public void run() {
            // Get the number of available processors
            int             availableProcessors = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
            final Semaphore semaphore           = new Semaphore(availableProcessors, true);

            // Run the experiments
            for (int run = 1; run <= experiment.totalRuns; run++) {
                try {
                    semaphore.acquire();

                    ExperimentRunnable runnable = runnableFactory.generate();
                    runnable.setRun(run);
                    runnable.setTask(agenda.task(run-1));

                    Thread th = new Thread(runnable);
                    th.start();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static final class Sequential extends Simulation {

        @Override
        public void run() {
            // Run the experiments
            for (int run = 1; run <= experiment.totalRuns; run++) {
                ExperimentRunnable runnable = runnableFactory.generate();
                runnable.setRun(run);
                runnable.setTask(agenda.task(run-1));
                runnable.run();
            }
        }
    }
}
