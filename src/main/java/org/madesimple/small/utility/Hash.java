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
     * @param integers integer array
     * @return integer representation
     */
    public static int pair(int... integers) {
        if (integers.length == 1) {
            return integers[0];
        }
        if (integers.length == 2) {
            return pair(integers[0], integers[1]);
        } else {
            int hash = pair(integers[0], integers[1]);
            for (int i = 2; i < integers.length; i++) {
                hash = pair(hash, integers[i]);
            }
            return hash;
        }
    }

    /**
     * Convert an array of booleans into a single integer representation.
     *
     * @param booleans boolean array
     * @return integer representation
     */
    public static int hash(boolean... booleans) {
        int hash = 0;
        for (boolean b : booleans) {
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
    public static <E extends Enum<E>> int hash(E... enums) {
        int[] integers = new int[enums.length];
        for (int i = 0; i < enums.length; i++) {
            integers[i] = enums[i].ordinal();
        }

        return pair(integers);
    }
}
