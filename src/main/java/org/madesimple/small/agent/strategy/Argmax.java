package org.madesimple.small.agent.strategy;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Argmax {

    /**
     * Selects the argument maximum (argmax) ranked action for the given set of actionValuePairs.
     * Randomly decide between equally best valued actions.
     *
     * @param actionValuePairs action-value pairs
     * @return argmax action
     */
    public static int select(double[] actionValuePairs) {
        return Argmax.select(actionValuePairs, true);
    }

    /**
     * Selects the argument maximum (argmax) ranked action for the given set of actionValuePairs.
     * Randomly decide between equally best valued actions.
     *
     * @param actionValuePairs action-value pairs
     * @param random           True to randomly distinguish best action, false if not
     * @return argmax action
     */
    public static int select(double[] actionValuePairs, boolean random) {
        // Initialise with the first element
        int    nTies      = 1;
        int    bestAction = 0;
        double bestValue  = actionValuePairs[0];

        // Search for possible better actions
        for (int i = 1; i < actionValuePairs.length; i++) {
            // If this action-value pair is greater than the current best
            if (actionValuePairs[i] > bestValue) {
                bestValue = actionValuePairs[i];
                bestAction = i;
                nTies = 1;
            }
            // If this action-value pair is equal to the current best
            else if (actionValuePairs[i] == bestValue) {
                // Randomly determine the "best" action
                if (random && ThreadLocalRandom.current().nextInt(++nTies) == 0) {
                    bestAction = i;
                }
            }
        }

        return bestAction;
    }

    /**
     * Counts the number of actions that share the argmax value.
     *
     * @param actionValuePairs action-value pairs
     * @return count of actions that have argmax value
     */
    public static int countArgmax(double[] actionValuePairs) {
        // Initialise with the first element
        int    nTies     = 1;
        double bestValue = actionValuePairs[0];

        // Search for possible better actions
        for (int i = 1; i < actionValuePairs.length; i++) {
            // If this action-value pair is greater than the current best
            if (actionValuePairs[i] > bestValue) {
                bestValue = actionValuePairs[i];
                nTies = 1;
            }
            // If this action-value pair is equal to the current best
            else if (actionValuePairs[i] == bestValue) {
                nTies++;
            }
        }

        return nTies;
    }

    /**
     * @param actionValuePairs action-value pairs
     * @return Array containing best actions
     */
    public static int[] argmaxSet(double[] actionValuePairs) {
        // Initialise with the first element
        int    nTies       = 1;
        int[]  bestActions = new int[actionValuePairs.length];
        double bestValue   = actionValuePairs[0];

        // Search for possible better actions
        for (int i = 1; i < actionValuePairs.length; i++) {
            // If this action-value pair is greater than the current best
            if (actionValuePairs[i] > bestValue) {
                bestActions[0] = i;
                bestValue = actionValuePairs[i];
                nTies = 1;
            }
            // If this action-value pair is equal to the current best
            else if (actionValuePairs[i] == bestValue) {
                bestActions[nTies++] = i;
            }
        }


        return Arrays.copyOfRange(bestActions, 0, nTies);
    }

    /**
     * @param actionValuePairs1 action-value pairs
     * @param actionValuePairs2 action-value pairs
     * @return True if same set of best actions, false otherwise
     */
    public static boolean matches(double[] actionValuePairs1, double[] actionValuePairs2) {
        // Calculate the argmax sets of each action-value pairs
        int[] argmaxSet1 = Argmax.argmaxSet(actionValuePairs1);
        int[] argmaxSet2 = Argmax.argmaxSet(actionValuePairs2);

        if (argmaxSet1.length != argmaxSet2.length) {
            return false;
        }

        for (int i = 0; i < argmaxSet1.length; i++) {
            if (argmaxSet1[i] != argmaxSet2[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param actionValuePairs1 action-value pairs
     * @param actionValuePairs2 action-value pairs
     * @return True if equal, false otherwise
     */
    public static boolean equals(double[] actionValuePairs1, double[] actionValuePairs2) {
        if (actionValuePairs1.length != actionValuePairs2.length) {
            return false;
        }

        for (int i = 0; i < actionValuePairs1.length; i++) {
            if (actionValuePairs1[i] != actionValuePairs2[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param actionValuePairs action-value pairs
     * @return minimum value
     */
    public static double min(double[] actionValuePairs) {
        double min = actionValuePairs[0];
        for (int i = 1; i < actionValuePairs.length; i++) {
            min = Math.min(min, actionValuePairs[i]);
        }

        return min;
    }

    /**
     * @param actionValuePairs action-value pairs
     * @return maximum value
     */
    public static double max(double[] actionValuePairs) {
        double max = actionValuePairs[0];
        for (int i = 1; i < actionValuePairs.length; i++) {
            max = Math.max(max, actionValuePairs[i]);
        }

        return max;
    }

    /**
     * @param actionValuePairs action-value pairs
     * @return maximum value
     */
    public static double sum(double[] actionValuePairs) {
        double sum = actionValuePairs[0];
        for (int i = 1; i < actionValuePairs.length; i++) {
            sum += actionValuePairs[i];
        }

        return sum;
    }
}
