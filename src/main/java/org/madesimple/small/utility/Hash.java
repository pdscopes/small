package org.madesimple.small.utility;

/**
 * @author Peter Scopes (peter.scopes@gmail.com)
 */
public class Hash {
    /**
     * Convert two integers into a single integer representation.
     *
     * @param x integer
     * @param y integer
     * @return integer representation
     */
    public static int pair(int x, int y) {
        return ((x + y) * (x + y + 1) / 2) + y;
    }

    /**
     * Convert an array of integers into a single integer representation.
     *
     * @param ints integer array
     * @return integer representation
     */
    public static int pair(int... ints) {
        if (ints.length == 1) {
            return ints[0];
        }
        if (ints.length == 2) {
            return pair(ints[0], ints[1]);
        } else {
            int hash = pair(ints[0], ints[1]);
            for (int i = 2; i < ints.length; i++) {
                hash = pair(hash, ints[i]);
            }
            return hash;
        }
    }

    /**
     * Convert an array of booleans into a single integer representation.
     *
     * @param bools boolean array
     * @return integer representation
     */
    public static int hash(boolean... bools) {
        int hash = 0;
        for (boolean b : bools) {
            hash = (hash << 1) + (b ? 1 : 0);
        }
        return hash;
    }

    /**
     * Convert an array of Enums into a single integer representation.
     *
     * @param enums enum array
     * @param <E>   Enum
     * @return integer representation
     */
    public static <E extends Enum<E>> int hash(Enum<E>... enums) {
        int[] ints = new int[enums.length];
        for (int i = 0; i < enums.length; i++) {
            ints[i] = enums[i].ordinal();
        }

        return pair(ints);
    }
}
