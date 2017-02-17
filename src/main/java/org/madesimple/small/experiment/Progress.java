package org.madesimple.small.experiment;

import java.util.Arrays;

/**
 * Progress is a simple tool for monitoring progress of a single task or a set of tasks.
 *
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public abstract class Progress {

    /**
     * Name of the progress being monitored.
     */
    private String name;

    /**
     * @param name Name of the progress
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Name of the progress
     */
    public String getName() {
        return name;
    }

    /**
     * @return Percentage complete (0 - 100)%
     */
    public abstract double percentage();

    /**
     * @return Number of completed parts
     */
    public abstract long amount();

    /**
     * @return Total number of parts
     */
    public abstract long target();

    /**
     * @return True if complete, false if not
     */
    public abstract boolean complete();

    /**
     * A individual task to be monitored. The number of parts to the task is set as <em>maximum</em>. The progress is
     * updated by either single {@link #increment()}s or in batches ({@link #increment(long)}).
     *
     * @author Peter Scopes (peter.scopes@gmail.com)
     */
    public static class Task extends Progress {
        /**
         * Initial amount.
         */
        private long initial;
        /**
         * Current amount.
         */
        private long amount;
        /**
         * Target amount.
         */
        private long maximum;

        /**
         * @param maximum Target amount
         */
        public Task(long maximum) {
            this(maximum, 0);
        }

        /**
         * @param maximum Target amount
         * @param initial Initial amount
         */
        public Task(long maximum, long initial) {
            setTarget(maximum);
            reset(initial);
        }

        /**
         * Increment the progress by 1.
         */
        public synchronized void increment() {
            increment(1);
        }

        /**
         * Increment the progress by <em>amount</em>.
         *
         * @param amount Increment amount
         */
        public synchronized void increment(long amount) {
            this.amount = Math.min(maximum, this.amount + amount);
        }

        @Override
        public synchronized double percentage() {
            return ((double) amount / (double) maximum) * 100.0d;
        }

        @Override
        public String toString() {
            return String.format("% 6.2f%%", percentage());
        }

        @Override
        public long amount() {
            return amount;
        }

        @Override
        public long target() {
            return maximum;
        }

        @Override
        public boolean complete() {
            return amount == maximum;
        }

        /**
         * @param maximum Target amount
         */
        public void setTarget(long maximum) {
            this.maximum = Math.max(1, maximum);
        }

        /**
         * Reset the progress to the <em>initial</em> amount.
         */
        public void reset() {
            reset(initial);
        }

        /**
         * Reset the progress to a new <em>initial</em> amount.
         *
         * @param initial Initial amount
         */
        public void reset(long initial) {
            this.initial = initial;
            this.amount = Math.max(0, Math.min(maximum, initial));
        }
    }

    /**
     * A set of Tasks to be monitored. The number of parts is the number of tasks. The progress can only be updated by
     * incrementing the tasks that make up the Agenda.
     *
     * @author Peter Scopes (peter.scopes@gmail.com)
     */
    public static class Agenda extends Progress {
        /**
         * Set of tasks that comprise the agenda.
         */
        private Progress.Task[] tasks;

        /**
         * @param tasks Tasks to complete
         */
        public Agenda(Progress.Task... tasks) {
            this.tasks = tasks;
        }

        /**
         * Add a task to the Agenda.
         *
         * @param task To be added
         */
        public void add(Progress.Task task) {
            tasks = Arrays.copyOf(tasks, tasks.length + 1);
            tasks[tasks.length + 1] = task;
        }

        /**
         * @return All Tasks in the Agenda
         */
        public Progress.Task[] tasks() {
            return tasks;
        }

        /**
         * @param number Task number
         * @return A specific task in the Agenda
         */
        public Progress.Task task(int number) {
            return tasks[number];
        }

        /**
         * @return Tally of completed tasks
         */
        public long tally() {
            long tally = 0;
            for (Progress item : tasks) {
                tally += item.complete() ? 1 : 0;
            }

            return tally;
        }

        /**
         * @return Total number of tasks
         */
        public long total() {
            return tasks.length;
        }

        @Override
        public double percentage() {
            double sumPercentage = 0.0d;
            for (Progress.Task task : tasks) {
                sumPercentage += task.percentage();
            }

            return sumPercentage / tasks.length;
        }

        @Override
        public long amount() {
            long amount = 0;
            for (Progress item : tasks) {
                amount += item.amount();
            }

            return amount;
        }

        @Override
        public long target() {
            long target = 0;
            for (Progress item : tasks) {
                target += item.target();
            }

            return target;
        }

        @Override
        public boolean complete() {
            return tally() == total();
        }
    }
}
