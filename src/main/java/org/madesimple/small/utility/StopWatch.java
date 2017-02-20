package org.madesimple.small.utility;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class StopWatch {
    private enum State {
        RUNNING, PAUSED, STOPPED
    }

    private long start,
            pause;
    private State state;


    /**
     * <p>Construct a new Stopwatch.</p>
     */
    public StopWatch() {
        start = pause = 0L;
        state = State.STOPPED;
    }


    /**
     * <p>(re)Start the clock.</p>
     */
    public void start() {
        start = System.currentTimeMillis();
        state = State.RUNNING;
    }

    /**
     * <p>Pause the clock. This only has any affect if the
     * stopwatch is {@link #start() running}.</p>
     *
     * @return True if the stopwatch was paused
     */
    public boolean pause() {
        if (state == State.RUNNING) {
            pause = System.currentTimeMillis() - start;
            state = State.PAUSED;

            return true;
        }
        return false;
    }

    /**
     * <p>Resume the clock. This only has any affect if the
     * stopwatch is {@link #pause() paused}.</p>
     *
     * @return True if was paused and has now been resumed
     */
    public boolean resume() {
        if (state == State.PAUSED) {
            start = System.currentTimeMillis() - pause;
            pause = 0L;
            state = State.RUNNING;

            return true;
        }
        return false;
    }

    /**
     * <p>Stop the clock. This only has any affect if the
     * stopwatch is either {@link #start() running} or has
     * been {@link #pause() paused}.</p>
     */
    public void stop() {
        if (state == State.RUNNING || state == State.PAUSED) {
            pause = System.currentTimeMillis() - start;
            start = 0L;
            state = State.STOPPED;
        }
    }

    /**
     * <p>Will return <code>True</code> if the stopwatch has been
     * {@link #start() started} and if the stopwatch isn't current
     * {@link #pause() paused} nor has been {@link #stop() stopped}.</p>
     *
     * @return True if the stopwatch is currently running
     */
    public boolean isRunning() {
        return state == State.RUNNING;
    }

    /**
     * <p>Will return <code>True</code> if the stopwatch has been
     * {@link #start() started} and has subsequently it has been
     * {@link #pause() paused} and not been {@link #stop() stopped}
     * nor {@link #resume() unpaused}.</p>
     *
     * @return True if the stopwatch is currently paused
     */
    public boolean isPaused() {
        return state == State.PAUSED;
    }

    /**
     * <p>Will return <code>True</code> if the stopwatch has been
     * {@link #stop() stopped} (which means it has been {@link #start() started}
     * and then stopped or has never been started.</p>
     *
     * @return True if the stopwatch either currently stopped or has never been run
     */
    public boolean isStopped() {
        return state == State.STOPPED;
    }


    /**
     * <p>Will return the amount of time on the stopwatch.</p>
     *
     * @return The amount of time since {@link #start() start} was called, ignoring the time when
     * the clock was paused
     */
    public long getTime() {
        switch (state) {
            default:
            case STOPPED:
            case PAUSED:
                return pause;
            case RUNNING:
                return System.currentTimeMillis() - start;
        }
    }


    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Stopwatch[t-" + (getTime() / 1000.0) + "s]";
    }
}
